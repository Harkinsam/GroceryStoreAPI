package dev.sam.SpringRestApi.service;

import dev.sam.SpringRestApi.dto.PaymentDetailsDTO;
import dev.sam.SpringRestApi.response.PaymentResponse;
import dev.sam.SpringRestApi.dto.PaystackPaymentDetailsDTO;
import dev.sam.SpringRestApi.response.PaystackResponse;
import dev.sam.SpringRestApi.model.Customer;
import dev.sam.SpringRestApi.response.VerifyTransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.text.DecimalFormat;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${korapay.secret.key}")
    private String korapaySecretKey;
    @Value("${paystack.secret-key}")
    private String paystackSecretKey;

    public PaymentResponse initializeKoraPayment(int amount, String currency, UserDetails userDetails) {
        String reference = generateUniqueReference();  // Generate a unique reference (add this method).

        Customer customer = new Customer(userDetails.getUsername());
        int payStackWay = amount * 100;

        // Create the payment details object
        PaymentDetailsDTO paymentDetails = new PaymentDetailsDTO(payStackWay, currency, reference, customer);

        try {
            // Create the WebClient instance with builder
            WebClient webClient = webClientBuilder.baseUrl("https://api.korapay.com")
                    .build();

            // Make a POST request to Korapay API
            PaymentResponse response = webClient.post()
                    .uri("/merchant/api/v1/charges/initialize")
                    .header("Authorization", "Bearer " + korapaySecretKey)
                    .bodyValue(paymentDetails)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        // Capture and log error response from Korapay API
                        return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            System.err.println("Korapay API error: " + errorBody); // Logs error for you
                            return Mono.error(new RuntimeException("Korapay API error: " + errorBody));
                        });
                    })
                    .bodyToMono(PaymentResponse.class)
                    .block(); // Blocking for simplicity in this example


            return response;

        } catch (Exception ex) {
            // Log the error for developer's reference
            System.err.println("Exception during payment initialization: " + ex.getMessage());
            throw new RuntimeException("Payment initialization failed");
        }
    }

    public PaystackResponse initializePaystackPayment(double amount, String currency, UserDetails userDetails) {

        int paystackWay = (int) Math.round(amount * 100);
        String reference = generateUniqueReference();

        PaystackPaymentDetailsDTO paymentDetails = new PaystackPaymentDetailsDTO (userDetails.getUsername(),paystackWay,currency,reference,"http://localhost:8080/api/payments/callback");

        try {
            // Create the WebClient instance with the Paystack base URL
            WebClient webClient = webClientBuilder.baseUrl("https://api.paystack.co")
                    .build();

            // Make a POST request to Paystack's initialize transaction endpoint
            PaystackResponse response = webClient.post()
                    .uri("/transaction/initialize")
                    .header("Authorization", "Bearer " + paystackSecretKey)  // Paystack secret key
                    .header("Content-Type", "application/json")
                    .bodyValue(paymentDetails)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        // Capture and log error response from Paystack API
                        return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Paystack API error: {}", errorBody); // Logs error for you
                            return Mono.error(new RuntimeException("Paystack API error: " + errorBody));
                        });
                    })
                    .bodyToMono(PaystackResponse.class)
                    .block();  // Blocking for simplicity in this example

            handlePaystackResponse(response);

            return response;

        } catch (Exception ex) {
            // Log the error for developer's reference
            log.error("Exception during payment initialization: {}", ex.getMessage(), ex);
            throw new RuntimeException("Payment initialization failed", ex);
        }
    }



    private void handlePaystackResponse(PaystackResponse response) {
        if (response.isStatus()) {
            String authorizationUrl = response.getData().getAuthorization_url();
            String accessCode = response.getData().getAccess_code();
            String reference = response.getData().getReference();

            // Handle the payment initiation process, e.g., redirect the user to the authorization URL
            System.out.println("Authorization URL: " + authorizationUrl);
            System.out.println("Access Code: " + accessCode);
            System.out.println("Reference: " + reference);

            // Here, you might want to return the URL to the controller or store the reference and access code
        } else {
            // Handle the failure case
            System.err.println("Payment initialization failed: " + response.getMessage());
        }
    }

    public VerifyTransactionResponse verifyTransaction(String reference) {
        try {
            // Make a GET request to Paystack API to verify the transaction
            Mono<VerifyTransactionResponse> responseMono = webClientBuilder
                    .build()
                    .get()
                    .uri("https://api.paystack.co/transaction/verify/{reference}", reference)
                    .header("Authorization", "Bearer " + paystackSecretKey)
                    .retrieve()
                    .bodyToMono(VerifyTransactionResponse.class);

            VerifyTransactionResponse payStackResponse = responseMono.block(); // Blocking for simplicity

            // Check if the response is null or status is false
            if (payStackResponse == null || !payStackResponse.isStatus()) {
                throw new RuntimeException("An error occurred while verifying payment: " + (payStackResponse != null ? payStackResponse.getMessage() : "Unknown error"));
            }

            // Check if transaction status is successful
            if ("success".equalsIgnoreCase(payStackResponse.getData().getStatus())) {
                // Handle successful verification (if needed)
                int amountInKobo = payStackResponse.getData().getAmount();
                double amountInNaira = amountInKobo / 100.0;
                log.info("Payment successful: {}",amountInNaira);
            } else {
                // Handle failed verification (if needed)
                log.warn("Payment failed: {}", payStackResponse.getData().getStatus());
            }

            return payStackResponse;

        } catch (Exception ex) {
            // Log and handle exception
            log.error("Exception during transaction verification: {}", ex.getMessage(), ex);
            throw new RuntimeException("Transaction verification failed", ex);
        }
    }



    private String generateUniqueReference() {
        String prefix = "TXN_"; // Custom prefix to identify the transaction
        String timestampPart = String.valueOf(System.currentTimeMillis()); // Add the current timestamp
        String uuidPart = UUID.randomUUID().toString().split("-")[0]; // Shortened UUID (first part)

        // Combine the prefix, timestamp, and UUID into a unique reference
        return prefix + timestampPart + "_" + uuidPart;
    }
}


