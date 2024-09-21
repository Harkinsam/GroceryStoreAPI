package dev.sam.SpringRestApi.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaystackResponse {
    private boolean status;
    private String message;
    private Data data;

    // Inner class for the 'data' part of the response
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private String authorization_url;
        private String access_code;
        private String reference;
    }
}

