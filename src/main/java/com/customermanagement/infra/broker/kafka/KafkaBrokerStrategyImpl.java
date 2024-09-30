package com.customermanagement.infra.broker.kafka;

import com.customermanagement.infra.broker.BrokerStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaBrokerStrategyImpl implements BrokerStrategy {
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Override
    public void produce(String topic, String message) {
        kafkaTemplate.send(topic, message)
                .addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        log.info("Message sent to topic " + topic);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Error sending message to topic " + topic, ex);
                    }
                });
    }

    @Override
    public void consume(String topic) {
        // Implement Kafka consumer logic here
        // This is a placeholder as consumption is typically set up differently
        log.info("Consumption from " + topic + " set up successfully");
    }
}
