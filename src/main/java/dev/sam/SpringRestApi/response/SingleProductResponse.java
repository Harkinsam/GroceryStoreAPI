package dev.sam.SpringRestApi.response;

import dev.sam.SpringRestApi.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleProductResponse {
    private String message;
    private ProductDTO product;
}
