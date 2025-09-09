package bo.edu.ucb.ms.accounting.controller;

import bo.edu.ucb.ms.accounting.dto.JournalDto;
import bo.edu.ucb.ms.accounting.entity.Journal;
import bo.edu.ucb.ms.accounting.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Journal operations
 */
@RestController
@RequestMapping("/api/accounting")
public class JournalController {
    
    @Autowired
    private JournalService journalService;
    
    /**
     * Create a new journal entry
     * @param journalDto Journal entry information
     * @return Created Journal entity
     */
    @PostMapping("/journal")
    public ResponseEntity<Journal> createJournalEntry(@Valid @RequestBody JournalDto journalDto) {
        try {
            Journal journal = journalService.createJournalEntry(journalDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(journal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get journal entry by journal entry number
     * @param journalEntryNumber Journal entry number
     * @return JournalDto
     */
    @GetMapping("/journal/{journalEntryNumber}")
    public ResponseEntity<JournalDto> getJournalEntryByNumber(@PathVariable String journalEntryNumber) {
        JournalDto journal = journalService.getJournalEntryByNumber(journalEntryNumber);
        if (journal != null) {
            return ResponseEntity.ok(journal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all journal entries
     * @return List of JournalDto
     */
    @GetMapping("/journal")
    public ResponseEntity<List<JournalDto>> getAllJournalEntries() {
        List<JournalDto> journals = journalService.getAllJournalEntries();
        return ResponseEntity.ok(journals);
    }
    
    /**
     * Get journal entries by status
     * @param status Journal entry status (draft, posted, reversed)
     * @return List of JournalDto
     */
    @GetMapping("/journal/status/{status}")
    public ResponseEntity<List<JournalDto>> getJournalEntriesByStatus(@PathVariable String status) {
        List<JournalDto> journals = journalService.getJournalEntriesByStatus(status);
        return ResponseEntity.ok(journals);
    }
    
    /**
     * Get journal entries by date range
     * @param startDate Start date (YYYY-MM-DD)
     * @param endDate End date (YYYY-MM-DD)
     * @return List of JournalDto
     */
    @GetMapping("/journal/date-range")
    public ResponseEntity<List<JournalDto>> getJournalEntriesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<JournalDto> journals = journalService.getJournalEntriesByDateRange(start, end);
            return ResponseEntity.ok(journals);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get journal entries by account code
     * @param accountCode Account code
     * @return List of JournalDto
     */
    @GetMapping("/journal/account/{accountCode}")
    public ResponseEntity<List<JournalDto>> getJournalEntriesByAccountCode(@PathVariable String accountCode) {
        List<JournalDto> journals = journalService.getJournalEntriesByAccountCode(accountCode);
        return ResponseEntity.ok(journals);
    }
    
    /**
     * Post a journal entry (change status from draft to posted)
     * @param journalEntryNumber Journal entry number
     * @return Updated JournalDto
     */
    @PutMapping("/journal/{journalEntryNumber}/post")
    public ResponseEntity<JournalDto> postJournalEntry(@PathVariable String journalEntryNumber) {
        JournalDto journal = journalService.postJournalEntry(journalEntryNumber);
        if (journal != null) {
            return ResponseEntity.ok(journal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Approve a journal entry
     * @param journalEntryNumber Journal entry number
     * @param approvedBy User who approved
     * @return Updated JournalDto
     */
    @PutMapping("/journal/{journalEntryNumber}/approve")
    public ResponseEntity<JournalDto> approveJournalEntry(
            @PathVariable String journalEntryNumber,
            @RequestParam String approvedBy) {
        
        JournalDto journal = journalService.approveJournalEntry(journalEntryNumber, approvedBy);
        if (journal != null) {
            return ResponseEntity.ok(journal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Reverse a journal entry
     * @param journalEntryNumber Journal entry number
     * @param reversedByEntry Entry number that reverses this one
     * @return Updated JournalDto
     */
    @PutMapping("/journal/{journalEntryNumber}/reverse")
    public ResponseEntity<JournalDto> reverseJournalEntry(
            @PathVariable String journalEntryNumber,
            @RequestParam String reversedByEntry) {
        
        JournalDto journal = journalService.reverseJournalEntry(journalEntryNumber, reversedByEntry);
        if (journal != null) {
            return ResponseEntity.ok(journal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

