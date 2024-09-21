package dev.sam.SpringRestApi.response;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private boolean status;
    private String message;
    private PaymentData data;

    // Inner class to represent the data part of the response
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentData {
        private String reference;
        private String checkout_url;


    }


}


