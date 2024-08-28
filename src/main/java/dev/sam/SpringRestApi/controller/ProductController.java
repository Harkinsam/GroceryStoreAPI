package dev.sam.SpringRestApi.controller;

import dev.sam.SpringRestApi.dto.ProductDTO;
import dev.sam.SpringRestApi.model.Product;
import dev.sam.SpringRestApi.response.ProductResponse;
import dev.sam.SpringRestApi.response.Response;
import dev.sam.SpringRestApi.response.SingleProductResponse;
import dev.sam.SpringRestApi.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> createProduct(@RequestPart("product") @Valid Product product,
                                                  @RequestPart("imageFile") MultipartFile imageFile) {
        Product newProduct = productService.createProduct(product,imageFile);
        Response response = new Response();
        response.setResponse(newProduct.getName() + " created successfully. With uuid " + newProduct.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @PutMapping("/update")
//    public ResponseEntity<Response> updateProduct(@RequestBody @Valid Product updatedProduct) {
//        Product updatedProd = productService.updateProduct(updatedProduct);
//        Response response = new Response();
//        response.setResponse(updatedProd.getName() + " updated successfully");
//        return ResponseEntity.ok(response);
//    }

    //    @GetMapping
//    public ResponseEntity<ProductResponse> getAllProducts() {
//        List<ProductDTO> products = productService.getAllProducts();
//        if (products.isEmpty()) {
//            ProductResponse productResponse = new ProductResponse("No products found", Collections.emptyList());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(productResponse);
//        }
//        ProductResponse response = new ProductResponse("Products retrieved successfully", products);
//        return ResponseEntity.ok(response);
//    }
    @GetMapping("/pages")
    public ResponseEntity<Page<ProductDTO>> getProductsByPages(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "3") int pageSize) {
        int zeroBasedPageNumber = pageNumber - 1;
        Page<ProductDTO> products = productService.findProductsWithPagination(zeroBasedPageNumber, pageSize);
        if (products.hasContent()) {
            return ResponseEntity.ok(products);
        } else {
            // Return an empty page with the same pageable information
            return ResponseEntity.ok(Page.empty(products.getPageable()));
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SingleProductResponse> getProductByUuid(@PathVariable String uuid) {
        ProductDTO product = productService.getProductByUuid(uuid);
        if (product == null) {
            SingleProductResponse response = new SingleProductResponse("Product not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        SingleProductResponse response = new SingleProductResponse("Product found", product);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}/similar")
    public ResponseEntity<ProductResponse> getProductsByTag(@PathVariable String uuid,
                                                            @RequestParam(defaultValue = "1") int pageNumber,
                                                            @RequestParam(defaultValue = "1") int pageSize) {
        int zeroBasedPageNumber = pageNumber - 1;
        Page<ProductDTO> similarProducts = productService.getSimilarProducts(uuid, zeroBasedPageNumber, pageSize);

        if (similarProducts.isEmpty()) {
            ProductResponse productResponse = new ProductResponse("No similar products found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(productResponse);
        }
        List<ProductDTO> productsList = similarProducts.getContent();

        ProductResponse response = new ProductResponse("Products retrieved successfully", productsList);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("remove/{uuid}")
    public ResponseEntity<Response> deleteExistingProduct(@PathVariable String uuid) {
        String deletedProduct = productService.deleteProduct(uuid);
        Response response = new Response();
        response.setResponse(deletedProduct + " deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
