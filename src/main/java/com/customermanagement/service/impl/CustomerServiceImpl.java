package com.customermanagement.service.impl;

import com.customermanagement.exceptions.KafkaPublishException;
import com.customermanagement.infra.broker.BrokerFactory;
import com.customermanagement.infra.broker.BrokerStrategy;
import com.customermanagement.model.Customer;
import com.customermanagement.repository.CustomerRepository;
import com.customermanagement.service.CustomerServiceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerServiceStrategy {

    private final BrokerFactory brokerFactory;
    private final MongoTemplate mongoTemplate;
    private final CustomerRepository customerRepository;

    @Value("${spring.kafka.producer.properties.customer.creation.topic}")
    private String customerCreationTopic;


    @Transactional
    public Customer createCustomer(@Valid Customer customerInput) {
        Customer customer = Customer.builder()
                .customerId(UUID.randomUUID().toString())
                .firstName(customerInput.getFirstName())
                .lastName(customerInput.getLastName())
                .age(customerInput.getAge())
                .spendingLimit(customerInput.getSpendingLimit())
                .mobileNumber(customerInput.getMobileNumber())
                .addresses(customerInput.getAddresses())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        System.out.println("Received customer input: " + customerInput);
        log.info("User Created: {}", savedCustomer.getCustomerId());
        BrokerStrategy broker = brokerFactory.getBroker();
        broker.produce(customerCreationTopic, savedCustomer.toString());
//        Message<String> message = MessageBuilder
//                .withPayload(savedCustomer.getCustomerId())
//                .setHeader(KafkaHeaders.TOPIC, "customer-created")
//                .build();
//
//        try {
//            SendResult<String, String> sendResult = kafkaTemplate.send(message).get(5000, TimeUnit.MILLISECONDS);
//            log.info("Message sent successfully to Kafka topic: {}", sendResult.getRecordMetadata().topic());
//        } catch (Exception e) {
//            log.error("Failed to send message to Kafka", e);
//            throw new KafkaPublishException("Failed to send message to Kafka", e);
//        }
        return savedCustomer;
    }


    public List<Customer> getCustomers(Map<String, String> params, String operation) {
        if (params.isEmpty()) {
            return customerRepository.findAll();
        }
        return customerRepository.findCustomersWithFilters(params, operation);
    }


    public List<Customer> getCustomersOnlyInA(List<String> listA, List<String> listB) {
        Query query = new Query(Criteria.where("customerId").in(listA).not().in(listB));
        return mongoTemplate.find(query, Customer.class);
    }

    public List<Customer> getCustomersOnlyInB(List<String> listA, List<String> listB) {
        Query query = new Query(Criteria.where("customerId").in(listB).not().in(listA));
        return mongoTemplate.find(query, Customer.class);
    }

    public List<Customer> getCustomersInBoth(List<String> listA, List<String> listB) {
        Query query = new Query(Criteria.where("customerId").in(listA).and("customerId").in(listB));
        return mongoTemplate.find(query, Customer.class);
    }
}