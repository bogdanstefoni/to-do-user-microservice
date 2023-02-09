package com.bogdan.todouser.enums;

import org.springframework.http.HttpStatus;

public enum ErrorsEnum {

    GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error.", 1),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User was not found", 2),
    USER_EXISTS(HttpStatus.NOT_ACCEPTABLE, "User already found", 3),
    EMAIL_ALREADY_EXISTS(HttpStatus.NOT_ACCEPTABLE, "Email already exists", 4),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "Token is missing from request", 5),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Token is invalid", 6),
    LOGIN_WRONG_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Wrong username or password", 7),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "User not found by email", 8),
    USER_DELETED_SUCCESSFULLY(HttpStatus.NO_CONTENT, "User deleted successfully", 9);

    private HttpStatus httpStatus;

    private String errorDescription;

    private int errorCode;

    ErrorsEnum(HttpStatus httpStatus, String errorDescription, int errorCode) {
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
