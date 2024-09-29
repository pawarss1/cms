package com.customermanagement.service;

import com.customermanagement.model.Customer;
import com.customermanagement.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public Customer createCustomer(@Valid Customer customerInput) {
        Customer customer = Customer.builder()
                .customerId(UUID.randomUUID().toString())
                .firstName(customerInput.getFirstName())
                .lastName(customerInput.getLastName())
                .customerNumber(customerInput.getCustomerNumber())
                .age(customerInput.getAge())
                .spendingLimit(customerInput.getSpendingLimit())
                .mobileNumber(customerInput.getMobileNumber())
                .addresses(customerInput.getAddresses())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("User Created: {}", savedCustomer.getCustomerId());
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send("customer-created", savedCustomer.getCustomerId());

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Message sent successfully to Kafka topic: {}", result.getRecordMetadata().topic());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Failed to send message to Kafka", ex);
                throw new RuntimeException("Failed to send message to Kafka", ex);
            }
        });

        return savedCustomer;
    }


    public List<Customer> getCustomers(String name, String city, String state) {
        if (name == null && city == null && state == null) {
            return customerRepository.findAll();
        }
        return customerRepository.findByNameOrCityOrState(name, city, state);
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