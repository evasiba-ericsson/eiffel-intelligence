package com.ericsson.ei.handlers;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.ei.jmespath.JmesPathInterface;
import com.ericsson.ei.rules.RulesObject;
import com.ericsson.ei.waitlist.WaitListStorageHandler;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class ExternalIdRulesHandler {

    static Logger log = (Logger) LoggerFactory.getLogger(ExternalIdRulesHandler.class);

    @Autowired
    private JmesPathInterface jmesPathInterface;

    @Autowired
    private MatchIdRulesHandler matchIdRulesHandler;

    @Autowired
    private ExternalExtractionHandler externalExtractionHandler;

    @Autowired
    private WaitListStorageHandler waitListStorageHandler;

    public void setJmesPathInterface(JmesPathInterface jmesPathInterface) {
        this.jmesPathInterface = jmesPathInterface;
    }

    public void runIdRules(RulesObject rulesObject, String event) {
         JsonNode idsJsonObj = getIds(rulesObject, event);
         ArrayList<String> objects = null;
         String id;
         if (idsJsonObj != null && idsJsonObj.isArray()) {
             for (final JsonNode idJsonObj : idsJsonObj) {
                 id = idJsonObj.textValue();
                 objects = matchIdRulesHandler.fetchObjectsById(rulesObject, id);
                 for (String object:objects) {
                     externalExtractionHandler.runExtraction(rulesObject, id, event, object);
                 }
                 if (objects.size() == 0) {
                     try {
                         waitListStorageHandler.addEventToWaitList(event, rulesObject);
                     } catch (Exception e) {
                         log.info(e.getMessage(),e);
                     }
                 }
             }
         }
    }

    public JsonNode getIds(RulesObject rulesObject, String event) {
        String idRule = rulesObject.getDownstreamIdentifyRules();
        JsonNode ids = null;
        if (idRule != null && !idRule.isEmpty()) {
            try {
                ids = jmesPathInterface.runRuleOnEvent(idRule, event);
            } catch (Exception e) {
                log.info(e.getMessage(),e);
            }
        }

        return ids;
    }
}
