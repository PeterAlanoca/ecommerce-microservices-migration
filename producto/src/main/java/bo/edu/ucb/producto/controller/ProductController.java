package bo.edu.ucb.producto.controller;

import bo.edu.ucb.producto.warehouse.dto.ProductDto;
import bo.edu.ucb.producto.warehouse.entity.Product;
import bo.edu.ucb.producto.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Product operations
 */
@RestController
@RequestMapping("/api/warehouse")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * Get all products
     * @return List of ProductDto
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    /**
     * Get product by ID
     * @param id Product ID
     * @return ProductDto
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Integer id) {
        ProductDto product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Create a new product
     * @param productDto Product information
     * @return Created ProductDto
     */
    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        try {
            ProductDto createdProduct = productService.createProduct(productDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update product
     * @param id Product ID
     * @param productDto Updated product information
     * @return Updated ProductDto
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductDto productDto) {
        try {
            ProductDto updatedProduct = productService.updateProduct(id, productDto);
            if (updatedProduct != null) {
                return ResponseEntity.ok(updatedProduct);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update product stock
     * @param id Product ID
     * @param stockQuantity New stock quantity
     * @return Updated ProductDto
     */
    @PutMapping("/products/{id}/stock")
    public ResponseEntity<ProductDto> updateProductStock(@PathVariable Integer id, @RequestParam Integer stockQuantity) {
        try {
            ProductDto updatedProduct = productService.updateProductStock(id, stockQuantity);
            if (updatedProduct != null) {
                return ResponseEntity.ok(updatedProduct);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get products by category
     * @param category Product category
     * @return List of ProductDto
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable String category) {
        List<ProductDto> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    /**
     * Get products with low stock
     * @return List of ProductDto
     */
    @GetMapping("/products/low-stock")
    public ResponseEntity<List<ProductDto>> getProductsWithLowStock() {
        List<ProductDto> products = productService.getProductsWithLowStock();
        return ResponseEntity.ok(products);
    }
}

