package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Product;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Long categoryId;
    private String categoryName;
    private MultipartFile image;

}
