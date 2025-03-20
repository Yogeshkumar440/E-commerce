package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class CartResponseDTO {

    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private ProductDTO product;
    private int quantity;

}

