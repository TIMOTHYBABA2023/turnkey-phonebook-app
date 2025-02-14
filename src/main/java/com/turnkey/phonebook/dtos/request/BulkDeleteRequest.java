package com.turnkey.phonebook.dtos.request;

import lombok.Data;

import java.util.List;
@Data
public class BulkDeleteRequest {
    private List<Long> contactIds;
}
