package com.turnkey.phonebook.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
public class APIException extends RuntimeException{
    private final String message;
    private final int statusCode;
}
