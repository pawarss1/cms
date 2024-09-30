package com.customermanagement.repository;

import com.customermanagement.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class CustomCustomerRepositoryImpl implements CustomCustomerRepository {

    private final MongoTemplate mongoTemplate;

    @Value("${customer.query.default-page-size:20}")
    private int defaultPageSize;

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
        // There is no tight coupling between the filter fields, as we have kept it dynamic in a Map,
        // so if new field is to be added for filtering, just add a new field in the Map, without changing anything in Service layer
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
        query.limit(defaultPageSize);
        return mongoTemplate.find(query, Customer.class);
    }

    @Override
    public List<Customer> findCustomersOnlyInA(List<String> listA, List<String> listB) {
        return findCustomersInOneListButNotOther(listA, listB, "A");
    }

    @Override
    public List<Customer> findCustomersOnlyInB(List<String> listA, List<String> listB) {
        return findCustomersInOneListButNotOther(listB, listA, "B");
    }

    private List<Customer> findCustomersInOneListButNotOther(List<String> includeList, List<String> excludeList, String listName) {
        log.info("Finding customers only in {}. Include list size: {}, Exclude list size: {}",
                listName, includeList.size(), excludeList.size());
        log.info("Include List: {}", includeList);
        log.info("Exclude List: {}", excludeList);

        Set<String> onlyInList = new HashSet<>(includeList);
        onlyInList.removeAll(excludeList);

        Query query = new Query(Criteria.where("customerId").in(onlyInList));
        query.limit(defaultPageSize);
        List<Customer> result = mongoTemplate.find(query, Customer.class);

        log.info("Found {} customers only in {}", result.size(), listName);
        log.info("Customers found only in {}: {}", listName,
                result.stream().map(Customer::getCustomerId).collect(Collectors.toList()));

        return result;
    }


    @Override
    public List<Customer> findCustomersInBoth(List<String> listA, List<String> listB) {
        // This can be made extensible by taking List<List<String>> to make it dynamic and not restrict to just 2 Lists.
        log.info("DB_INTERSECTION_FIND_ListA {}", listA);
        log.info("DB_INTERSECTION_FIND_ListB {}", listB);
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("customerId").in(listA),
                Criteria.where("customerId").in(listB)
        ));
        query.limit(defaultPageSize);
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