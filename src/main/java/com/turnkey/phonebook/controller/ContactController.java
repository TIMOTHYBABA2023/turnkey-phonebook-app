package com.turnkey.phonebook.controller;


import com.turnkey.phonebook.dtos.request.BulkDeleteRequest;
import com.turnkey.phonebook.dtos.request.NewContactRequest;
import com.turnkey.phonebook.dtos.request.UpdateContactRequest;
import com.turnkey.phonebook.dtos.response.APIResponse;
import com.turnkey.phonebook.dtos.response.ContactResponseData;
import com.turnkey.phonebook.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.StringWriter;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Slf4j
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<APIResponse<ContactResponseData>> createContact(@Valid @RequestBody NewContactRequest contact) {
        APIResponse<ContactResponseData> apiResponse = contactService.createContact(contact);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<APIResponse<Page<ContactResponseData>>> getAllContacts(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
            ) {
        APIResponse<Page<ContactResponseData>> apiResponse = contactService.getAllContacts(page, size);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ContactResponseData>> getContactById(@PathVariable Long id) {
        APIResponse<ContactResponseData> apiResponse = contactService.getContactById(id);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @GetMapping("/allSortedContacts")
    public ResponseEntity<APIResponse<Page<ContactResponseData>>> getAllSortedContacts(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "firstName") String sort,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ) {
        APIResponse<Page<ContactResponseData>> apiResponse = contactService.getAllSortedContacts(page, size, sort, direction);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @GetMapping("/contactCount")
    public ResponseEntity<APIResponse<Integer>> contactCount() {
        APIResponse<Integer> apiResponse = contactService.contactCount();
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<ContactResponseData>> updateContact(@PathVariable Long id, @RequestBody UpdateContactRequest contact) {
        log.info("Request coming in ::: {}", contact);
        APIResponse<ContactResponseData> apiResponse = contactService.updateContact(id, contact);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteContact(@PathVariable Long id) {
        APIResponse<Void> apiResponse = contactService.deleteContact(id);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<APIResponse<Void>> bulkDeleteContacts(@RequestBody BulkDeleteRequest bulkDeleteRequest) {
        APIResponse<Void> apiResponse = contactService.bulkDeleteContacts(bulkDeleteRequest);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse<Page<ContactResponseData>>> searchContacts(
            @RequestParam(value = "keyword", defaultValue = "firstName") String keyword,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        APIResponse<Page<ContactResponseData>> apiResponse = contactService.searchContacts(keyword, page, size);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @PostMapping("/toggleIsFavorite/{id}")
    public ResponseEntity<APIResponse<ContactResponseData>> toggleIsFavorite(@PathVariable Long id) {
        APIResponse<ContactResponseData> apiResponse = contactService.toggleFavorite(id);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @GetMapping("/favorites")
    public ResponseEntity<APIResponse<Page<ContactResponseData>>> getFavoriteContacts(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        APIResponse<Page<ContactResponseData>> apiResponse = contactService.getFavoriteContacts(page, size);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }


    @PostMapping(value = "/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importContacts(@RequestPart("file") MultipartFile file) {
        try {
            contactService.importContactsFromCSV(file);
            return ResponseEntity.ok("Contacts imported successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import contacts");
        }
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportContacts() {
        try {
            StringWriter writer = new StringWriter();
            contactService.exportContactsToCSV(writer);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

            return new ResponseEntity<>(writer.toString(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to export contacts");
        }
    }

}
