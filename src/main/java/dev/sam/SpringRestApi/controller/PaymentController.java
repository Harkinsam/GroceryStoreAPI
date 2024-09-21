package dev.sam.SpringRestApi.controller;

import dev.sam.SpringRestApi.response.PaymentResponse;
import dev.sam.SpringRestApi.response.PaystackResponse;
import dev.sam.SpringRestApi.response.VerifyTransactionResponse;
import dev.sam.SpringRestApi.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initialize/korapay")
    public ResponseEntity<PaymentResponse> initializeKoraPayment(
            @RequestParam int amount,
            @RequestParam String currency,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            Authentication authentication) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PaymentResponse(false, "Authorization header is missing or invalid.", null));
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            // Return unauthorized response
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PaymentResponse(false, "Authentication required or invalid token", null));
        }

        // Get the logged-in user from the authentication token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        try {
            // Initialize the payment and get the response
            PaymentResponse paymentResponse = paymentService.initializeKoraPayment(amount, currency, userDetails);

            // Return the response from the payment service, containing status, message, and data
            return ResponseEntity.ok(paymentResponse);
        } catch (Exception e) {
            // Handle errors in payment initialization
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PaymentResponse(false, "Payment initialization failed: " + e.getMessage(), null));
        }
    }
    @PostMapping("/initialize/paystack")
    public ResponseEntity<PaystackResponse> initializePayment(
            @RequestParam double amount,
            @RequestParam String currency,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            Authentication authentication) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PaystackResponse(false, "Authorization header is missing or invalid.", null));
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            // Return unauthorized response
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PaystackResponse(false, "Authentication required or invalid token", null));
        }


        // Get the logged-in user from the authentication token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        try {
            // Initialize the payment and get the response
            PaystackResponse paymentResponse = paymentService.initializePaystackPayment(amount, currency, userDetails);

            // Return the response from the payment service, containing status, message, and data
            return ResponseEntity.ok(paymentResponse);
        } catch (Exception e) {
            // Handle errors in payment initialization
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PaystackResponse(false, "Payment initialization failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<VerifyTransactionResponse> verifyTransaction(
            @RequestParam String reference) {

        try {
            VerifyTransactionResponse verificationResponse = paymentService.verifyTransaction(reference);

            if (verificationResponse != null && verificationResponse.isStatus()) {
                return ResponseEntity.ok(verificationResponse);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(verificationResponse);
            }

        } catch (Exception e) {
            // Handle errors during transaction verification
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new VerifyTransactionResponse(false, "Transaction verification failed: " + e.getMessage(), null));
        }
    }


}




