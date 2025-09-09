package bo.edu.ucb.producto.service;

import bo.edu.ucb.producto.warehouse.dto.ProductDto;
import bo.edu.ucb.producto.warehouse.entity.Product;
import bo.edu.ucb.producto.warehouse.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Product business logic
 */
@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Get all products
     * @return List of ProductDto
     */
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Get product by ID
     * @param id Product ID
     * @return ProductDto if found, null otherwise
     */
    public ProductDto getProductById(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(this::convertToDto).orElse(null);
    }
    
    /**
     * Create a new product
     * @param productDto Product information
     * @return Created ProductDto
     */
    public ProductDto createProduct(ProductDto productDto) {
        Product product = convertToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }
    
    /**
     * Update product
     * @param id Product ID
     * @param productDto Updated product information
     * @return Updated ProductDto
     */
    public ProductDto updateProduct(Integer id, ProductDto productDto) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            updateProductFromDto(product, productDto);
            Product updatedProduct = productRepository.save(product);
            return convertToDto(updatedProduct);
        }
        return null;
    }
    
    /**
     * Update product stock
     * @param id Product ID
     * @param stockQuantity New stock quantity
     * @return Updated ProductDto
     */
    public ProductDto updateProductStock(Integer id, Integer stockQuantity) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(stockQuantity);
            Product updatedProduct = productRepository.save(product);
            return convertToDto(updatedProduct);
        }
        return null;
    }
    
    /**
     * Get products by category
     * @param category Product category
     * @return List of ProductDto
     */
    public List<ProductDto> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        return products.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Get products with low stock
     * @return List of ProductDto
     */
    public List<ProductDto> getProductsWithLowStock() {
        List<Product> products = productRepository.findByStockQuantityLessThanEqualAndStatus(
            productRepository.findAll().stream()
                .mapToInt(p -> p.getMinStockLevel())
                .min()
                .orElse(0), 
            Product.ProductStatus.active
        );
        return products.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Convert Product entity to ProductDto
     * @param product Product entity
     * @return ProductDto
     */
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setCost(product.getCost());
        dto.setSku(product.getSku());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setMinStockLevel(product.getMinStockLevel());
        dto.setMaxStockLevel(product.getMaxStockLevel());
        dto.setSupplier(product.getSupplier());
        dto.setBrand(product.getBrand());
        dto.setWeight(product.getWeight());
        dto.setDimensions(product.getDimensions());
        dto.setStatus(product.getStatus() != null ? product.getStatus().name() : null);
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
    
    /**
     * Convert ProductDto to Product entity
     * @param dto ProductDto
     * @return Product entity
     */
    private Product convertToEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setCost(dto.getCost());
        product.setSku(dto.getSku());
        product.setStockQuantity(dto.getStockQuantity());
        product.setMinStockLevel(dto.getMinStockLevel());
        product.setMaxStockLevel(dto.getMaxStockLevel());
        product.setSupplier(dto.getSupplier());
        product.setBrand(dto.getBrand());
        product.setWeight(dto.getWeight());
        product.setDimensions(dto.getDimensions());
        
        if (dto.getStatus() != null) {
            product.setStatus(Product.ProductStatus.valueOf(dto.getStatus()));
        }
        
        return product;
    }
    
    /**
     * Update Product entity from ProductDto
     * @param product Product entity to update
     * @param dto ProductDto with new values
     */
    private void updateProductFromDto(Product product, ProductDto dto) {
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getCost() != null) product.setCost(dto.getCost());
        if (dto.getSku() != null) product.setSku(dto.getSku());
        if (dto.getStockQuantity() != null) product.setStockQuantity(dto.getStockQuantity());
        if (dto.getMinStockLevel() != null) product.setMinStockLevel(dto.getMinStockLevel());
        if (dto.getMaxStockLevel() != null) product.setMaxStockLevel(dto.getMaxStockLevel());
        if (dto.getSupplier() != null) product.setSupplier(dto.getSupplier());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());
        if (dto.getWeight() != null) product.setWeight(dto.getWeight());
        if (dto.getDimensions() != null) product.setDimensions(dto.getDimensions());
        if (dto.getStatus() != null) product.setStatus(Product.ProductStatus.valueOf(dto.getStatus()));
    }
}

