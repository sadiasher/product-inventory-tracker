package com.access.productInventoryTracker.repository;

import com.access.productInventoryTracker.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // You can add custom methods here if needed, for example: 
    // List<Product> findByCategory(String category);

    // Find products by category (case-insensitive) that are available, ordered by price descending
    @Query("SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category) AND p.available = true ORDER BY p.price DESC")
    List<Product> findProductsByCategory(@Param("category") String category);

    // Find products within a price range
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}
