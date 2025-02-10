package com.turnkey.phonebook.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewContactRequest {

    private String profession;
    private String gender;
    private String profilePicUrl;
    private String mobileNumber;
    private String workPhone;
    private AddressRequest address;
    private List<GroupRequest> groups;

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

    @NotBlank(message = "Password is required!")
    @NotBlank(message = "Password is required!")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)."
    )
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotBlank(message = "Phone number is required!")
    @Pattern(
            regexp = "^\\+?[0-9]{11}$",
            message = "Phone number must be between 11 digits."
    )
    private String phoneNumber;

//    @NotBlank(message = "Phone number is required!")
//    @Pattern(
//            regexp = "^\\+\\d{1,4}[0-9]{7,15}$",
//            message = "Phone number must start with a valid country code and contain 7 to 15 digits."
//    )
//    private String phoneNumber;

}
