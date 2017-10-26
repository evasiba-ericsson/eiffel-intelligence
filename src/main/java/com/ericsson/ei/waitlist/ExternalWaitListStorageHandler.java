package com.ericsson.ei.waitlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExternalWaitListStorageHandler extends WaitListStorageHandlerBase {

    @Value("${external.waitlist.collection.name}") private String collectionName;

    static Logger log = (Logger) LoggerFactory.getLogger(ExternalWaitListStorageHandler.class);

    public static Logger getLog() {
        return log;
    }

    public String getCollectionName() {
        return collectionName;
    }

    private void updateTestEventCount(boolean increase) {
        if (System.getProperty("flow.test") == "true") {
            String countStr = System.getProperty("eiffel.intelligence.external.waitListEventsCount");
            int count = Integer.parseInt(countStr);
            if (increase) {
                count++;
            } else {
                count--;
            }
            System.setProperty("eiffel.intelligence.external.waitListEventsCount", "" + count);
        }
    }
}