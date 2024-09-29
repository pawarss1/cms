package com.customermanagement.repository;

import com.customermanagement.model.Customer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CustomCustomerRepositoryImpl implements CustomCustomerRepository {

    private final MongoTemplate mongoTemplate;

    private final Map<String, Function<String, Criteria>> queryFunctions = Map.of(
            "name", this::getNameCriteria,
            "city", this::getCityCriteria,
            "state", this::getStateCriteria
            // Add more parameters here as needed
    );

    public CustomCustomerRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Customer> findCustomersWithFilters(Map<String, String> filters, String operation) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        filters.forEach((key, value) -> {
            if (queryFunctions.containsKey(key)) {
                criteriaList.add(queryFunctions.get(key).apply(value));
            }
        });

        if (!criteriaList.isEmpty()) {
            Criteria combinedCriteria;
            if ("OR".equalsIgnoreCase(operation)) {
                combinedCriteria = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
            } else {
                combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            }
            query.addCriteria(combinedCriteria);
        }

        return mongoTemplate.find(query, Customer.class);
    }

    private Criteria getNameCriteria(String name) {
        return new Criteria().orOperator(
                Criteria.where("firstName").regex(name, "i"),
                Criteria.where("lastName").regex(name, "i")
        );
    }

    private Criteria getCityCriteria(String city) {
        return Criteria.where("addresses.city").regex(city, "i");
    }

    private Criteria getStateCriteria(String state) {
        return Criteria.where("addresses.state").regex(state, "i");
    }
}