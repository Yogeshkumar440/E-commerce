package com.example.ecommerce.exception;

public class CategoryHaveProductsException extends RuntimeException{

    public CategoryHaveProductsException(String message){
        super(message);
    }

}
