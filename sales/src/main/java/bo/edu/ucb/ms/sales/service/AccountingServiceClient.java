package bo.edu.ucb.ms.sales.service;

import bo.edu.ucb.ms.sales.dto.JournalDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for Accounting Service
 * Handles communication with the Accounting microservice
 */
@FeignClient(name = "accounting")
public interface AccountingServiceClient {
    
    /**
     * Create a journal entry
     * @param journalDto Journal entry information
     * @return Created JournalDto
     */
    @PostMapping("/api/accounting/journal")
    JournalDto createJournalEntry(@RequestBody JournalDto journalDto);
}

