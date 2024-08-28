package dev.sam.SpringRestApi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="product", indexes = {
        @Index(name = "idx_product_uuid", columnList = "uuid"),
        @Index(name = "idx_product_name", columnList = "name")
})
public class Product {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true, columnDefinition = "CHAR(36)")
    private String uuid;


    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters long")
    private String name;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Generic Name must be between 2 and 100 characters long")
    private String genericName;

    @ManyToOne
    @JoinColumn(name = "category_identifier", referencedColumnName = "uuid")
    @NotNull(message = "Category cannot be null")
    private Category category;

    @NotBlank(message = "Unit specification cannot be Blank")
    @Size(max = 255, message = "Unit specification can be up to 255 characters long")
    private String unitSpecification;


    @DecimalMin(value = "0.0", inclusive = false, message = "Price per unit must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price per unit must be a valid decimal with up to 2 decimal places")
    private BigDecimal pricePerUnit;


    @Min(value = 0, message = "Quantity in stock must be zero or greater")
    private int quantityInStock;

    @NotNull(message = "Is perishable flag cannot be null")
    private Boolean isPerishable;

    @NotBlank
    @Size(max = 100, message = "Tag can be up to 100 characters long")
    private String tag;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name= "vendor_id",referencedColumnName = "uuid")
    private Vendor vendor;


}


