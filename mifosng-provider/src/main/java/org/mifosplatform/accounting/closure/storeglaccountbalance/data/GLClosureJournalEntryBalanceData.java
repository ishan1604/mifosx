/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.storeglaccountbalance.data;

import java.math.BigDecimal;

import org.joda.time.LocalDateTime;
import org.mifosplatform.accounting.closure.storeglaccountbalance.domain.GLClosureJournalEntryBalance;

/**
 * Immutable object representing a {@link GLClosureJournalEntryBalance} entity
 */
public class GLClosureJournalEntryBalanceData {
    private final Long id;
    private final Long glClosureId;
    private final Long glAccountId;
    private final BigDecimal amount;
    private final LocalDateTime createdDate;
    private final Long createdByUserId;
    private final String createdByUsername;
    private final LocalDateTime lastModifiedDate;
    private final Long lastModifiedByUserId;
    private final String lastModifiedByUsername;
    private final boolean deleted;
    
    /**
     * @param id
     * @param glClosure
     * @param glAccount
     * @param amount
     * @param createdDate
     * @param createdByUserId
     * @param createdByUsername
     * @param lastModifiedDate
     * @param lastModifiedByUserId
     * @param lastModifiedByUsername
     * @param deleted
     */
    private GLClosureJournalEntryBalanceData(Long id, Long glClosureId, Long glAccountId,
            BigDecimal amount, LocalDateTime createdDate, Long createdByUserId, String createdByUsername,
            LocalDateTime lastModifiedDate, Long lastModifiedByUserId, String lastModifiedByUsername, boolean deleted) {
        this.id = id;
        this.glClosureId = glClosureId;
        this.glAccountId = glAccountId;
        this.amount = amount;
        this.createdDate = createdDate;
        this.createdByUserId = createdByUserId;
        this.createdByUsername = createdByUsername;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.lastModifiedByUsername = lastModifiedByUsername;
        this.deleted = deleted;
    }
    
    /**
     * Creates a new {@link GLClosureJournalEntryBalanceData} object
     * 
     * @param glClosureJournalEntryBalance {@link GLClosureJournalEntryBalance} object
     * @return {@link GLClosureJournalEntryBalanceData} object
     */
    public static GLClosureJournalEntryBalanceData newGLClosureBalanceData(
            final GLClosureJournalEntryBalance glClosureJournalEntryBalance) {
        LocalDateTime createdDate = null;
        LocalDateTime lastModifiedDate = null;
        String createdByUsername = null;
        String lastModifiedByUsername = null;
        Long createdByUserId = null;
        Long lastModifiedByUserId = null;
        Long glClosureId = null;
        Long glAccountId = null;
        
        if (glClosureJournalEntryBalance.getCreatedBy() != null) {
            createdByUsername = glClosureJournalEntryBalance.getCreatedBy().getUsername();
            createdByUserId = glClosureJournalEntryBalance.getCreatedBy().getId();
        }
        
        if (glClosureJournalEntryBalance.getLastModifiedBy() != null) {
            lastModifiedByUsername = glClosureJournalEntryBalance.getLastModifiedBy().getUsername();
            lastModifiedByUserId = glClosureJournalEntryBalance.getLastModifiedBy().getId();
        }
        
        if (glClosureJournalEntryBalance.getCreatedDate() != null) {
            createdDate = new LocalDateTime(glClosureJournalEntryBalance.getCreatedDate());
        }
        
        if (glClosureJournalEntryBalance.getLastModifiedDate() != null) {
            lastModifiedDate = new LocalDateTime(glClosureJournalEntryBalance.getLastModifiedDate());
        }
        
        if (glClosureJournalEntryBalance.getGlAccount() != null) {
            glAccountId = glClosureJournalEntryBalance.getGlAccount().getId();
        }
        
        if (glClosureJournalEntryBalance.getGlClosure() != null) {
            glClosureId = glClosureJournalEntryBalance.getGlClosure().getId();
        }
        
        return new GLClosureJournalEntryBalanceData(glClosureJournalEntryBalance.getId(), 
                glClosureId, glAccountId, glClosureJournalEntryBalance.getAmount(), createdDate, 
                createdByUserId, createdByUsername, lastModifiedDate, lastModifiedByUserId, 
                lastModifiedByUsername, glClosureJournalEntryBalance.isDeleted());
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the glClosureId
     */
    public Long getGlClosureId() {
        return glClosureId;
    }

    /**
     * @return the glAccountId
     */
    public Long getGlAccountId() {
        return glAccountId;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return the createdDate
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * @return the createdByUserId
     */
    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    /**
     * @return the createdByUsername
     */
    public String getCreatedByUsername() {
        return createdByUsername;
    }

    /**
     * @return the lastModifiedDate
     */
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * @return the lastModifiedByUserId
     */
    public Long getLastModifiedByUserId() {
        return lastModifiedByUserId;
    }

    /**
     * @return the lastModifiedByUsername
     */
    public String getLastModifiedByUsername() {
        return lastModifiedByUsername;
    }

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }
}
