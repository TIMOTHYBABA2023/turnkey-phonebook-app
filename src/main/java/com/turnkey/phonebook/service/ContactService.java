package com.turnkey.phonebook.service;


import com.turnkey.phonebook.dtos.request.BulkDeleteRequest;
import com.turnkey.phonebook.dtos.request.NewContactRequest;
import com.turnkey.phonebook.dtos.request.UpdateContactRequest;
import com.turnkey.phonebook.dtos.response.APIResponse;
import com.turnkey.phonebook.dtos.response.ContactResponseData;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface ContactService {
    APIResponse<ContactResponseData> createContact(NewContactRequest contact);
    APIResponse<Page<ContactResponseData>> getAllContacts(Integer page, Integer size);
    APIResponse<ContactResponseData> getContactById(Long id);
    APIResponse<ContactResponseData> updateContact(Long id, UpdateContactRequest contact);
    APIResponse<Void> deleteContact(Long id);
    APIResponse<Void> bulkDeleteContacts(BulkDeleteRequest bulkDeleteRequest);
    APIResponse<Page<ContactResponseData>> searchContacts(String keyword, int page, int size);
    APIResponse<Page<ContactResponseData>> getFavoriteContacts(int page, int size);

    void importContactsFromCSV(MultipartFile file) throws Exception;

    void exportContactsToCSV(Writer writer) throws IOException;

    APIResponse<ContactResponseData> toggleFavorite(Long id);

    APIResponse<Page<ContactResponseData>> getAllSortedContacts(Integer page, Integer size, String sort, String direction);

    APIResponse<Integer> contactCount();
}
