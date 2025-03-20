package com.example.ecommerce.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.ProductService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String UPLOAD_DIR = "uploads/product-images/";

    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ){
        List<Product> products = productService.filterProducts(category,minPrice,maxPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query){
        List<Product> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId){
        return productService.getProductsByCategory(categoryId);
    }

    @PostMapping("/add")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@ModelAttribute ProductDTO productDTO){
        try{

            Product product = new Product();

            // Create Product Object
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setStock(productDTO.getStock());
            if(productDTO.getImage() != null && !productDTO.getImage().isEmpty()) {
                String imagePath = saveImage(productDTO.getImage());
                product.setImageUrl(imagePath);
            }

            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
            if(category == null){
                return ResponseEntity.badRequest().body(null);
            }
            product.setCategory(category);

            Product savedProduct = productService.createProduct(product);
            return ResponseEntity.ok(savedProduct);

        } catch (IOException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        if(image.isEmpty()){
            return null;
        }
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR,fileName);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath,image.getBytes());
        return "/" + UPLOAD_DIR + fileName;
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Product> updateProduct(@PathVariable Long id,@RequestBody Product product){
//        Product updatedProduct = productService.updateProduct(id, product);
//        return ResponseEntity.ok(updatedProduct);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
//            System.out.println("Received request to update product with ID: "+ id);

            Product existingProduct = productService.getProductById(id).orElse(null);
            if (existingProduct == null) {
//                System.out.println("Product not found with Id: " + id);
                return ResponseEntity.notFound().build();
            }

//            System.out.println("Existing product : " + existingProduct);

            // Update Fields
            existingProduct.setName(name == null ? existingProduct.getName() : name);
            existingProduct.setDescription(description == null ? existingProduct.getDescription() : description);
            existingProduct.setPrice(price == null ? existingProduct.getPrice() : price);

            // If a new image is provided, delete the old one and save the new one
            if(image != null && !image.isEmpty()){
//                System.out.println("New image received: " + image.getOriginalFilename());


                // Delete old image
                if(existingProduct.getImageUrl() != null){
                    Path oldImagePath = Paths.get(existingProduct.getImageUrl().substring(1));
                    Files.deleteIfExists(oldImagePath);
//                    System.out.println("Deleted old image: " + existingProduct.getImageUrl());
                }

                String newImagePath = saveImage(image);
//                System.out.println("New image Path Saved: " + newImagePath);

                existingProduct.setImageUrl(newImagePath);
            }
//            System.out.println("Updated product before saving: " + existingProduct);
            Product updatedProduct = productService.createProduct(existingProduct);
//            System.out.println("Updated product after saving: " + updatedProduct);

            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
//            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Product>> addProduct(@RequestBody List<Product> products){
        List<Product> savedProducts = productService.addProducts(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
