package com.customermanagement.model;

import com.customermanagement.enums.AddressType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Address {
    @NotNull(message = "Address type is mandatory")
    private AddressType type;

    @NotBlank(message = "Street is mandatory")
    private String street;

    private String address2;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "State is mandatory")
    private String state;

    @NotBlank(message = "Zip code is mandatory")
    private String zipCode;
}