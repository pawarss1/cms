package com.customermanagement.repository;

import com.customermanagement.model.Customer;
import java.util.List;
import java.util.Map;

public interface CustomCustomerRepository {
    List<Customer> findCustomersWithFilters(Map<String, String> filters, String operation);
    List<Customer> findCustomersOnlyInA(List<String> listA, List<String> listB);
    List<Customer> findCustomersOnlyInB(List<String> listA, List<String> listB);
    List<Customer> findCustomersInBoth(List<String> listA, List<String> listB);
}