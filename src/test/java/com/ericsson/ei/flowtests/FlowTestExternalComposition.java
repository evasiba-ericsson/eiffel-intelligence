package com.ericsson.ei.flowtests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ericsson.ei.waitlist.ExternalWaitListStorageHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FlowTestExternalComposition extends FlowTestBase {

     private static Logger log = LoggerFactory.getLogger(FlowTest.class);
     static protected String inputFilePath = "src/test/resources/aggregatedExternalComposition.json";

     @Autowired
     ExternalWaitListStorageHandler externalWaitListStorageHandler;

        protected ArrayList<String> getEventNamesToSend() {
             ArrayList<String> eventNames = new ArrayList<>();
             eventNames.add("event_EiffelArtifactCreatedEvent_3");
             eventNames.add("event_EiffelCompositionDefinedEvent_4");
             eventNames.add("event_EiffelArtifactCreatedEvent_4");
             eventNames.add("event_EiffelCompositionDefinedEvent_5");
             eventNames.add("event_EiffelArtifactCreatedEvent_5");
             return eventNames;
        }

        protected void checkResult() {
            try {
                String expectedDocuments = FileUtils.readFileToString(new File(inputFilePath));
                ObjectMapper objectmapper = new ObjectMapper();
                JsonNode expectedJsons = objectmapper.readTree(expectedDocuments);
                JsonNode expectedJson1 = expectedJsons.get(0);
                JsonNode expectedJson2 = expectedJsons.get(1);
                JsonNode expectedJson3 = expectedJsons.get(2);
                String document1 = objectHandler.findObjectById("6acc3c87-75e0-4b6d-88f5-b1a5d4e62b43");
                String document2 = objectHandler.findObjectById("cfce572b-c3j4-441e-abc9-b62f48080ca2");
                String document3 = objectHandler.findObjectById("cfre572b-c3j4-4d1e-ajc9-b62f45080ca2");
                JsonNode actualJson1 = objectmapper.readTree(document1);
                JsonNode actualJson2 = objectmapper.readTree(document2);
                JsonNode actualJson3 = objectmapper.readTree(document3);
                String actualJsonStr1 = actualJson1.get("aggregatedObject").asText();
                String actualJsonStr2 = actualJson2.get("aggregatedObject").asText();
                String actualJsonStr3 = actualJson3.get("aggregatedObject").asText();
                assertEquals(expectedJson1.toString().length(), actualJsonStr1.length());
                assertEquals(expectedJson2.toString().length(), actualJsonStr2.length());
                assertEquals(expectedJson3.toString().length(), actualJsonStr3.length());
                int i = 0;
            } catch (Exception e) {
                log.info(e.getMessage(),e);
            }

        }

        protected void waitForEventsToBeProcessed(int eventsCount) {
            super.waitForEventsToBeProcessed(eventsCount);
            int waitlistCount = externalWaitListStorageHandler.waitListCount();
            while (waitlistCount > 1) {
                waitlistCount = externalWaitListStorageHandler.waitListCount();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                log.info(e.getMessage(),e);
            }

        }
}
