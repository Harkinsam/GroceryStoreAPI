package dev.sam.SpringRestApi.service;

import dev.sam.SpringRestApi.exception.ConflictException;
import dev.sam.SpringRestApi.model.Category;
import dev.sam.SpringRestApi.repository.CategoryRepository;
import dev.sam.SpringRestApi.response.AuthenticationResponse;
import dev.sam.SpringRestApi.response.Response;
import dev.sam.SpringRestApi.response.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {
        Optional<Category> categoryOptional = categoryRepository.findCategoryByName(category.getName());
        if (categoryOptional.isPresent()) {
            throw new ConflictException(category.getName() + " already exists");
        }

        Category category1 = new Category();
        category1.setName(category.getName());
        category1.setUuid(UUID.randomUUID().toString());
        return categoryRepository.save(category1);
    }


}
