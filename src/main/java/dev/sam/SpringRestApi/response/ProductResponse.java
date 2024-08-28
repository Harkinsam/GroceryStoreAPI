package dev.sam.SpringRestApi.response;


import dev.sam.SpringRestApi.dto.ProductDTO;
import dev.sam.SpringRestApi.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String message;
    private List<ProductDTO> products;

}

