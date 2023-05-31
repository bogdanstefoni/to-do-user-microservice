package com.bogdan.todouser.exception;


import com.bogdan.todouser.enums.ErrorsEnum;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ErrorsEnum errorsEnum;

    public CustomException(ErrorsEnum errorsEnum) {
        super();
        this.errorsEnum = errorsEnum;
    }

    public ErrorsEnum getErrorsEnum() {
        return errorsEnum;
    }

    public void setErrorsEnum(ErrorsEnum errorsEnum) {
        this.errorsEnum = errorsEnum;
    }

}
