package dev.sam.SpringRestApi.dto;

import dev.sam.SpringRestApi.model.Customer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsDTO {

    private int amount;
    private String currency;
    private String reference;
    private Customer customer; // Use the existing User entity
}