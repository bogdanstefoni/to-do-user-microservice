package com.bogdan.todouser.enums;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorsEnum {

    GENERAL_ERROR(INTERNAL_SERVER_ERROR, "Unexpected error.", INTERNAL_SERVER_ERROR.value()),
    USER_NOT_FOUND(NOT_FOUND, "User was not found", NOT_FOUND.value()),
    USER_EXISTS(NOT_ACCEPTABLE, "User already found", NOT_ACCEPTABLE.value()),
    EMAIL_ALREADY_EXISTS(NOT_ACCEPTABLE, "Email already exists", NOT_ACCEPTABLE.value()),
    TOKEN_MISSING(UNAUTHORIZED, "Token is missing from request", UNAUTHORIZED.value()),
    TOKEN_INVALID(UNAUTHORIZED, "Token is invalid", UNAUTHORIZED.value()),
    LOGIN_WRONG_CREDENTIALS(UNAUTHORIZED, "Wrong username or password", UNAUTHORIZED.value()),
    USER_NOT_FOUND_BY_EMAIL(NOT_FOUND, "User not found by email", NOT_FOUND.value()),
    USER_DELETED_SUCCESSFULLY(NO_CONTENT, "User deleted successfully", NO_CONTENT.value()),
    USER_IS_LOCKED(FORBIDDEN,"User account is locked" ,FORBIDDEN.value() ),
    TASK_EXISTS(NOT_ACCEPTABLE,"Task already exists, " +
            "please complete the existing one or create another task" ,NOT_ACCEPTABLE.value() );

    private final HttpStatus httpStatus;

    private final String errorDescription;

    private final int errorCode;

    ErrorsEnum(HttpStatus httpStatus, String errorDescription, int errorCode) {
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public int getErrorCode() {
        return errorCode;
    }


}
