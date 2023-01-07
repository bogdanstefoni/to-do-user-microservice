package com.bogdan.todouser.exception;

import com.bogdan.todouser.enums.ErrorsEnum;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public class RestResponse {

    private static final String RESULT = "result";
    private static final String TIMESTAMP = "timestamp";
    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_DESCRIPTION = "errorDescription";

    public static ResponseEntity<String> createSuccessResponse(JSONObject jsonObject) {

        return createRestResponse(createResponse(jsonObject), HttpStatus.OK);
    }

    public static ResponseEntity<String> createGenericSuccessRestResponse() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "success");
        return createRestResponse(createResponse(jsonObject), HttpStatus.OK);
    }

    public static ResponseEntity<String> createErrorResponse(ErrorsEnum error) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ERROR_CODE, error.getErrorCode());
        jsonObject.put(ERROR_DESCRIPTION, error.getErrorDescription());
        return createRestResponse(createResponse(jsonObject), error.getHttpStatus());
    }

    public static JSONObject createResponse(JSONObject jsonObject) {
        JSONObject response = new JSONObject();
        response.put(RESULT, jsonObject);
        response.put(TIMESTAMP, LocalDate.now());
        return response;
    }

    private static ResponseEntity<String> createRestResponse(JSONObject jsonObject,
                                                             HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new ResponseEntity<>(jsonObject.toString(), headers, httpStatus);
    }

}
