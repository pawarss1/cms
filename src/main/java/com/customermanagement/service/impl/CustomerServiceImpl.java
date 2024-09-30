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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerServiceStrategy {
    // Error handling for all service module is taken by Global Exception Handler, using ControllerAdvise
    private final BrokerFactory brokerFactory;
    private final CustomerRepository customerRepository;

    @Value("${spring.kafka.producer.properties.customer.creation.topic}")
    private String customerCreationBrokerTopic;

    @Value("${customer.query.default-page-size:20}")
    private int defaultPageSize;

    @Value("${customer.query.default-page-start:0}")
    private int defaultPageStart;

    /**
     * Create a new customer, save it in MongoDB, and produce the event to Kafka.
     */
    public Customer createCustomer(@Valid Customer customerInput) {
        log.info("Starting customer creation process for customer with firstName: {}, lastName: {}", customerInput.getFirstName(), customerInput.getLastName());
        Customer customer = Customer.builder()
                .customerId(UUID.randomUUID().toString())
                .firstName(customerInput.getFirstName())
                .lastName(customerInput.getLastName())
                .age(customerInput.getAge())
                .spendingLimit(customerInput.getSpendingLimit())
                .mobileNumber(customerInput.getMobileNumber())
                .addresses(customerInput.getAddresses())
                .build();
        log.debug("Generated new customer with ID: {}", customer.getCustomerId());

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer saved to MongoDB with ID: {}", savedCustomer.getCustomerId());
        produceToBrokerAsync(savedCustomer);
        log.info("PUBLISHING_TO_BROKER_IN_PROGRESS");
        return savedCustomer;
    }

    @Async
    private void produceToBrokerAsync(Customer customer) {
        // Assumption is that if producing to kafka is failing, we wont rollback the MongoDB Insertion,
        // coz it is not End User's/ Client's headache if there is issue from our infra's end
        // Ideal way is to have some mechanism to have a retry of New User Detail production onto kafka, for the failed ones
        // But in case of failure the Creation in Database shouldn't rollback.
        BrokerStrategy broker = brokerFactory.getBroker();
        broker.produce(customerCreationBrokerTopic, customer.toString());
    }

    /**
     * Get customers with optional filters. Defaults to 25 records with pagination.
     */
    public List<Customer> getCustomers(Map<String, String> params, String operation) {
        // This Get API is not paginated as of now, but it will return maximum 25 records, which is configurable
        // Pagination can be easily added going forward
        log.info("Fetching customers with operation: {}", operation);

        if (params.isEmpty()) {
            log.debug("No filters provided. Fetching customers with default pagination. Page size: {}", defaultPageSize);
            Pageable pageable = PageRequest.of(defaultPageStart, defaultPageSize);
            return customerRepository.findAll(pageable).getContent();
        }
        // If filters are provided, apply them
        log.debug("Filters provided. Fetching customers with default pagination. Page size: {}", defaultPageSize);
        return customerRepository.findCustomersWithFilters(params, operation);
    }


    /**
     * Get customers present in  list A and NOT list B.
     */
    @Override
    public List<Customer> getCustomersOnlyInA(List<String> listA, List<String> listB) {
        // This Get API is not paginated as of now, but it will return maximum 25 records.
        // Pagination can be easily added going forward
        log.info("Fetching customers present only in list A and not in list B");
        return customerRepository.findCustomersOnlyInA(listA, listB);
    }

    /**
     * Get customers present in  list B and NOT list A.
     */
    @Override
    public List<Customer> getCustomersOnlyInB(List<String> listA, List<String> listB) {
        // This Get API is not paginated as of now, but it will return maximum 25 records.
        // Pagination can be easily added going forward
        log.info("Fetching customers present only in list B and not in list A");
        return customerRepository.findCustomersOnlyInB(listA, listB);
    }

    /**
     * Get customers present in both list A and list B.
     */
    @Override
    public List<Customer> getCustomersInBoth(List<String> listA, List<String> listB) {
        // This Get API is not paginated as of now, but it will return maximum 25 records.
        // Pagination can be easily added going forward
        log.info("Fetching customers present in both list A and list B");
        return customerRepository.findCustomersInBoth(listA, listB);
    }
}