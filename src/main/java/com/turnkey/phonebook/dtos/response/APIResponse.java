package com.turnkey.phonebook.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse<D> {
    private String message;
    private D data;
    private boolean success;
    private int statusCode;
}
