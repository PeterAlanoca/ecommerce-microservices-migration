package bo.edu.ucb.ms.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Journal entries to Accounting Service
 */
public class JournalDto {
    
    private String journalEntryNumber;
    private LocalDate transactionDate;
    private LocalDate postingDate;
    private String accountCode;
    private String accountName;
    private String description;
    private String referenceNumber;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String balanceType;
    private String department;
    private String costCenter;
    private String projectCode;
    private String currencyCode;
    private BigDecimal exchangeRate;
    private String sourceDocument;
    private String createdBy;
    private String approvedBy;
    private LocalDateTime approvalDate;
    private String status;
    private String reversedByEntry;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public JournalDto() {}
    
    // Constructor with required fields
    public JournalDto(String accountCode, String accountName, String description, BigDecimal amount, String balanceType, String createdBy) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.description = description;
        this.createdBy = createdBy;
        this.transactionDate = LocalDate.now();
        this.postingDate = LocalDate.now();
        this.currencyCode = "USD";
        this.exchangeRate = BigDecimal.ONE;
        this.status = "draft";
        
        if ("D".equals(balanceType)) {
            this.debitAmount = amount;
            this.creditAmount = BigDecimal.ZERO;
        } else {
            this.debitAmount = BigDecimal.ZERO;
            this.creditAmount = amount;
        }
    }
    
    // Getters and Setters
    public String getJournalEntryNumber() {
        return journalEntryNumber;
    }
    
    public void setJournalEntryNumber(String journalEntryNumber) {
        this.journalEntryNumber = journalEntryNumber;
    }
    
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public LocalDate getPostingDate() {
        return postingDate;
    }
    
    public void setPostingDate(LocalDate postingDate) {
        this.postingDate = postingDate;
    }
    
    public String getAccountCode() {
        return accountCode;
    }
    
    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public BigDecimal getDebitAmount() {
        return debitAmount;
    }
    
    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }
    
    public BigDecimal getCreditAmount() {
        return creditAmount;
    }
    
    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }
    
    public String getBalanceType() {
        return balanceType;
    }
    
    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getCostCenter() {
        return costCenter;
    }
    
    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }
    
    public String getProjectCode() {
        return projectCode;
    }
    
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    
    public String getSourceDocument() {
        return sourceDocument;
    }
    
    public void setSourceDocument(String sourceDocument) {
        this.sourceDocument = sourceDocument;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReversedByEntry() {
        return reversedByEntry;
    }
    
    public void setReversedByEntry(String reversedByEntry) {
        this.reversedByEntry = reversedByEntry;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "JournalDto{" +
                "journalEntryNumber='" + journalEntryNumber + '\'' +
                ", transactionDate=" + transactionDate +
                ", accountCode='" + accountCode + '\'' +
                ", accountName='" + accountName + '\'' +
                ", description='" + description + '\'' +
                ", debitAmount=" + debitAmount +
                ", creditAmount=" + creditAmount +
                ", balanceType='" + balanceType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

