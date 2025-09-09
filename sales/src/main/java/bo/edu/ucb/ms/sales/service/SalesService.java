package bo.edu.ucb.ms.sales.service;

import bo.edu.ucb.ms.sales.dto.JournalDto;
import bo.edu.ucb.ms.sales.dto.ProductDto;
import bo.edu.ucb.ms.sales.dto.SaleDto;
import bo.edu.ucb.ms.sales.entity.Sale;
import bo.edu.ucb.ms.sales.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for Sales business logic
 * Handles sales operations and communication with other microservices
 */
@Service
@Transactional
public class SalesService {
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
    @Autowired
    private AccountingServiceClient accountingServiceClient;
    
    /**
     * Create a new sale with complete transaction flow
     * @param productId Product ID to sell
     * @param quantity Quantity to sell
     * @param customerName Customer name
     * @return Created SaleDto
     */
    public SaleDto createSale(Integer productId, Integer quantity, String customerName) {
        // 1. Validate and get product from Product Service
        ProductDto product = productServiceClient.getProduct(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        
        // 2. Validate stock availability
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
        }
        
        // 3. Create sale entity
        Sale sale = new Sale();
        sale.setSaleNumber("SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        sale.setProductId(productId);
        sale.setQuantity(quantity);
        sale.setUnitPrice(product.getPrice());
        sale.setCustomerName(customerName);
        
        // Calculate total amount
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        sale.setTotalAmount(totalAmount);
        // finalAmount is calculated automatically by database trigger
        
        // 4. Save sale
        Sale savedSale = saleRepository.save(sale);
        
        // 5. Update product stock in Product Service
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productServiceClient.updateProduct(productId, product);
        
        // 6. Create accounting entries
        createAccountingEntries(savedSale);
        
        // 7. Convert to DTO and return
        return convertToDto(savedSale);
    }
    
    /**
     * Create accounting journal entries for a sale
     * @param sale Sale entity
     */
    private void createAccountingEntries(Sale sale) {
        try {
            // Create debit entry for Accounts Receivable
            JournalDto debitDto = createJournalDto(
                "1200", 
                "Cuentas por Cobrar", 
                "Venta - " + sale.getSaleNumber() + " - Producto ID: " + sale.getProductId(),
                sale.getTotalAmount(),
                "D",
                sale.getSaleNumber()
            );
            
            // Create credit entry for Sales Revenue
            JournalDto creditDto = createJournalDto(
                "4100", 
                "Ingresos por Ventas", 
                "Venta - " + sale.getSaleNumber() + " - Producto ID: " + sale.getProductId(),
                sale.getTotalAmount(),
                "C",
                sale.getSaleNumber()
            );
            
            // Send to Accounting Service with retry logic for duplicate key errors
            createJournalEntryWithRetry(debitDto);
            createJournalEntryWithRetry(creditDto);
            
            System.out.println("Accounting entries created for sale " + sale.getSaleNumber());
            
        } catch (Exception e) {
            System.err.println("Error creating accounting entries for sale " + sale.getSaleNumber() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create a JournalDto with the specified parameters
     */
    private JournalDto createJournalDto(String accountCode, String accountName, String description, 
                                      BigDecimal amount, String balanceType, String saleNumber) {
        JournalDto dto = new JournalDto();
        dto.setJournalEntryNumber(generateJournalEntryNumber());
        dto.setTransactionDate(LocalDate.now());
        dto.setAccountCode(accountCode);
        dto.setAccountName(accountName);
        dto.setDescription(description);
        dto.setCreatedBy("SALES_SERVICE");
        dto.setReferenceNumber(saleNumber);
        dto.setDepartment("VENTAS");
        dto.setNotes("Registro automático por venta de producto");
        
        if ("D".equals(balanceType)) {
            dto.setDebitAmount(amount);
            dto.setCreditAmount(BigDecimal.ZERO);
        } else {
            dto.setDebitAmount(BigDecimal.ZERO);
            dto.setCreditAmount(amount);
        }
        dto.setBalanceType(balanceType);
        
        return dto;
    }
    
    /**
     * Create journal entry with retry logic for duplicate key errors
     */
    private void createJournalEntryWithRetry(JournalDto dto) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                accountingServiceClient.createJournalEntry(dto);
                return; // Success, exit retry loop
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("duplicate key")) {
                    retryCount++;
                    if (retryCount < maxRetries) {
                        // Generate new journal entry number and retry
                        dto.setJournalEntryNumber(generateJournalEntryNumber());
                        System.out.println("Retrying journal entry creation with new number: " + dto.getJournalEntryNumber());
                        try {
                            Thread.sleep(10); // Small delay to ensure different timestamp
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        throw new RuntimeException("Failed to create journal entry after " + maxRetries + " retries", e);
                    }
                } else {
                    throw e; // Re-throw if it's not a duplicate key error
                }
            }
        }
    }
    
    /**
     * Generate a unique journal entry number
     * Enhanced version to prevent duplicates in concurrent calls
     * Format: JE-YYYYMMDD-HHMMSS (max 20 characters)
     * @return Unique journal entry number
     */
    private String generateJournalEntryNumber() {
        LocalDate now = LocalDate.now();
        long timestamp = System.currentTimeMillis() % 100000; // últimos 5 dígitos del timestamp
        // Add nano seconds to ensure uniqueness in concurrent calls
        long nanos = System.nanoTime() % 1000; // últimos 3 dígitos de nanosegundos
        return String.format("JE-%s-%05d%03d", 
            now.format(DateTimeFormatter.ofPattern("yyyyMMdd")), 
            timestamp,
            nanos);
    }
    
    
    /**
     * Get sale by sale number
     * @param saleNumber Sale number
     * @return SaleDto if found, null otherwise
     */
    public SaleDto getSaleByNumber(String saleNumber) {
        Optional<Sale> sale = saleRepository.findBySaleNumber(saleNumber);
        return sale.map(this::convertToDto).orElse(null);
    }
    
    /**
     * Get all sales
     * @return List of SaleDto
     */
    public List<SaleDto> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Get sales by customer name
     * @param customerName Customer name
     * @return List of SaleDto
     */
    public List<SaleDto> getSalesByCustomer(String customerName) {
        List<Sale> sales = saleRepository.findByCustomerNameContainingIgnoreCase(customerName);
        return sales.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Get sales by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of SaleDto
     */
    public List<SaleDto> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDate, endDate);
        return sales.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Convert Sale entity to SaleDto
     * @param sale Sale entity
     * @return SaleDto
     */
    private SaleDto convertToDto(Sale sale) {
        SaleDto dto = new SaleDto();
        dto.setId(sale.getId());
        dto.setSaleNumber(sale.getSaleNumber());
        dto.setProductId(sale.getProductId());
        dto.setQuantity(sale.getQuantity());
        dto.setUnitPrice(sale.getUnitPrice());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setDiscountPercentage(sale.getDiscountPercentage());
        dto.setDiscountAmount(sale.getDiscountAmount());
        dto.setFinalAmount(sale.getFinalAmount());
        dto.setSaleDate(sale.getSaleDate());
        dto.setCustomerId(sale.getCustomerId());
        dto.setCustomerName(sale.getCustomerName());
        dto.setSalesperson(sale.getSalesperson());
        dto.setPaymentMethod(sale.getPaymentMethod());
        dto.setPaymentStatus(sale.getPaymentStatus());
        dto.setNotes(sale.getNotes());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setUpdatedAt(sale.getUpdatedAt());
        return dto;
    }
}

