package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:query,'%'))"+
    " OR LOWER(p.description) LIKE LOWER(CONCAT('%',:query,'%'))"+
    " OR LOWER(p.category.name) LIKE LOWER(CONCAT('%',:query,'%'))")
    List<Product> searchProducts(@Param("query") String query);


    @Query("SELECT p FROM Product p WHERE " +
        "(:category IS NULL OR LOWER(p.category.name) = LOWER(:category))" +
        "AND (:minPrice IS NULL OR p.price >= :minPrice) "+
        "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> filterProducts(
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );


}
