package com.example.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;

    // Add a new category
    public Category addCategory(Category category){
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if(existingCategory.isPresent()){
            throw new RuntimeException("Category already exists:");
        }
        return categoryRepository.save(category);
    }

    // Get all categories
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    // Get a category by Id
    public Category getCategoryById(Long id){
        return categoryRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Category not found:"));
    }

    // Update an existing category
    public Category updateCategory(Long id,Category categoryDetails){
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());;
        return categoryRepository.save(category);
    }

    // Delete category (only if no products are assigned)
    public ResponseEntity<String> deleteCategory(Long id){
       
        if(productRepository.existsByCategoryId(id)){
            // throw new CategoryHaveProductsException("Cannot delete category because it has products:");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot delete category(it have one or more products)");
        }

        if(categoryRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("category does not exist");
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok("Category deleted successfully:");
    }

    // Add categories in bulk
    public List<Category> addCategories(List<Category> categories) {
        return categoryRepository.saveAll(categories);
    }

}
