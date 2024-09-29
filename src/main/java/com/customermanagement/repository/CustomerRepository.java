package com.customermanagement.repository;

import com.customermanagement.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String>, CustomCustomerRepository {
}