package com.turnkey.phonebook.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateContactRequest {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String firstName;
    private String lastName;
    private String profession;
    private String email;
    private String password;
    private String gender;
    private LocalDate dob;
    private String profilePicUrl;
    private String phoneNumber;
    private String mobileNumber;
    private String workPhone;
    private AddressRequest address;
    private List<GroupRequest> groups;
}
