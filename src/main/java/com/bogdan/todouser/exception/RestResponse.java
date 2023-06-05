package com.bogdan.todouser.exception;

import com.bogdan.todouser.domain.HttpResponse;
import com.bogdan.todouser.enums.ErrorsEnum;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class RestResponse {

    public static final String ERROR_CODE = "Error code";
    public static final String ERROR_DESCRIPTION = "Error description";

    public static ResponseEntity<HttpResponse> createResponse(HttpStatus httpStatus, String message) {
        return response(httpStatus, message);
    }

    public static ResponseEntity<String> createSuccessResponse(JSONObject jsonObject) {
        return createRestResponse(createResponse(jsonObject), HttpStatus.OK);
    }

    private static ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message);

        return new ResponseEntity<>(body, httpStatus);
    }

    public static JSONObject createResponse(JSONObject jsonObject) {
        JSONObject response = new JSONObject();
        HttpResponse httpResponse = new HttpResponse();
        response.put(httpResponse.getMessage(), jsonObject);
        response.put("TIMESTAMP", httpResponse.getTimeStamp());
        return response;
    }

    private static ResponseEntity<String> createRestResponse(JSONObject jsonObject,
                                                             HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new ResponseEntity<>(jsonObject.toString(), headers, httpStatus);
    }

    public static ResponseEntity<ErrorResponse> createErrorResponse(ErrorsEnum error) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(error.getErrorCode());
        errorResponse.setErrorDescription(error.getErrorDescription());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, error.getHttpStatus());
    }
}
