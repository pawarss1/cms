package com.customermanagement.infra.broker;

public interface BrokerStrategy {
    void produce(String topic, String message);
    void consume(String topic);
}
