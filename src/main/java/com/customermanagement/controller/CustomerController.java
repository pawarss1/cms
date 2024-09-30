package com.customermanagement.controller;

import com.customermanagement.model.Customer;
import com.customermanagement.service.impl.CustomerServiceImpl;
import com.customermanagement.validation.ListOfTwoLists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Customer.class),
                    examples = @ExampleObject(
                            value = "{\n" +
                                    "  \"firstName\": \"John\",\n" +
                                    "  \"lastName\": \"Doe\",\n" +
                                    "  \"age\": 30,\n" +
                                    "  \"spendingLimit\": 5000.00,\n" +
                                    "  \"mobileNumber\": \"+1234567890\",\n" +
                                    "  \"addresses\": [\n" +
                                    "    {\n" +
                                    "      \"type\": \"Home\",\n" +
                                    "      \"street\": \"123 Main St\",\n" +
                                    "      \"city\": \"Anytown\",\n" +
                                    "      \"state\": \"CA\",\n" +
                                    "      \"zipCode\": \"12345\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"
                    )
            )
    )
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customerInput) {
        log.info("Received request to create customer: firstName={}, lastName={}", customerInput.getFirstName(), customerInput.getLastName());
        Customer createdCustomer = customerService.createCustomer(customerInput);
        log.info("Customer created successfully: id={}", createdCustomer.getCustomerId());
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get customers based on dynamic filters",
            parameters = {
                    @Parameter(
                            name = "name",
                            description = "Customer name",
                            in = ParameterIn.QUERY,
                            example = "test"
                    ),
                    @Parameter(
                            name = "city",
                            description = "City of the customer",
                            in = ParameterIn.QUERY,
                            example = "bng"
                    ),
                    @Parameter(
                            name = "state",
                            description = "State of the customer",
                            in = ParameterIn.QUERY,
                            example = "la"
                    )
            }
    )
    public ResponseEntity<List<Customer>> getCustomers(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "AND") String operation) {
        log.info("Received request to get customers with filters: params={}, operation={}", params, operation);
        List<Customer> customers = customerService.getCustomers(params, operation);
        log.info("Retrieved {} customers matching the criteria", customers.size());
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/only-in-a")
    @Operation(summary = "Get customers only in list A")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class),
                    examples = @ExampleObject(
                            value = "[\n" +
                                    "  [\"d6f8202d-5dd0-4887-a5ce-da30325689f5\"],\n" +
                                    "  [\"84365e2c-41fe-40b0-b26a-e85cd48ecf99\", \"de326f2e-b2fa-42a9-a9fe-300a62516bb6\", \"d6f8202d-5dd0-4887-a5ce-da30325689f5\"]\n" +
                                    "]"
                    )
            )
    )
    public ResponseEntity<List<Customer>> getCustomersOnlyInA(@Valid @ListOfTwoLists @RequestBody List<List<String>> lists) {
        log.info("Received request to get customers only in list A: listA size={}, listB size={}", lists.get(0).size(), lists.get(1).size());
        List<Customer> customers = customerService.getCustomersOnlyInA(lists.get(0), lists.get(1));
        log.info("Retrieved {} customers only in list A", customers.size());
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/only-in-b")
    @Operation(summary = "Get customers only in list B")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class),
                    examples = @ExampleObject(
                            value = "[\n" +
                                    "  [\"d6f8202d-5dd0-4887-a5ce-da30325689f5\"],\n" +
                                    "  [\"84365e2c-41fe-40b0-b26a-e85cd48ecf99\", \"de326f2e-b2fa-42a9-a9fe-300a62516bb6\", \"d6f8202d-5dd0-4887-a5ce-da30325689f5\"]\n" +
                                    "]"
                    )
            )
    )
    public ResponseEntity<List<Customer>> getCustomersOnlyInB(@Valid @ListOfTwoLists @RequestBody List<List<String>> lists) {
        log.info("Received request to get customers only in list B: listA size={}, listB size={}", lists.get(0).size(), lists.get(1).size());
        List<Customer> customers = customerService.getCustomersOnlyInB(lists.get(0), lists.get(1));
        log.info("Retrieved {} customers only in list B", customers.size());
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/in-both")
    @Operation(summary = "Get customers in both lists")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class),
                    examples = @ExampleObject(
                            value = "[\n" +
                                    "  [\"d6f8202d-5dd0-4887-a5ce-da30325689f5\"],\n" +
                                    "  [\"84365e2c-41fe-40b0-b26a-e85cd48ecf99\", \"de326f2e-b2fa-42a9-a9fe-300a62516bb6\", \"d6f8202d-5dd0-4887-a5ce-da30325689f5\"]\n" +
                                    "]"
                    )
            )
    )
    public ResponseEntity<List<Customer>> getCustomersInBoth(@Valid  @ListOfTwoLists @RequestBody List<List<String>> lists) {
        log.info("Received request to get customers in both lists: listA size={}, listB size={}", lists.get(0).size(), lists.get(1).size());
        List<Customer> customers = customerService.getCustomersInBoth(lists.get(0), lists.get(1));
        log.info("Retrieved {} customers in both lists", customers.size());
        return ResponseEntity.ok(customers);
    }
}