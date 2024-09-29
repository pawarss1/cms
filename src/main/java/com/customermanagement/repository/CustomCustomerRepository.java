package com.customermanagement.repository;

import com.customermanagement.model.Customer;
import java.util.List;
import java.util.Map;

public interface CustomCustomerRepository {
    List<Customer> findCustomersWithFilters(Map<String, String> filters, String operation);
}