package com.prowler.alertmgmt.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

@Configuration
public class ActiveMQConfig {

    @Value("${active-mq.broker-url}")
    private String brokerUrl;

    @Bean
    public ConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory activeMQConnectionFactory  = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);

        RedeliveryPolicy topicPolicy = new RedeliveryPolicy();
        topicPolicy.setInitialRedeliveryDelay(0);
        topicPolicy.setRedeliveryDelay(1000);
        topicPolicy.setUseExponentialBackOff(false);
        topicPolicy.setMaximumRedeliveries(3);

        RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(topicPolicy);

        activeMQConnectionFactory.setRedeliveryPolicy(topicPolicy);
        activeMQConnectionFactory.setRedeliveryPolicyMap(redeliveryPolicyMap);
        return activeMQConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean
    public SimpleJmsListenerContainerFactory jmsListenerContainerFactory(){
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        factory.setPubSubDomain(true);
        return factory;
    }
}