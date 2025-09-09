package bo.edu.ucb.producto.warehouse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.producto.warehouse.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Find product by name
    Optional<Product> findByName(String name);
    
    // Find product by sku
    Optional<Product> findBySku(String sku);
    
    // Find products by category
    List<Product> findByCategory(String category);
    
    // Find products with low stock
    List<Product> findByStockQuantityLessThanEqualAndStatus(Integer stockQuantity, Product.ProductStatus status);
    
}
