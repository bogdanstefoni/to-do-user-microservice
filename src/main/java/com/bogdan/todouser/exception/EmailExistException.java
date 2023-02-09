package com.bogdan.todouser.exception;

import com.bogdan.todouser.enums.ErrorsEnum;

public class EmailExistException extends Exception {

    public EmailExistException(ErrorsEnum message) {
        super(String.valueOf(message));
    }
}
