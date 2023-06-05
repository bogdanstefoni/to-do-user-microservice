package com.bogdan.todouser.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private int errorCode;
    private String errorDescription;

}
