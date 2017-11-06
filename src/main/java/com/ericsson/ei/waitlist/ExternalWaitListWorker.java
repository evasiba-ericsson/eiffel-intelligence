package com.ericsson.ei.waitlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.ei.rmqhandler.RmqHandler;
import com.ericsson.ei.rules.RulesObject;

@Component
public class ExternalWaitListWorker extends WaitListWorkerBase {

    @Autowired
    private ExternalWaitListStorageHandler waitListStorageHandler;

    public WaitListStorageHandlerBase getWaitlistStorageHandler() {
        return waitListStorageHandler;
    }

    public void publishToWaitList(RmqHandler rmqHandler, String event) {
        rmqHandler.publishToExternalWaitlistQueue(event);
    }

    static Logger log = (Logger) LoggerFactory.getLogger(ExternalWaitListWorker.class);

    public static Logger getLog() {
        return log;
    }

    public String getIdentifyRules(RulesObject rulesObject) {
        return rulesObject.getDownstreamIdentifyRules();
    }
}
