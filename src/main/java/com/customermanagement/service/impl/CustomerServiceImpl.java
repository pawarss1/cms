package com.customermanagement.service.impl;

import com.customermanagement.infra.broker.BrokerFactory;
import com.customermanagement.infra.broker.BrokerStrategy;
import com.customermanagement.model.Customer;
import com.customermanagement.repository.CustomerRepository;
import com.customermanagement.service.CustomerServiceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerServiceStrategy {

    private final BrokerFactory brokerFactory;
    private final MongoTemplate mongoTemplate;
    private final CustomerRepository customerRepository;

    @Value("${spring.kafka.producer.properties.customer.creation.topic}")
    private String customerCreationBrokerTopic;

    @Value("${customer.query.default-page-size:20}")
    private int defaultPageSize;

    @Value("${customer.query.default-page-start:0}")
    private int defaultPageStart;


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
        log.info("User Created: {}", savedCustomer.getCustomerId());
        // Assumption is that if producing to kafka is failing, we wont rollback the MongoDB Insertion,
        // coz it is not End User's/ Client's headache if there is issue from our infra's end
        // Ideal way is to have some mechanism to have a retry of New User Detail production onto kafka, for the failed ones
        // But in case of failure the Creation in Database shouldn't rollback.
        BrokerStrategy broker = brokerFactory.getBroker();
        broker.produce(customerCreationBrokerTopic, savedCustomer.toString());
        return savedCustomer;
    }


    public List<Customer> getCustomers(Map<String, String> params, String operation) {
        // This Get API is not paginated as of now, but it will return maximum 25 records, which is configurable
        // Pagination can be easily added going forward
        if (params.isEmpty()) {
            Pageable pageable = PageRequest.of(defaultPageStart, defaultPageSize);
            return customerRepository.findAll(pageable).getContent();
        }
        return customerRepository.findCustomersWithFilters(params, operation);
    }


    @Override
    public List<Customer> getCustomersOnlyInA(List<String> listA, List<String> listB) {
        // This Get API is not paginated as of now, but it will return maximum 25 records.
        // Pagination can be easily added going forward
        return customerRepository.findCustomersOnlyInA(listA, listB);
    }

    @Override
    public List<Customer> getCustomersOnlyInB(List<String> listA, List<String> listB) {
        // This Get API is not paginated as of now, but it will return maximum 25 records.
        // Pagination can be easily added going forward
        return customerRepository.findCustomersOnlyInB(listA, listB);
    }

    @Override
    public List<Customer> getCustomersInBoth(List<String> listA, List<String> listB) {
        // This Get API is not paginated as of now, but it will return maximum 25 records.
        // Pagination can be easily added going forward
        return customerRepository.findCustomersInBoth(listA, listB);
    }
}