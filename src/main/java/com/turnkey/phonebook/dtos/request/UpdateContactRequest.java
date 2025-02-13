package com.turnkey.phonebook.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.turnkey.phonebook.enums.ContactGroup;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateContactRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicUrl;
    private String phoneNumber;
    private String mobileNumber;
    private String workPhone;
    private AddressRequest address;
    private ContactGroup contactGroup;
}
