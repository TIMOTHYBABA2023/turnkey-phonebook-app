package com.turnkey.phonebook.service.ServiceImpl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.turnkey.phonebook.dtos.request.BulkDeleteRequest;
import com.turnkey.phonebook.enitity.Address;
import com.turnkey.phonebook.enitity.Contact;
import com.turnkey.phonebook.exceptions.APIException;
import com.turnkey.phonebook.repository.AddressRepository;
import com.turnkey.phonebook.repository.ContactRepository;
import com.turnkey.phonebook.dtos.request.NewContactRequest;
import com.turnkey.phonebook.dtos.request.UpdateContactRequest;
import com.turnkey.phonebook.dtos.response.APIResponse;
import com.turnkey.phonebook.dtos.response.ContactResponseData;
import com.turnkey.phonebook.service.ContactService;
import com.turnkey.phonebook.service.ContactSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ModelMapper mapper;
    private final AddressRepository addressRepository;

    @Override
    public APIResponse<ContactResponseData> createContact(NewContactRequest newContactRequest) {
        final Logger logger = LoggerFactory.getLogger(ContactService.class);

        logger.info("Received contact: " + newContactRequest);
        Optional<Contact> optionalContact = contactRepository.findContactByPhoneNumber(newContactRequest.getPhoneNumber());
        if (optionalContact.isPresent()) {
            throw APIException.builder()
                    .message("Contact already exists")
                    .statusCode(409)
                    .build();
        }
        Contact newContact = mapper.map(newContactRequest, Contact.class);
        logger.info("Mapped contact: " + newContact);

        Address newAddress = new Address();
        newAddress.setCreatedAt(LocalDateTime.now());
        newAddress.setModifiedAt(LocalDateTime.now());
        if (newContactRequest.getAddress() != null) {
            logger.info("Received address: " + newContactRequest.getAddress());
            newAddress = mapper.map(newContactRequest.getAddress(), Address.class);
        }
        // Set the Address in the Contact entity
        newContact.setAddress(newAddress);
        newContact.setCreatedAt(LocalDateTime.now());
        newContact.setModifiedAt(LocalDateTime.now());

        Contact savedContact = contactRepository.save(newContact);
        logger.info("Saved contact: " + savedContact);

        ContactResponseData responseData = mapper.map(savedContact, ContactResponseData.class);

        return APIResponse.<ContactResponseData>builder()
                .data(responseData)
                .statusCode(201)
                .success(true)
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

    @Transactional
    @Override
    public APIResponse<ContactResponseData> updateContact(Long id, UpdateContactRequest updateContactRequest) {
        log.info("Attempting to update contact with ID: {}", id);

        return contactRepository.findById(id)
                .map(contact -> {
                    log.info("Existing Contact: {}", contact);

                    // Update contact fields
                    mapper.map(updateContactRequest, contact);
                    contact.setModifiedAt(LocalDateTime.now());

                    // Ensure we don't replace the address ID
                    if (updateContactRequest.getAddress() != null) {
                        Address existingAddress = contact.getAddress();

                        if (existingAddress == null) {
                            log.info("No existing address found, creating a new one...");
                            existingAddress = new Address();
                            contact.setAddress(existingAddress);
                        }

                        log.info("Before update: Address ID = {}", existingAddress.getId());

                        // Ensure the address ID remains unchanged
                        Long originalAddressId = existingAddress.getId();

                        // Map only fields, not ID
                        existingAddress.setStreet(updateContactRequest.getAddress().getStreet());
                        existingAddress.setCity(updateContactRequest.getAddress().getCity());
                        existingAddress.setState(updateContactRequest.getAddress().getState());
                        existingAddress.setZipCode(updateContactRequest.getAddress().getZipCode());
                        existingAddress.setCountry(updateContactRequest.getAddress().getCountry());
                        existingAddress.setModifiedAt(LocalDateTime.now());

                        // Restore the original ID
                        existingAddress.setId(originalAddressId);
                        log.info("After update: Address ID = {}", existingAddress.getId());
                    }

                    // Save updated contact
                    Contact savedContact = contactRepository.save(contact);
                    contactRepository.flush(); // Ensure persistence

                    ContactResponseData updatedContactResponseData = mapper.map(savedContact, ContactResponseData.class);
                    log.info("Updated Contact Successfully: {}", updatedContactResponseData);

                    return APIResponse.<ContactResponseData>builder()
                            .data(updatedContactResponseData)
                            .statusCode(200)
                            .success(true)
                            .message("Contact updated successfully")
                            .build();
                })
                .orElseThrow(() -> {
                    log.error("Contact with ID {} not found", id);
                    return APIException.builder()
                            .message("Contact not found")
                            .statusCode(404)
                            .build();
                });
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
    @Transactional
    public APIResponse<Void> bulkDeleteContacts(BulkDeleteRequest bulkDeleteRequest) {
        List<Long> contactIds = bulkDeleteRequest.getContactIds();
        contactRepository.deleteAllByIdIsIn(contactIds);
        return APIResponse.<Void>builder()
               .statusCode(204)
               .build();
    }

    @Override
    public APIResponse<Page<ContactResponseData>> searchContacts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Specification<Contact> spec = ContactSpecification.searchByKeyword(keyword);

        Page<Contact> contacts = contactRepository.findAll(spec, pageable);

        Page<ContactResponseData> responseData = contacts.map(contact -> mapper.map(contact, ContactResponseData.class));

        return APIResponse.<Page<ContactResponseData>>builder()
                .data(responseData)
                .statusCode(200)
                .build();
    }

    @Override
    public APIResponse<ContactResponseData> toggleFavorite(Long id) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (optionalContact.isEmpty()) {
            throw APIException.builder()
                    .message("Contact not found for provided Id!")
                    .statusCode(404)
                    .build();
        }

        Contact contact = optionalContact.get();
        boolean contactFavoriteState = contact.isFavorite();

        contact.setFavorite(!contactFavoriteState);
        contactRepository.save(contact);

        ContactResponseData updatedContactResponseData = mapper.map(contact, ContactResponseData.class);
        return APIResponse.<ContactResponseData>builder()
                .data(updatedContactResponseData)
                .statusCode(200)
                .build();
    }

    @Override
    public APIResponse<Page<ContactResponseData>> getAllSortedContacts(Integer page, Integer size, String sort, String direction) {
        Sort.Direction sortDirection = Sort.Direction.valueOf(direction.toUpperCase());
        Sort sorting = Sort.by(sortDirection, validateSortField(sort));

        Page<ContactResponseData> contacts = contactRepository.findAllBy(
                PageRequest.of(page, size, sorting)
        ).map(contact -> mapper.map(contact, ContactResponseData.class));

        return APIResponse.<Page<ContactResponseData>>builder()
                .data(contacts)
                .statusCode(200)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public APIResponse<Integer> contactCount() {
        try {
            long count = contactRepository.count(); // Fetch the count of contacts
            return APIResponse.<Integer>builder()
                    .message("Count fetched successfully")
                    .data((int) count) // Cast long to int
                    .success(true)
                    .statusCode(200)
                    .build();
        } catch (Exception e) {
            return APIResponse.<Integer>builder()
                    .message("Failed to fetch count: " + e.getMessage())
                    .data(null)
                    .success(false)
                    .statusCode(500)
                    .build();
        }
    }

    private String validateSortField(String sort) {
        Set<String> validSortFields = Set.of("firstName", "lastName", "email", "phoneNumber");
        if (!validSortFields.contains(sort)) {
            throw new IllegalArgumentException("Invalid sort field. Valid fields are: " + String.join(", ", validSortFields));
        }
        return sort;
    }

    @Override
    public APIResponse<Page<ContactResponseData>> getFavoriteContacts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Contact> favoriteContacts = contactRepository.findContactsByIsFavorite(true, pageable);

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

            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                Contact contact = Contact.builder()
                        .firstName(record[0])
                        .lastName(record[1])
                        .email(record[2])
                        .phoneNumber(record[3])
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
            csvWriter.writeNext(new String[]{"First Name", "Last Name", "Email", "Phone"});

            for (Contact contact : contacts) {
                csvWriter.writeNext(new String[]{
                        contact.getFirstName(),
                        contact.getLastName(),
                        contact.getEmail(),
                        contact.getPhoneNumber()
                });
            }
        }
    }


}
