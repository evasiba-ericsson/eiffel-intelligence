package com.ericsson.ei.rmqhandler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ericsson.ei.handlers.EventHandler;
import com.ericsson.ei.handlers.EventHandlerBase;
import com.ericsson.ei.handlers.ExternalEventHandler;

@Component
public class RmqHandler {

    @Value("${rabbitmq.queue.durable}")
    private Boolean queueDurable;
    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.port}")
    private Integer port;
    @Value("${rabbitmq.tls}")
    private String tlsVer;
    @Value("${rabbitmq.user}")
    private String user;
    @Value("${rabbitmq.password}")
    private String password;
    @Value("${rabbitmq.domainId}")
    private String domainId;
    @Value("${rabbitmq.componentName}")
    private String componentName;
    @Value("${rabbitmq.waitlist.queue.suffix}")
    private String waitlistSufix;
    @Value("${rabbitmq.waitlist.external.queue.suffix}")
    private String externalWaitlistSufix;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.consumerName}")
    private String consumerName;
    private RabbitTemplate rabbitTemplate;
    private RabbitTemplate externalRabbitTemplate;
    private CachingConnectionFactory factory;
    private SimpleMessageListenerContainer container;
    private SimpleMessageListenerContainer externalWaitlistContainer;
    static Logger log = (Logger) LoggerFactory.getLogger(RmqHandler.class);

    public Boolean getQueueDurable() {
        return queueDurable;
    }

    public void setQueueDurable(Boolean queueDurable) {
        this.queueDurable = queueDurable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getTlsVer() {
        return tlsVer;
    }

    public void setTlsVer(String tlsVer) {
        this.tlsVer = tlsVer;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    @Bean
    ConnectionFactory connectionFactory() {
        factory = new CachingConnectionFactory(host, port);
        factory.setPublisherConfirms(true);
        factory.setPublisherReturns(true);
        if(user != null && user.length() !=0 && password != null && password.length() !=0) {
            factory.setUsername(user);
            factory.setPassword(password);
        }
        return factory;
    }

    @Bean
    Queue queue1() {
        return new Queue(getQueueName1(), true);
    }

    @Bean
    Queue queue2() {
        return new Queue(getQueueName2(), true);
    }

    @Bean
    Queue waitlistQueue() {
        return new Queue(getWaitlistQueueName(), true);
    }

    @Bean
    Queue externalWaitlistQueue() {
        return new Queue(getExternalWaitlistQueueName(), true);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Binding binding1(Queue queue1, TopicExchange exchange) {
        return BindingBuilder.bind(queue1).to(exchange).with(mainQueueRoutingKey());
    }

    @Bean
    Binding binding2(Queue queue2, TopicExchange exchange) {
        return BindingBuilder.bind(queue2).to(exchange).with(mainQueueRoutingKey());
    }

    @Bean
    Binding waitlistBinding(Queue waitlistQueue, TopicExchange exchange) {
        return BindingBuilder.bind(waitlistQueue).to(exchange).with(waitlistQueue.getName());
    }

    @Bean
    Binding externalWaitlistBinding(Queue externalWaitlistQueue, TopicExchange exchange) {
        return BindingBuilder.bind(externalWaitlistQueue).to(exchange).with(externalWaitlistQueue.getName());
    }

    @Bean
    SimpleMessageListenerContainer bindToQueueForRecentEvents(ConnectionFactory factory, EventHandler eventHandler) {
        String queueName = getQueueName1();
        String waitListQueueName = getWaitlistQueueName();
        container = createMessageListenerContainer(eventHandler, queueName, waitListQueueName);
        return container;
    }

    @Bean
    SimpleMessageListenerContainer bindToExternalQueueForRecentEvents(ConnectionFactory factory, ExternalEventHandler eventHandler) {
        String queueName = getQueueName2();
        String waitListQueueName = getExternalWaitlistQueueName();
        externalWaitlistContainer = createMessageListenerContainer(eventHandler, queueName, waitListQueueName);
        return externalWaitlistContainer;
    }

    private SimpleMessageListenerContainer createMessageListenerContainer(EventHandlerBase eventHandler, String... queues) {
        MessageListenerAdapter listenerAdapter = new EIMessageListenerAdapter(eventHandler);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.setQueueNames(queues);
        container.setMessageListener(listenerAdapter);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

    public String getQueueName1() {
        return domainId + "." + componentName + "." + consumerName + "." + getDurableName() + ".1";
    }

    public String getQueueName2() {
        return domainId + "." + componentName + "." + consumerName + "." + getDurableName() + ".2";
    }

    public String mainQueueRoutingKey() {
//        return routingKey;
        return domainId + "." + componentName + "." + consumerName + "." + getDurableName();
    }

    public String getDurableName() {
        return queueDurable ? "durable" : "transient";
    }

    public String getWaitlistQueueName() {
        String durableName = queueDurable ? "durable" : "transient";
        return domainId + "." + componentName + "." + consumerName + "." + waitlistSufix + "." + durableName;
    }

    public String getExternalWaitlistQueueName() {
        String durableName = queueDurable ? "durable" : "transient";
        return domainId + "." + componentName + "." + consumerName + "." + externalWaitlistSufix + "." + durableName ;
    }

    @Bean
    public RabbitTemplate rabbitMqTemplate() {
        if (rabbitTemplate == null) {
            rabbitTemplate = createRMQTemplate(getWaitlistQueueName());
        }
        return rabbitTemplate;
    }

    @Bean
    public RabbitTemplate externalRabbitMqTemplate() {

        if (externalRabbitTemplate == null) {
            externalRabbitTemplate = createRMQTemplate(getExternalWaitlistQueueName());
        }
        return externalRabbitTemplate;
    }

    public RabbitTemplate createRMQTemplate(String queueName) {
        RabbitTemplate rabbitTemplate;

        if (factory != null) {
            rabbitTemplate = new RabbitTemplate(factory);
        } else {
            rabbitTemplate = new RabbitTemplate(connectionFactory());
        }

        rabbitTemplate.setExchange(exchangeName);
        rabbitTemplate.setRoutingKey(routingKey);
        rabbitTemplate.setQueue(queueName);
        rabbitTemplate.setConfirmCallback(new ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("Received confirm with result : {}", ack);
            }
        });

        return rabbitTemplate;
    }

    public void publishToWaitlistQueue(String message) {
        log.info("publishing message to wait list queue...");
        rabbitMqTemplate().convertAndSend(message);
    }

    public void publishToExternalWaitlistQueue(String message) {
        log.info("publishing message to external wait list queue...");
        externalRabbitMqTemplate().convertAndSend(message);
    }

    public void close() {
        try {
            externalWaitlistContainer.destroy();
            container.destroy();
            factory.destroy();
        } catch (Exception e) {
            log.info("exception occured while closing connections");
            log.info(e.getMessage(),e);
        }
    }
}
