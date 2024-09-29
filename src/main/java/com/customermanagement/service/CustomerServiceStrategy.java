package com.customermanagement.service;

import com.customermanagement.model.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerServiceStrategy {
    Customer createCustomer(Customer customerInput);

    List<Customer> getCustomers(Map<String, String> params, String operation);

    List<Customer> getCustomersOnlyInA(List<String> listA, List<String> listB);

    List<Customer> getCustomersOnlyInB(List<String> listA, List<String> listB);

    List<Customer> getCustomersInBoth(List<String> listA, List<String> listB);
}
