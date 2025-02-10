package com.turnkey.phonebook.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponseData {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
    private AddressResponse address;
    private List<GroupResponse> groups;
}
