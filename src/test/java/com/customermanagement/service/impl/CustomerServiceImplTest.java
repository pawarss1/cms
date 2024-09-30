package com.customermanagement.service.impl;

import com.customermanagement.infra.broker.BrokerFactory;
import com.customermanagement.infra.broker.BrokerStrategy;
import com.customermanagement.model.Customer;
import com.customermanagement.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private BrokerFactory brokerFactory;

    @Mock
    private BrokerStrategy brokerStrategy;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        when(brokerFactory.getBroker()).thenReturn(brokerStrategy); // Mock broker behavior
    }

    @Test
    void testGetCustomersWithFilters() {
        // Mock the filters
        Map<String, String> filters = new HashMap<>();
        filters.put("firstName", "John");

        List<Customer> filteredCustomers = Arrays.asList(
                Customer.builder().customerId("1").firstName("John").lastName("Doe").build()
        );

        // Mock repository findCustomersWithFilters method
        when(customerRepository.findCustomersWithFilters(anyMap(), anyString())).thenReturn(filteredCustomers);

        // Call the getCustomers method with filters
        List<Customer> result = customerService.getCustomers(filters, "operation");

        // Assert that the result is as expected
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());

        // Verify that the repository findCustomersWithFilters method was called
        verify(customerRepository, times(1)).findCustomersWithFilters(anyMap(), anyString());
    }

    @Test
    void testGetCustomersOnlyInA() {
        List<String> listA = Arrays.asList("1", "2", "3");
        List<String> listB = Arrays.asList("2");

        List<Customer> customersOnlyInA = Arrays.asList(
                Customer.builder().customerId("1").firstName("John").lastName("Doe").build(),
                Customer.builder().customerId("3").firstName("Jane").lastName("Doe").build()
        );

        // Mock repository findCustomersOnlyInA method
        when(customerRepository.findCustomersOnlyInA(anyList(), anyList())).thenReturn(customersOnlyInA);

        // Call the getCustomersOnlyInA method
        List<Customer> result = customerService.getCustomersOnlyInA(listA, listB);

        // Assert that the result is as expected
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());

        // Verify that the repository findCustomersOnlyInA method was called
        verify(customerRepository, times(1)).findCustomersOnlyInA(anyList(), anyList());
    }

    @Test
    void testGetCustomersOnlyInB() {
        List<String> listA = Arrays.asList("1", "2");
        List<String> listB = Arrays.asList("3");

        List<Customer> customersOnlyInB = Arrays.asList(
                Customer.builder().customerId("3").firstName("John").lastName("Smith").build()
        );

        // Mock repository findCustomersOnlyInB method
        when(customerRepository.findCustomersOnlyInB(anyList(), anyList())).thenReturn(customersOnlyInB);

        // Call the getCustomersOnlyInB method
        List<Customer> result = customerService.getCustomersOnlyInB(listA, listB);

        // Assert that the result is as expected
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());

        // Verify that the repository findCustomersOnlyInB method was called
        verify(customerRepository, times(1)).findCustomersOnlyInB(anyList(), anyList());
    }

    @Test
    void testGetCustomersInBoth() {
        List<String> listA = Arrays.asList("1", "2");
        List<String> listB = Arrays.asList("2", "3");

        List<Customer> customersInBoth = Arrays.asList(
                Customer.builder().customerId("2").firstName("John").lastName("Doe").build()
        );

        // Mock repository findCustomersInBoth method
        when(customerRepository.findCustomersInBoth(anyList(), anyList())).thenReturn(customersInBoth);

        // Call the getCustomersInBoth method
        List<Customer> result = customerService.getCustomersInBoth(listA, listB);
    }
}


