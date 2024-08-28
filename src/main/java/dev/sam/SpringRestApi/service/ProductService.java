package dev.sam.SpringRestApi.service;

import dev.sam.SpringRestApi.dto.ProductDTO;
import dev.sam.SpringRestApi.exception.ConflictException;
import dev.sam.SpringRestApi.model.Category;
import dev.sam.SpringRestApi.model.Product;
import dev.sam.SpringRestApi.model.Vendor;
import dev.sam.SpringRestApi.repository.CategoryRepository;
import dev.sam.SpringRestApi.repository.ProductRepository;
import dev.sam.SpringRestApi.repository.VendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.DataRedundancy;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ProductService {
    private static final String BUCKET_NAME ="saminventorystorage";
    private final ProductRepository productRepository;
    private  final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;
    private final AWSS3Service awsS3Service;
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, VendorRepository vendorRepository, AWSS3Service awsS3Service) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
        this.awsS3Service = awsS3Service;
    }

    public Product createProduct(Product product,MultipartFile imageFile) {
        log.info("Product creation started for product: {}", product.getName());

        // Log the content type of the uploaded file
        log.info("File content type: {}", imageFile.getContentType());


        Optional<Product> existingProduct = productRepository.findProductByName(product.getName());
        if (existingProduct.isPresent()) {
            log.warn("Product creation failed: Product with name '{}' already exists", product.getName());
            throw new ConflictException(product.getName() + " already exists");
        }

        // Check if the category exists
        Optional<Category> existingCategory = categoryRepository.findCategoryByName(product.getCategory().getName());
        if (existingCategory.isEmpty()) {
            log.warn("Product creation failed: Category '{}' does not exist", product.getCategory().getName());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, product.getCategory().getName() + " category does not exist");
        }
        Category category = existingCategory.get();
        log.info("Category '{}' found", category.getName());

        // Check if the vendor exists
        Optional<Vendor> existingVendor = vendorRepository.findVendorByUuid(product.getVendor().getUuid());
        if (existingVendor.isEmpty()) {
            log.warn("Product creation failed: Vendor with UUID '{}' does not exist", product.getVendor().getUuid());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, product.getVendor().getName() + " vendor does not exist");
        }
        Vendor vendor = existingVendor.get();
        log.info("Vendor '{}' found", vendor.getName());

        // Upload the image file
        log.info("Uploading image for product: {}", product.getName());

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is required for creating a product.");
        }

        CompletableFuture<String> uploadFuture = awsS3Service.uploadFile(imageFile, BUCKET_NAME);
        String imageUrl = uploadFuture.join();
        log.info("Image uploaded successfully for product: {}, URL: {}", product.getName(), imageUrl);

        // Create and save the new product
        Product newProduct = new Product();
        newProduct.setUuid(UUID.randomUUID().toString());
        newProduct.setGenericName(product.getGenericName());
        newProduct.setName(product.getName());
        newProduct.setCategory(category);
        newProduct.setUnitSpecification(product.getUnitSpecification());
        newProduct.setPricePerUnit(product.getPricePerUnit());
        newProduct.setQuantityInStock(product.getQuantityInStock());
        newProduct.setIsPerishable(product.getIsPerishable());
        newProduct.setTag(product.getTag());
        newProduct.setVendor(vendor);
        newProduct.setImageUrl(imageUrl);

        Product savedProduct = productRepository.save(newProduct);
        log.info("Product '{}' created successfully with UUID: {}", savedProduct.getName(), savedProduct.getUuid());

        return savedProduct;
    }

    public Product updateProduct(Product updatedProduct) {
        Optional<Product> existingProductOpt = productRepository.findProductByUuid(updatedProduct.getUuid());
        if (existingProductOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Product with name: " + updatedProduct.getName() + ", does not exist");
        }

        Product existingProduct = existingProductOpt.get();

        Optional<Category> existingCategoryOpt = categoryRepository.findCategoryByName(updatedProduct.getCategory().getName());
        if (existingCategoryOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category " + updatedProduct.getCategory().getName() + " does not exist");
        }
        Category category = existingCategoryOpt.get();

        Optional<Vendor> existingVendorOpt = vendorRepository.findVendorByUuid(updatedProduct.getVendor().getUuid());
        if (existingVendorOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Vendor " + updatedProduct.getVendor().getUuid() + " does not exist");
        }
        Vendor vendor = existingVendorOpt.get();

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setGenericName(updatedProduct.getGenericName());
        existingProduct.setCategory(category);
        existingProduct.setUnitSpecification(updatedProduct.getUnitSpecification());
        existingProduct.setPricePerUnit(updatedProduct.getPricePerUnit());
        existingProduct.setQuantityInStock(updatedProduct.getQuantityInStock());
        existingProduct.setIsPerishable(updatedProduct.getIsPerishable());
        existingProduct.setTag(updatedProduct.getTag());
        existingProduct.setVendor(vendor);

        return productRepository.save(existingProduct);
    }

    public ProductDTO getProductByUuid(String uuid) {
        Optional<Product> productOpt = productRepository.findProductByUuid(uuid);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            ProductDTO dto = getProductDTO(product);
            return dto;
        }
        return null;
    }
    public Page<ProductDTO> findProductsWithPagination(int pageNumber, int pageSize) {
        Page<Product> products = productRepository.findAll(PageRequest.of(pageNumber, pageSize));
        List<ProductDTO> productDTOs = new ArrayList<>();

        for (Product product : products.getContent()) {
            ProductDTO dto = getProductDTO(product);
            productDTOs.add(dto);
        }

        // Return a Page<ProductDTO> with the same pagination information
        return new PageImpl<>(productDTOs, products.getPageable(), products.getTotalElements());
    }

    private static ProductDTO getProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setUuid(product.getUuid());
        dto.setGenericName(product.getGenericName());
        dto.setName(product.getName());
        dto.setPricePerUnit(product.getPricePerUnit());
        dto.setCategoryName(product.getCategory().getName());
        dto.setPerishable(product.getIsPerishable());
        dto.setQuantityInStock(product.getQuantityInStock());
        dto.setTag(product.getTag());
        dto.setUnitSpecification(product.getUnitSpecification());
        dto.setVendorName(product.getVendor().getName());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }


    public Page<ProductDTO> getSimilarProducts(String uuid, int pageNumber, int pageSize) {
        Optional<Product> productOpt = productRepository.findProductByUuid(uuid);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<Product> similarProducts = productRepository.findByTag(product.getTag(),pageable);

            // Convert products to DTOs manually
            List<ProductDTO> productDTOs = new ArrayList<>();
            for (Product similarProduct : similarProducts) {
                ProductDTO dto = getProductDTO(similarProduct);
                productDTOs.add(dto);
            }

            // Return a Page<ProductDTO> with the same pagination information
            return new PageImpl<>(productDTOs, pageable, similarProducts.size());
        }
        return new PageImpl<>(Collections.emptyList(), PageRequest.of(pageNumber, pageSize), 0);
    }




    public String deleteProduct(String uuid) {
        Optional<Product> existingProduct = productRepository.findProductByUuid(uuid);
        if (existingProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, uuid + " does not exist");
        }

        Product product = existingProduct.get();
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // Extract the file name from the image URL
            String fileName = product.getImageUrl().substring(product.getImageUrl().lastIndexOf("/") + 1);
            CompletableFuture<DeleteObjectResponse> deleteFuture = awsS3Service.deleteFile(BUCKET_NAME, fileName);

            // Handle the completion or failure of the delete operation
            deleteFuture.thenAccept(aVoid -> {
                log.info("Image file '{}' deleted successfully from S3 for product: {}", fileName, product.getName());
            }).exceptionally(throwable -> {
                log.error("Failed to delete image file '{}' from S3 for product: {}", fileName, product.getName(), throwable);
                return null;
            });
        } else {
            log.info("Product '{}' does not have an associated image to delete.", product.getName());
        }

        productRepository.delete(product);
        return product.getName();
    }

}
