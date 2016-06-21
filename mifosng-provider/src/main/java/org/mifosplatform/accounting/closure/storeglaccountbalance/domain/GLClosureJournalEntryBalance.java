/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.storeglaccountbalance.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.accounting.closure.domain.GLClosure;
import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "acc_gl_closure_journal_entry_balance")
public class GLClosureJournalEntryBalance extends AbstractAuditableCustom<AppUser, Long> {
    /**
     * @see http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html
     **/
    private static final long serialVersionUID = 4250549922319698128L;

    @ManyToOne
    @JoinColumn(name = "closure_id", nullable = false)
    private GLClosure glClosure;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private GLAccount glAccount;
    
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;
    
    /**
     * Protected constructor
     */
    protected GLClosureJournalEntryBalance() { }

    /**
     * @param glClosure
     * @param glAccount
     * @param amount
     * @param deleted
     */
    private GLClosureJournalEntryBalance(final GLClosure glClosure, final GLAccount glAccount, 
            final BigDecimal amount, final boolean deleted) {
        this.glClosure = glClosure;
        this.glAccount = glAccount;
        this.amount = amount;
        this.deleted = deleted;
    }
    
    /**
     * Creates a new {@link GLClosureJournalEntryBalance} entity
     * 
     * @param glClosure {@link GLClosure} entity
     * @param glAccount {@link GLAccount} entity
     * @param amount the account running balance at period closure
     * @return {@link GLClosureJournalEntryBalance} entity
     */
    public static GLClosureJournalEntryBalance newGLClosureBalance(final GLClosure glClosure, final GLAccount glAccount, 
            final BigDecimal amount) {
        return new GLClosureJournalEntryBalance(glClosure, glAccount, amount, false);
    }

    /**
     * @return the glClosure
     */
    public GLClosure getGlClosure() {
        return glClosure;
    }

    /**
     * @return the glAccount
     */
    public GLAccount getGlAccount() {
        return glAccount;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Set the deleted property to true
     */
    public void delete() {
        this.deleted = true;
    }
}
