package com.bogdan.todouser.factory;

import com.bogdan.todouser.domain.HttpResponse;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestResponseFactory {

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
}
