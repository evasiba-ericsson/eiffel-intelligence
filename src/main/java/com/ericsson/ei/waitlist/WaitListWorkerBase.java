package com.ericsson.ei.waitlist;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.ericsson.ei.handlers.MatchIdRulesHandler;
import com.ericsson.ei.jmespath.JmesPathInterface;
import com.ericsson.ei.rmqhandler.RmqHandler;
import com.ericsson.ei.rules.RulesHandler;
import com.ericsson.ei.rules.RulesObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class WaitListWorkerBase {

    static Logger log = (Logger) LoggerFactory.getLogger(WaitListWorkerBase.class);

    @Autowired
    private RmqHandler rmqHandler;

    @Autowired
    private RulesHandler rulesHandler;

    @Autowired
    private JmesPathInterface jmesPathInterface;

    @Autowired
    private MatchIdRulesHandler matchIdRulesHandler;

    public static Logger getLog() {
        return log;
    }

    public WaitListStorageHandlerBase getWaitlistStorageHandler() {
        return null;
    }

    public void publishToWaitList(RmqHandler rmqHandler, String event) {
        rmqHandler.publishToWaitlistQueue(event);
    }

    @Scheduled(initialDelayString = "${waitlist.initialDelayResend}", fixedRateString = "${waitlist.fixedRateResend}")
    public void run() {
        RulesObject rulesObject = null;
        ArrayList<String> documents = getWaitlistStorageHandler().getWaitList();
        for (String document : documents) {
            DBObject dbObject = (DBObject) JSON.parse(document);
            String event = dbObject.get("Event").toString();
            rulesObject = rulesHandler.getRulesForEvent(event);
            String idRule = getIdentifyRules(rulesObject);
            if (idRule != null && !idRule.isEmpty()) {
                JsonNode ids = jmesPathInterface.runRuleOnEvent(idRule, event);
                if (ids.isArray()) {
                    for (final JsonNode idJsonObj : ids) {
                        ArrayList<String> objects = matchIdRulesHandler.fetchObjectsById(rulesObject, idJsonObj.textValue());
                        if (objects.size() > 0) {
                            publishToWaitList(rmqHandler, event);
                            getWaitlistStorageHandler().dropDocumentFromWaitList(document);
                        }
                    }
                }
            }
        }
    }

    public String getIdentifyRules(RulesObject rulesObject) {
        return rulesObject.getIdentifyRules();
    }
}
