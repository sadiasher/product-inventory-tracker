package com.access.productInventoryTracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.access.productInventoryTracker.dto.ProductDTO;
import com.access.productInventoryTracker.model.Product;
import com.access.productInventoryTracker.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setupMockProducts() {
        List<Product> mockProducts = Arrays.asList(
            new Product(1L, "Laptop", 1500.0, "Electronics", true),
            new Product(2L, "Smartphone", 800.0, "Electronics", false),
            new Product(3L, "Coffee Maker", 100.0, "Home Appliances", true),
            new Product(4L, "Blender", 150.0, "Home Appliances", true),
            new Product(5L, "T-Shirt", 30.0, "Apparel", true),
            new Product(6L, "Jeans", 45.0, "Apparel", true),
            new Product(7L, "Desk Lamp", 89.99, "Home Appliances", false),
            new Product(8L, "Wall Art", 120.0, "Home Decor", true),
            new Product(9L, "Sneakers", 75.0, "Apparel", true),
            new Product(10L, "Wristwatch", 250.0, "Accessories", false),
            new Product(11L, "Backpack", 60.0, "Accessories", true),
            new Product(12L, "Microwave Oven", 99.0, "Home Appliances", false),
            new Product(13L, "Floor Rug", 150.0, "Home Decor", true),
            new Product(14L, "Speaker", 300.0, "Electronics", true),
            new Product(15L, "E-reader", 200.0, "Electronics", false),
            new Product(16L, "Gaming Console", 499.99, "Electronics", true),
            new Product(17L, "Office Chair", 220.0, "Office Supplies", true),
            new Product(18L, "Pen Set", 29.99, "Office Supplies", true),
            new Product(19L, "Mountain Bike", 489.0, "Outdoor", true),
            new Product(20L, "Camping Tent", 270.0, "Outdoor", false)
        );

        Mockito.lenient().when(productRepository.findAll()).thenReturn(mockProducts);
        
        // Mock findProductsByCategory to return only available products matching the category
        Mockito.lenient()
            .when(productRepository.findProductsByCategory(org.mockito.ArgumentMatchers.anyString()))
            .thenAnswer(invocation -> {
                String cat = invocation.getArgument(0);
                return mockProducts.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(cat) && p.isAvailable())
                    .collect(Collectors.toList());
            });
        // Mock findByPriceRange to return products within the provided inclusive bounds
        Mockito.lenient()
            .when(productRepository.findByPriceRange(org.mockito.ArgumentMatchers.anyDouble(), org.mockito.ArgumentMatchers.anyDouble()))
            .thenAnswer(invocation -> {
                Double minArg = invocation.getArgument(0);
                Double maxArg = invocation.getArgument(1);
                double min = minArg != null ? minArg : 0.0;
                double max = maxArg != null ? maxArg : Double.MAX_VALUE;
                return mockProducts.stream()
                    .filter(product -> product.getPrice() >= min && product.getPrice() <= max)
                    .collect(Collectors.toList());
            });
    }

     @Test
    void shouldReturnProductsWithinRange() {

        List<ProductDTO> result =
                productService.getProductsByPriceRange(100.0, 800.0);

        assertEquals(12, result.size());

        assertTrue(result.stream()
                .allMatch(p -> p.getPrice() >= 100.0 && p.getPrice() <= 800.0));
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsInRange() {

        List<ProductDTO> result =
                productService.getProductsByPriceRange(2000.0, 3000.0);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleMinGreaterThanMax() {
        // When min > max are provided, they should be swapped internally
        List<ProductDTO> result =
                productService.getProductsByPriceRange(800.0, 100.0);

        // Should return same results as if 100.0, 800.0 were passed
        assertEquals(12, result.size());
    }

    @Test
    void shouldHandleNullMinAndMax() {
        List<ProductDTO> result =
                productService.getProductsByPriceRange(null, null);

        assertEquals(20, result.size());
    }

    // Tests for getProductsByCategory
    @Test
    void shouldReturnProductsByCategory() {
        // Arrange: "Electronics" category has 3 available products (Laptop, Speaker, Gaming Console)
        List<ProductDTO> result = productService.getProductsByCategory("Electronics");

        // Assert: verify correct count and all items belong to Electronics
        assertEquals(3, result.size());
        assertTrue(result.stream()
                .allMatch(p -> p.getCategory().equalsIgnoreCase("Electronics")));
    }

    @Test
    void shouldReturnEmptyListWhenCategoryNotFound() {
        // Arrange: search for a non-existent category
        List<ProductDTO> result = productService.getProductsByCategory("NonExistent");

        // Assert: verify empty list is returned
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void shouldReturnProductsByCategoryIgnoreCaseSensitive() {
        // Arrange: test case insensitivity with different case variations
        List<ProductDTO> resultLower = productService.getProductsByCategory("apparel");
        List<ProductDTO> resultUpper = productService.getProductsByCategory("APPAREL");
        List<ProductDTO> resultMixed = productService.getProductsByCategory("ApPaReL");

        // Assert: all queries return the same results regardless of case
        assertEquals(3, resultLower.size());
        assertEquals(3, resultUpper.size());
        assertEquals(3, resultMixed.size());
        assertEquals(resultLower.size(), resultUpper.size());
    }

    @Test
    void shouldReturnCorrectProductsForMultipleCategories() {
        // Test multiple distinct categories to ensure filtering works across data set
        List<ProductDTO> homeAppliancesResult = productService.getProductsByCategory("Home Appliances");
        List<ProductDTO> accessoriesResult = productService.getProductsByCategory("Accessories");
        List<ProductDTO> outdoorResult = productService.getProductsByCategory("Outdoor");

        // Assert: verify counts and filter integrity
        assertEquals(2, homeAppliancesResult.size()); // Coffee Maker, Blender
        assertEquals(1, accessoriesResult.size());    // Backpack
        assertEquals(1, outdoorResult.size());        // Mountain Bike
        
        // Verify no overlap between categories
        assertTrue(homeAppliancesResult.stream()
                .noneMatch(p -> p.getCategory().equalsIgnoreCase("Accessories")));
    }

    // Tests for getProductsByAvailability
    @Test
    void shouldReturnAvailableProducts() {
        // Arrange: filter for available products (true)
        List<ProductDTO> result = productService.getProductsByAvailability(true);

        // Assert: verify all returned products are available and count is correct
        assertEquals(14, result.size()); // Based on mock data: 14 available products
        assertTrue(result.stream()
                .allMatch(ProductDTO::isAvailable),
                "All returned products should be available");
    }

    @Test
    void shouldReturnUnavailableProducts() {
        // Arrange: filter for unavailable products (false)
        List<ProductDTO> result = productService.getProductsByAvailability(false);

        // Assert: verify all returned products are unavailable and count is correct
        assertEquals(6, result.size()); // Based on mock data: 6 unavailable products (Smartphone, Desk Lamp, Wristwatch, Microwave Oven, E-reader, Camping Tent)
        assertTrue(result.stream()
                .noneMatch(ProductDTO::isAvailable),
                "All returned products should be unavailable");
    }

    @Test
    void shouldReturnAllProductsWhenAvailabilityIsNull() {
        // Arrange: pass null for availability filter
        List<ProductDTO> result = productService.getProductsByAvailability(null);

        // Assert: verify all products are returned regardless of availability
        assertEquals(20, result.size(),
                "Should return all products when availability filter is null");
    }

    @Test
    void shouldVerifyAvailabilityDistribution() {
        // Arrange: get both available and unavailable products
        List<ProductDTO> availableProducts = productService.getProductsByAvailability(true);
        List<ProductDTO> unavailableProducts = productService.getProductsByAvailability(false);
        List<ProductDTO> allProducts = productService.getProductsByAvailability(null);

        // Assert: verify totals add up and no product appears in both lists
        assertEquals(availableProducts.size() + unavailableProducts.size(),
                allProducts.size(),
                "Sum of available and unavailable should equal all products");
        
        // Verify no product ID appears in both lists
        assertTrue(availableProducts.stream()
                .map(ProductDTO::getId)
                .noneMatch(id -> unavailableProducts.stream()
                        .map(ProductDTO::getId)
                        .anyMatch(id::equals)),
                "Available and unavailable product lists should not overlap");
    }

    @Test
    void shouldHandleEdgeCaseAvailabilityFiltering() {
        // Arrange: test boundary between available and unavailable
        List<ProductDTO> available = productService.getProductsByAvailability(true);
        List<ProductDTO> unavailable = productService.getProductsByAvailability(false);

        // Assert: verify distinct products in each list and boolean values are consistent
        for (ProductDTO product : available) {
            assertTrue(product.isAvailable(),
                    "Product " + product.getId() + " should be available");
        }

        for (ProductDTO product : unavailable) {
            assertFalse(product.isAvailable(),
                    "Product " + product.getId() + " should be unavailable");
        }
    }

}