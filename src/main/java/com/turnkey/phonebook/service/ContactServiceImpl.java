package com.turnkey.phonebook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.turnkey.phonebook.enitity.Address;
import com.turnkey.phonebook.enitity.Contact;
import com.turnkey.phonebook.enitity.Group;
import com.turnkey.phonebook.exceptions.APIException;
import com.turnkey.phonebook.repository.AddressRepository;
import com.turnkey.phonebook.repository.ContactRepository;
import com.turnkey.phonebook.repository.GroupRepository;
import com.turnkey.phonebook.request.NewContactRequest;
import com.turnkey.phonebook.request.UpdateContactRequest;
import com.turnkey.phonebook.response.APIResponse;
import com.turnkey.phonebook.response.ContactResponseData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final AddressRepository addressRepository;
    private final GroupRepository groupRepository;

    @Override
    public APIResponse<ContactResponseData> createContact(NewContactRequest contact) {
        Optional<Contact> optionalContact = contactRepository.findContactByPhoneNumber(contact.getPhoneNumber());
        if (optionalContact.isPresent()) {
            throw APIException.builder()
                    .message("Contact exist already")
                    .statusCode(409)
                    .build();
        }
        Contact newContact = mapper.map(contact, Contact.class);
        if(contact.getAddress() != null){
            Address newAddress = mapper.map(contact.getAddress(), Address.class);
            Address savedAaddress = addressRepository.save(newAddress);
            newContact.setAddress(savedAaddress);
        }
        if(contact.getGroups() != null && !contact.getGroups().isEmpty()){
            List<Group> groupList = contact.getGroups()
                     .stream()
                     .map(groupRequest -> groupRepository.findGroupByName(groupRequest.getName())
                             .orElse(null))
                    .filter(Objects::nonNull)
                     .collect(Collectors.toList());
            newContact.setGroups(groupList);
        }
        Contact savedContact = contactRepository.save(newContact);
        return APIResponse.<ContactResponseData>builder()
                .data(mapper.map(savedContact, ContactResponseData.class))
                .statusCode(201)
                .build();
    }


    @Override
    public APIResponse<Page<ContactResponseData>> getAllContacts(Integer page, Integer size) {

        Page<ContactResponseData> contacts = contactRepository.findAllBy(PageRequest.of(page, size))
                .map(contact -> mapper.map(contact, ContactResponseData.class));

        return APIResponse.<Page<ContactResponseData>>builder()
               .data(contacts)
               .statusCode(200)
               .build();
    }

    @Override
    public APIResponse<ContactResponseData> getContactById(Long id) {
        return contactRepository.findById(id)
               .map(contact -> mapper.map(contact, ContactResponseData.class))
               .map(contactResponseData -> APIResponse.<ContactResponseData>builder()
                      .data(contactResponseData)
                      .statusCode(200)
                      .build())
               .orElseThrow(()-> APIException.builder()
                       .message("Contact not found")
                       .statusCode(404)
                       .build());
    }

    @Override
    public APIResponse<ContactResponseData> updateContact(Long id, UpdateContactRequest updatedContact) {
        return contactRepository.findById(id)
               .map(contact -> {
                    contact = mapper.map(updatedContact, contact.getClass());
                    Contact updateContact =  contactRepository.save(contact);
                    ContactResponseData updatedContactResponseData = mapper.map(updateContact, ContactResponseData.class);
                    return APIResponse.<ContactResponseData>builder()
                             .data(updatedContactResponseData)
                             .statusCode(200)
                             .build();
                })
               .orElseThrow(() -> APIException.builder()
                       .message("Contact not found")
                       .statusCode(404)
                       .build());
    }

    @Override
    public APIResponse<Void> deleteContact(Long id) {
        return contactRepository.findById(id)
               .map(contact -> {
                    contactRepository.delete(contact);
                    return APIResponse.<Void>builder()
                             .statusCode(204)
                             .build();
                })
               .orElseThrow(() -> APIException.builder()
                       .message("Contact not found")
                       .statusCode(404)
                       .build());
    }

    @Override
    public APIResponse<Void> bulkDeleteContacts(List<Long> contactIds) {
        contactRepository.deleteAllByIdIsIn(contactIds);
        return APIResponse.<Void>builder()
               .statusCode(204)
               .build();
    }

    @Override
    public APIResponse<Page<ContactResponseData>> searchContacts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Specification<Contact> spec = ContactSpecification.searchByKeyword(keyword);

        Page<Contact> contacts = contactRepository.findAll(spec, pageable);

        Page<ContactResponseData> responseData = contacts.map(contact -> mapper.map(contact, ContactResponseData.class));

        return APIResponse.<Page<ContactResponseData>>builder()
                .data(responseData)
                .statusCode(200)
                .build();
    }

    @Override
    public APIResponse<Page<ContactResponseData>> getFavoriteContacts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Contact> favoriteContacts = contactRepository.findContactsByFavorite(true, pageable);

        Page<ContactResponseData> responseData = favoriteContacts.map(contact -> mapper.map(contact, ContactResponseData.class));

        return APIResponse.<Page<ContactResponseData>>builder()
                .data(responseData)
                .statusCode(200)
                .build();
    }

    @Override
    public void importContactsFromCSV(MultipartFile file) throws Exception {
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();
            List<Contact> contacts = new ArrayList<>();

            for (int i = 1; i < records.size(); i++) { // Skipping header
                String[] record = records.get(i);
                Contact contact = Contact.builder()
                        .firstName(record[0])
                        .lastName(record[1])
                        .email(record[2])
                        .phoneNumber(record[3])
                        .profession(record[4])
                        .build();
                contacts.add(contact);
            }
            contactRepository.saveAll(contacts);
        }
    }

    @Override
    public void exportContactsToCSV(Writer writer) throws IOException {
        List<Contact> contacts = contactRepository.findAll();
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(new String[]{"First Name", "Last Name", "Email", "Phone", "Profession"});

            for (Contact contact : contacts) {
                csvWriter.writeNext(new String[]{
                        contact.getFirstName(),
                        contact.getLastName(),
                        contact.getEmail(),
                        contact.getPhoneNumber(),
                        contact.getProfession()
                });
            }
        }
    }

}
