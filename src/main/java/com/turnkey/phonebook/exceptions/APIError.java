package com.turnkey.phonebook.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class APIError {
    private int statusCode;
    private String message;
    private String path;
    private String timestamp;
    private List<String> errors;
}
