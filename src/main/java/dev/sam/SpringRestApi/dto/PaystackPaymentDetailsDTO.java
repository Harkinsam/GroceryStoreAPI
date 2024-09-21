package dev.sam.SpringRestApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaystackPaymentDetailsDTO {
    private String email;
    private int amount;
    private String currency;
    private String reference;
    private String url;

    // Constructor, getters, setters
}

