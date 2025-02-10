package com.turnkey.phonebook.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressRequest {
    private String country;
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
