package com.ericsson.ei.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ericsson.ei.rules.RulesHandler;
import com.ericsson.ei.rules.RulesObject;

import com.rabbitmq.client.Channel;

import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

@Component
public class EventHandler extends EventHandlerBase {

    private static Logger log = LoggerFactory.getLogger(EventHandler.class);

    public static Logger getLog() {
        return log;
    }

    @Autowired
    RulesHandler rulesHandler;

    @Autowired
    IdRulesHandler idRulesHandler;

    public void eventReceived(String event) {
        RulesObject eventRules = rulesHandler.getRulesForEvent(event);
        idRulesHandler.runIdRules(eventRules, event);
    }
}
