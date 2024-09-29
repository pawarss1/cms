package com.customermanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer {
    @Id
    private String customerId;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Customer number is mandatory")
    private String customerNumber;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 150, message = "Age must be less than 150")
    private Integer age;

    @PositiveOrZero(message = "Spending limit must be positive or zero")
    private Double spendingLimit;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Mobile number must be a valid phone number")
    private String mobileNumber;

    @NotEmpty(message = "At least one address is required")
    @Valid
    private List<Address> addresses;
}