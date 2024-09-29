package com.customermanagement.repository;

import com.customermanagement.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    @Query("{'$or': [{'firstName': {$regex: ?0, $options: 'i'}}, {'lastName': {$regex: ?0, $options: 'i'}}, {'addresses.city': {$regex: ?1, $options: 'i'}}, {'addresses.state': {$regex: ?2, $options: 'i'}}]}")
    List<Customer> findByNameOrCityOrState(String name, String city, String state);
}