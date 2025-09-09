package bo.edu.ucb.ms.accounting.repository;

import bo.edu.ucb.ms.accounting.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    
    // Find journal entry by journal entry number
    Optional<Journal> findByJournalEntryNumber(String journalEntryNumber);
    
    // Find journal entries by account code
    List<Journal> findByAccountCode(String accountCode);
    
    // Find journal entries by account name
    List<Journal> findByAccountNameContainingIgnoreCase(String accountName);
    
    // Find journal entries by status
    List<Journal> findByStatus(Journal.Status status);
    
    // Find journal entries by created by
    List<Journal> findByCreatedBy(String createdBy);
    
    // Find journal entries by department
    List<Journal> findByDepartment(String department);
    
    // Find journal entries by date range
    List<Journal> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find journal entries by specific date
    List<Journal> findByTransactionDate(LocalDate transactionDate);
    
    // Find journal entries by posting date range
    List<Journal> findByPostingDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find journal entries with debit amount greater than specified value
    @Query("SELECT j FROM Journal j WHERE j.debitAmount > :amount")
    List<Journal> findJournalEntriesWithDebitAmountGreaterThan(@Param("amount") BigDecimal amount);
    
    // Find journal entries with credit amount greater than specified value
    @Query("SELECT j FROM Journal j WHERE j.creditAmount > :amount")
    List<Journal> findJournalEntriesWithCreditAmountGreaterThan(@Param("amount") BigDecimal amount);
    
    // Find journal entries by reference number
    List<Journal> findByReferenceNumber(String referenceNumber);
    
    // Find journal entries by source document
    List<Journal> findBySourceDocumentContainingIgnoreCase(String sourceDocument);
    
    // Get total debit amount by date range
    @Query("SELECT COALESCE(SUM(j.debitAmount), 0) FROM Journal j WHERE j.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalDebitAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Get total credit amount by date range
    @Query("SELECT COALESCE(SUM(j.creditAmount), 0) FROM Journal j WHERE j.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalCreditAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Get journal entries summary by status
    @Query("SELECT j.status, COUNT(j), COALESCE(SUM(j.debitAmount), 0), COALESCE(SUM(j.creditAmount), 0) FROM Journal j GROUP BY j.status")
    List<Object[]> getJournalEntriesSummaryByStatus();
    
    // Get journal entries by account and date range
    @Query("SELECT j FROM Journal j WHERE j.accountCode = :accountCode AND j.transactionDate BETWEEN :startDate AND :endDate")
    List<Journal> findJournalEntriesByAccountAndDateRange(@Param("accountCode") String accountCode, 
                                                          @Param("startDate") LocalDate startDate, 
                                                          @Param("endDate") LocalDate endDate);
    
    // Find draft journal entries
    @Query("SELECT j FROM Journal j WHERE j.status = 'draft'")
    List<Journal> findDraftJournalEntries();
    
    // Find posted journal entries
    @Query("SELECT j FROM Journal j WHERE j.status = 'posted'")
    List<Journal> findPostedJournalEntries();
    
    // Find reversed journal entries
    @Query("SELECT j FROM Journal j WHERE j.status = 'reversed'")
    List<Journal> findReversedJournalEntries();
    
    // Get monthly journal entries report
    @Query("SELECT YEAR(j.transactionDate), MONTH(j.transactionDate), COUNT(j), COALESCE(SUM(j.debitAmount), 0), COALESCE(SUM(j.creditAmount), 0) " +
           "FROM Journal j " +
           "WHERE j.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(j.transactionDate), MONTH(j.transactionDate) " +
           "ORDER BY YEAR(j.transactionDate), MONTH(j.transactionDate)")
    List<Object[]> getMonthlyJournalEntriesReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

