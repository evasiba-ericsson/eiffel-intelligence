package com.ericsson.ei.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.scheduling.annotation.Async;

import com.rabbitmq.client.Channel;

public class EventHandlerBase {
    private static Logger log = LoggerFactory.getLogger(EventHandler.class);

    public static Logger getLog() {
        return log;
    }

    public void eventReceived(String event) {
    }

    public void eventReceived(byte[] message) {
        getLog().info("Thread id " + Thread.currentThread().getId() + " spawned");
        String actualMessage = new String(message);
        getLog().info("Event received <" + actualMessage + ">");
        eventReceived(actualMessage);
        //	        if (System.getProperty("flow.test") == "true") {
        //	            String countStr = System.getProperty("eiffel.intelligence.processedEventsCount");
        //	            int count = Integer.parseInt(countStr);
        //	            count++;
        //	            System.setProperty("eiffel.intelligence.processedEventsCount", "" + count);
        //	        }
    }

    @Async
    public void onMessage(Message message, Channel channel) throws Exception {
        byte[] messageBody = message.getBody();
        eventReceived(messageBody);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, false);
    }
}
