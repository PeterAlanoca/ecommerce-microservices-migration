package bo.edu.ucb.ms.sales.service;

import bo.edu.ucb.ms.sales.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for Product Service
 * Handles communication with the Product microservice
 */
@FeignClient(name = "producto")
public interface ProductServiceClient {
    
    /**
     * Get product by ID
     * @param id Product ID
     * @return ProductDto with product information
     */
    @GetMapping("/api/warehouse/products/{id}")
    ProductDto getProduct(@PathVariable("id") Integer id);
    
    /**
     * Update product stock
     * @param id Product ID
     * @param productDto Product information with updated stock
     * @return Updated ProductDto
     */
    @PutMapping("/api/warehouse/products/{id}")
    ProductDto updateProduct(@PathVariable("id") Integer id, @RequestBody ProductDto productDto);
}

