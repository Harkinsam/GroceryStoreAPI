package dev.sam.SpringRestApi.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class ResponseHandler {
//    public static ResponseEntity<Object> responseBuilder(
//            String message,String token, HttpStatus httpStatus, Object responseObject
//    )
//    {
//        Map<String, Object> response = new HashMap<>();
//        response.put("token", token);
//        response.put("message", message);
//        response.put("httpStatus", httpStatus);
//        response.put("data", responseObject);
//
//        return new ResponseEntity<>(response, httpStatus);
//    }

    public ResponseEntity<AuthenticationResponse> responseBuilder(String message, String token, HttpStatus httpStatus) {
        AuthenticationResponse response = new AuthenticationResponse(token, message);
        return new ResponseEntity<>(response, httpStatus);
    }

}
