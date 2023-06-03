package com.bogdan.todouser.exception;


import com.bogdan.todouser.enums.ErrorsEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandling extends ResponseEntityExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ExceptionHandling.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleException(
            HttpServletRequest request, Throwable ex) {
        ErrorsEnum error;
        if (ex instanceof CustomException customException) {
            error = customException.getErrorsEnum();
        } else {
            error = ErrorsEnum.GENERAL_ERROR;
        }
        logger.error(error.getErrorDescription(), ex);

        return RestResponse.createErrorResponse(error);
    }


}