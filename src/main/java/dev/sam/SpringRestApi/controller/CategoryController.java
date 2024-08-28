package dev.sam.SpringRestApi.controller;

import dev.sam.SpringRestApi.model.Category;
import dev.sam.SpringRestApi.model.Product;
import dev.sam.SpringRestApi.repository.CategoryRepository;
import dev.sam.SpringRestApi.response.AuthenticationResponse;
import dev.sam.SpringRestApi.response.Response;
import dev.sam.SpringRestApi.response.ResponseHandler;
import dev.sam.SpringRestApi.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;

    }

    @PostMapping("/create")
    public ResponseEntity<Response> createNewCategory(@RequestBody @Valid Category category) {
        Category newCategory = categoryService.createCategory(category);
        Response response = new Response();
        response.setResponse(newCategory.getName() + " created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
