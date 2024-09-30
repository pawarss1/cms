package com.customermanagement.infra.broker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerFactory {
    // Broker Factory is used to return instance of the current broker being used
    // For now its Kafka, in future if we decide to change the broker, there will be no change in the Service code.
    // Just return new broker instance from here
    private final BrokerStrategy KafkaBrokerStrategyImpl;

    public BrokerStrategy getBroker() {
        return KafkaBrokerStrategyImpl;
    }
}