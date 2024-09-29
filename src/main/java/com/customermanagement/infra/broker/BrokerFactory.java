package com.customermanagement.infra.broker;

import com.customermanagement.infra.broker.kafka.KafkaBrokerStrategyImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerFactory {
    private final BrokerStrategy KafkaBrokerStrategyImpl;

    public BrokerStrategy getBroker() {
        return KafkaBrokerStrategyImpl;
    }
}