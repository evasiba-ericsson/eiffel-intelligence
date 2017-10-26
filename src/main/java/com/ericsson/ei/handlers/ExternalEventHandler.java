package com.ericsson.ei.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ericsson.ei.rules.RulesHandler;
import com.ericsson.ei.rules.RulesObject;

public class ExternalEventHandler extends EventHandlerBase {
    private static Logger log = LoggerFactory.getLogger(EventHandler.class);

    public static Logger getLog() {
        return log;
    }

    @Autowired
    RulesHandler rulesHandler;

    @Autowired
    ExternalIdRulesHandler externalIdRulesHandler;

    public void eventReceived(String event) {
        RulesObject eventRules = rulesHandler.getRulesForEvent(event);
        externalIdRulesHandler.runIdRules(eventRules, event);
    }
}
