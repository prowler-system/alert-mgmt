package com.prowler.alertmgmt.config;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "dummy");
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        factory.setRetryTemplate(retryTemplate());
        return factory;
    }

    private RetryTemplate retryTemplate() {
        final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(2000);
        backOffPolicy.setMultiplier(2);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(getSimpleRetryPolicy());
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

    private SimpleRetryPolicy getSimpleRetryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<>();

        exceptionMap.put(RuntimeException.class, true);
        return new SimpleRetryPolicy(10, exceptionMap,true);
    }
}
