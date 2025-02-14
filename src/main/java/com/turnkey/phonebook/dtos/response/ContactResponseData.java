package com.turnkey.phonebook.dtos.response;

import com.turnkey.phonebook.enums.ContactGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponseData {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isFavorite;
    private String profilePicUrl;
    private String phoneNumber;
    private String mobileNumber;
    private String workPhone;
    private AddressResponse address;
    private ContactGroup contactGroup;
}
