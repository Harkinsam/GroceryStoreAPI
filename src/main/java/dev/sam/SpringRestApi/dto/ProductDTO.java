package dev.sam.SpringRestApi.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Setter
@Getter
public class ProductDTO {
    private String uuid;
    private String genericName;
    private String name;
    private String categoryName; // Instead of Category object, just the name or ID
    private boolean isPerishable;
    private BigDecimal pricePerUnit;
    private int quantityInStock;
    private String tag;
    private String unitSpecification;
    private String vendorName;
    private String imageUrl;
}

