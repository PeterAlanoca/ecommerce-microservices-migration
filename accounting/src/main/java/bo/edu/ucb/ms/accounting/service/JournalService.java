package bo.edu.ucb.ms.accounting.service;

import bo.edu.ucb.ms.accounting.dto.JournalDto;
import bo.edu.ucb.ms.accounting.entity.Journal;
import bo.edu.ucb.ms.accounting.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for Journal business logic
 */
@Service
@Transactional
public class JournalService {
    
    @Autowired
    private JournalRepository journalRepository;
    
    /**
     * Create a new journal entry
     * @param journalDto Journal entry information
     * @return Created Journal entity
     */
    public Journal createJournalEntry(JournalDto journalDto) {
        // Generate unique journal entry number if not provided
        if (journalDto.getJournalEntryNumber() == null || journalDto.getJournalEntryNumber().isEmpty()) {
            journalDto.setJournalEntryNumber("JE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        // Convert DTO to entity
        Journal journal = convertToEntity(journalDto);
        
        // Save and return
        return journalRepository.save(journal);
    }
    
    /**
     * Get journal entry by journal entry number
     * @param journalEntryNumber Journal entry number
     * @return JournalDto if found, null otherwise
     */
    public JournalDto getJournalEntryByNumber(String journalEntryNumber) {
        Optional<Journal> journal = journalRepository.findByJournalEntryNumber(journalEntryNumber);
        return journal.map(this::convertToDto).orElse(null);
    }
    
    /**
     * Get all journal entries
     * @return List of JournalDto
     */
    public List<JournalDto> getAllJournalEntries() {
        List<Journal> journals = journalRepository.findAll();
        return journals.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Get journal entries by status
     * @param status Journal entry status
     * @return List of JournalDto
     */
    public List<JournalDto> getJournalEntriesByStatus(String status) {
        Journal.Status journalStatus = Journal.Status.valueOf(status.toUpperCase());
        List<Journal> journals = journalRepository.findByStatus(journalStatus);
        return journals.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Get journal entries by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of JournalDto
     */
    public List<JournalDto> getJournalEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Journal> journals = journalRepository.findByTransactionDateBetween(startDate, endDate);
        return journals.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Get journal entries by account code
     * @param accountCode Account code
     * @return List of JournalDto
     */
    public List<JournalDto> getJournalEntriesByAccountCode(String accountCode) {
        List<Journal> journals = journalRepository.findByAccountCode(accountCode);
        return journals.stream().map(this::convertToDto).toList();
    }
    
    /**
     * Post a journal entry (change status from draft to posted)
     * @param journalEntryNumber Journal entry number
     * @return Updated JournalDto
     */
    public JournalDto postJournalEntry(String journalEntryNumber) {
        Optional<Journal> journalOpt = journalRepository.findByJournalEntryNumber(journalEntryNumber);
        if (journalOpt.isPresent()) {
            Journal journal = journalOpt.get();
            journal.post();
            Journal updatedJournal = journalRepository.save(journal);
            return convertToDto(updatedJournal);
        }
        return null;
    }
    
    /**
     * Approve a journal entry
     * @param journalEntryNumber Journal entry number
     * @param approvedBy User who approved
     * @return Updated JournalDto
     */
    public JournalDto approveJournalEntry(String journalEntryNumber, String approvedBy) {
        Optional<Journal> journalOpt = journalRepository.findByJournalEntryNumber(journalEntryNumber);
        if (journalOpt.isPresent()) {
            Journal journal = journalOpt.get();
            journal.approve(approvedBy);
            Journal updatedJournal = journalRepository.save(journal);
            return convertToDto(updatedJournal);
        }
        return null;
    }
    
    /**
     * Reverse a journal entry
     * @param journalEntryNumber Journal entry number
     * @param reversedByEntry Entry number that reverses this one
     * @return Updated JournalDto
     */
    public JournalDto reverseJournalEntry(String journalEntryNumber, String reversedByEntry) {
        Optional<Journal> journalOpt = journalRepository.findByJournalEntryNumber(journalEntryNumber);
        if (journalOpt.isPresent()) {
            Journal journal = journalOpt.get();
            journal.reverse(reversedByEntry);
            Journal updatedJournal = journalRepository.save(journal);
            return convertToDto(updatedJournal);
        }
        return null;
    }
    
    /**
     * Convert Journal entity to JournalDto
     * @param journal Journal entity
     * @return JournalDto
     */
    private JournalDto convertToDto(Journal journal) {
        JournalDto dto = new JournalDto();
        dto.setId(journal.getId());
        dto.setJournalEntryNumber(journal.getJournalEntryNumber());
        dto.setTransactionDate(journal.getTransactionDate());
        dto.setPostingDate(journal.getPostingDate());
        dto.setAccountCode(journal.getAccountCode());
        dto.setAccountName(journal.getAccountName());
        dto.setDescription(journal.getDescription());
        dto.setReferenceNumber(journal.getReferenceNumber());
        dto.setDebitAmount(journal.getDebitAmount());
        dto.setCreditAmount(journal.getCreditAmount());
        dto.setBalanceType(journal.getBalanceType() != null ? journal.getBalanceType().name() : null);
        dto.setDepartment(journal.getDepartment());
        dto.setCostCenter(journal.getCostCenter());
        dto.setProjectCode(journal.getProjectCode());
        dto.setCurrencyCode(journal.getCurrencyCode());
        dto.setExchangeRate(journal.getExchangeRate());
        dto.setSourceDocument(journal.getSourceDocument());
        dto.setCreatedBy(journal.getCreatedBy());
        dto.setApprovedBy(journal.getApprovedBy());
        dto.setApprovalDate(journal.getApprovalDate());
        dto.setStatus(journal.getStatus() != null ? journal.getStatus().name() : null);
        dto.setReversedByEntry(journal.getReversedByEntry());
        dto.setNotes(journal.getNotes());
        dto.setCreatedAt(journal.getCreatedAt());
        dto.setUpdatedAt(journal.getUpdatedAt());
        return dto;
    }
    
    /**
     * Convert JournalDto to Journal entity
     * @param dto JournalDto
     * @return Journal entity
     */
    private Journal convertToEntity(JournalDto dto) {
        Journal journal = new Journal();
        journal.setJournalEntryNumber(dto.getJournalEntryNumber());
        journal.setTransactionDate(dto.getTransactionDate());
        journal.setPostingDate(dto.getPostingDate());
        journal.setAccountCode(dto.getAccountCode());
        journal.setAccountName(dto.getAccountName());
        journal.setDescription(dto.getDescription());
        journal.setReferenceNumber(dto.getReferenceNumber());
        journal.setDebitAmount(dto.getDebitAmount());
        journal.setCreditAmount(dto.getCreditAmount());
        
        if (dto.getBalanceType() != null) {
            journal.setBalanceType(Journal.BalanceType.valueOf(dto.getBalanceType()));
        }
        
        journal.setDepartment(dto.getDepartment());
        journal.setCostCenter(dto.getCostCenter());
        journal.setProjectCode(dto.getProjectCode());
        journal.setCurrencyCode(dto.getCurrencyCode());
        journal.setExchangeRate(dto.getExchangeRate());
        journal.setSourceDocument(dto.getSourceDocument());
        journal.setCreatedBy(dto.getCreatedBy());
        journal.setApprovedBy(dto.getApprovedBy());
        journal.setApprovalDate(dto.getApprovalDate());
        
        if (dto.getStatus() != null) {
            journal.setStatus(Journal.Status.valueOf(dto.getStatus()));
        }
        
        journal.setReversedByEntry(dto.getReversedByEntry());
        journal.setNotes(dto.getNotes());
        return journal;
    }
}

