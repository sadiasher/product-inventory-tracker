package com.access.productInventoryTracker.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.access.productInventoryTracker.dto.ProductDTO;
import com.access.productInventoryTracker.model.Product;
import com.access.productInventoryTracker.repository.ProductRepository;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // Helper method to convert Product to ProductDTO
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCategory().toLowerCase(),
            product.isAvailable()
        );
    }
    
    // Get all products as DTOs
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Your filtering methods here...

    // AI Generated
    public List<ProductDTO> getProductsByCategory(String category) {
        // Input validation: category must not be null or blank
        if (category == null || category.isBlank()) {
            return List.of();
        }
        
        return productRepository.findProductsByCategory(category).stream()
            .filter(Objects::nonNull)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Return products whose price is within the given inclusive range.
    // If minPrice or maxPrice is null, they are treated as unbounded on that side.
    public List<ProductDTO> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        // Input validation: prices must not be negative
        if ((minPrice != null && minPrice < 0) || (maxPrice != null && maxPrice < 0)) {
            return List.of();
        }
        
        double min = Optional.ofNullable(minPrice).orElse(0.0);
        double max = Optional.ofNullable(maxPrice).orElse(Double.MAX_VALUE);

        // If caller accidentally provided min > max, swap to be forgiving.
        if (min > max) {
            double tmp = min;
            min = max;
            max = tmp;
        }

        final double lower = min;
        final double upper = max;

        return productRepository.findByPriceRange(lower, upper)
        	.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Return products filtered by availability. If `available` is null, return all products.
    public List<ProductDTO> getProductsByAvailability(Boolean available) {
        return productRepository.findAll().stream()
            .filter(Objects::nonNull)
            .filter(product -> Optional.ofNullable(available)
                .map(av -> product.isAvailable() == av)
                .orElse(true))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

}
