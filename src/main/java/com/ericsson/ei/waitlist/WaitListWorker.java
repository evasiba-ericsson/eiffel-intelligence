package com.ericsson.ei.waitlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WaitListWorker extends WaitListWorkerBase {

    @Autowired
    private WaitListStorageHandler waitListStorageHandler;

    public WaitListStorageHandlerBase getWaitlistStorageHandler() {
        return waitListStorageHandler;
    }

    static Logger log = (Logger) LoggerFactory.getLogger(WaitListWorker.class);

    public static Logger getLog() {
        return log;
    }
}
