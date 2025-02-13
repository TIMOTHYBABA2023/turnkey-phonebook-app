package com.turnkey.phonebook.dtos.request;

import com.turnkey.phonebook.enums.ContactGroup;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NewContactRequest {

    @NotBlank(message = "Firstname is required!")
    @Size(min = 3, message = "Firstname must have at least 3 characters!")
    @Size(max = 20, message = "Firstname can have at most 20 characters!")
    private String firstName;

    @NotBlank(message = "Lastname is required!")
    @Size(min = 3, message = "Lastname must have at least 3 characters!")
    @Size(max = 20, message = "Lastname can have at most 20 characters!")
    private String lastName;

    @Email(message = "Email is not in valid format!")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Phone number is required!")
    @Pattern(
            regexp = "^(0?[0-9]{10})$",
            message = "Phone number must be 10 or 11 digits and may optionally start with 0."
    )
    private String phoneNumber;
    private String profilePicUrl;
    private String mobileNumber;
    private String workPhone;
    private AddressRequest address;
    private ContactGroup contactGroup;

//    @NotBlank(message = "Phone number is required!")
//    @Pattern(
//            regexp = "^\\+\\d{1,4}[0-9]{7,15}$",
//            message = "Phone number must start with a valid country code and contain 7 to 15 digits."
//    )
//    private String phoneNumber;

}
