package com.bogdan.todouser.exception;

import com.bogdan.todouser.enums.ErrorsEnum;

public class UsernameExistException extends Exception {

    public UsernameExistException(ErrorsEnum message) {
        super(String.valueOf(message));
    }
}
