package com.customermanagement.controller;

import com.customermanagement.model.Customer;
import com.customermanagement.service.impl.CustomerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customerInput) {
        log.info("Received customer input: {}", customerInput);
        System.out.println("Received customer input: " + customerInput);
        return new ResponseEntity<>(customerService.createCustomer(customerInput), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get customers based on dynamic filters")
    public ResponseEntity<List<Customer>> getCustomers(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "AND") String operation) {
        return ResponseEntity.ok(customerService.getCustomers(params, operation));
    }


    @PostMapping("/only-in-a")
    @Operation(summary = "Get customers only in list A")
    public ResponseEntity<List<Customer>> getCustomersOnlyInA(@RequestBody List<List<String>> lists) {
        log.info("only a {}", lists.get(0));
        return ResponseEntity.ok(customerService.getCustomersOnlyInA(lists.get(0), lists.get(1)));
    }

    @PostMapping("/only-in-b")
    @Operation(summary = "Get customers only in list B")
    public ResponseEntity<List<Customer>> getCustomersOnlyInB(@RequestBody List<List<String>> lists) {
        log.info("only b {}", lists.get(0));
        return ResponseEntity.ok(customerService.getCustomersOnlyInB(lists.get(0), lists.get(1)));
    }

    @PostMapping("/in-both")
    @Operation(summary = "Get customers in both lists")
    public ResponseEntity<List<Customer>> getCustomersInBoth(@RequestBody List<List<String>> lists) {
        return ResponseEntity.ok(customerService.getCustomersInBoth(lists.get(0), lists.get(1)));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}