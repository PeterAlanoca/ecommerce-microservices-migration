package bo.edu.ucb.ms.sales.controller;

import bo.edu.ucb.ms.sales.dto.SaleDto;
import bo.edu.ucb.ms.sales.service.SalesService;
import bo.edu.ucb.ms.sales.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Sales operations
 */
@RestController
@RequestMapping("/api/sales")
public class SalesController {
    
    @Autowired
    private SalesService salesService;
    
    /**
     * Create a new sale (Compatible with Monolith API)
     * @param productDto Product information in request body
     * @param quantity Quantity to sell (query parameter)
     * @return Created SaleDto
     */
    @PostMapping
    public ResponseEntity<SaleDto> createSale(
            @Valid @RequestBody ProductDto productDto,
            @RequestParam(defaultValue = "1") Integer quantity) {
        
        try {
            SaleDto sale = salesService.createSale(productDto.getId(), quantity, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(sale);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get sale by sale number
     * @param saleNumber Sale number
     * @return SaleDto
     */
    @GetMapping("/{saleNumber}")
    public ResponseEntity<SaleDto> getSaleByNumber(@PathVariable String saleNumber) {
        SaleDto sale = salesService.getSaleByNumber(saleNumber);
        if (sale != null) {
            return ResponseEntity.ok(sale);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all sales
     * @return List of SaleDto
     */
    @GetMapping
    public ResponseEntity<List<SaleDto>> getAllSales() {
        List<SaleDto> sales = salesService.getAllSales();
        return ResponseEntity.ok(sales);
    }
    
    /**
     * Get sales by customer name
     * @param customerName Customer name
     * @return List of SaleDto
     */
    @GetMapping("/customer/{customerName}")
    public ResponseEntity<List<SaleDto>> getSalesByCustomer(@PathVariable String customerName) {
        List<SaleDto> sales = salesService.getSalesByCustomer(customerName);
        return ResponseEntity.ok(sales);
    }
    
    /**
     * Get sales by date range
     * @param startDate Start date (YYYY-MM-DD)
     * @param endDate End date (YYYY-MM-DD)
     * @return List of SaleDto
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<SaleDto>> getSalesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<SaleDto> sales = salesService.getSalesByDateRange(start, end);
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

