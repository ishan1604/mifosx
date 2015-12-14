/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.bookoffincomeandexpense.domain;

import org.mifosplatform.accounting.closure.domain.GLClosure;
import org.mifosplatform.organisation.office.domain.Office;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "acc_income_and_expense_bookings",uniqueConstraints = { @UniqueConstraint(columnNames = { "journal_entry_transaction_id" }, name = "journal_entry_transaction_id") })
public class IncomeAndExpenseBooking extends AbstractPersistable<Long>  {

    @ManyToOne
    @JoinColumn(name = "gl_closure_id", nullable = false)
    private GLClosure glClosure;
    @Column(name = "journal_entry_transaction_id",nullable = false)
    private String transactionId;
    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;
    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;


    public IncomeAndExpenseBooking(final GLClosure glClosure, final String transactionId,
                                   final Office office, final boolean reversed) {
        this.glClosure = glClosure;
        this.transactionId = transactionId;
        this.office = office;
        this.reversed = reversed;
    }

    protected IncomeAndExpenseBooking() {
    }

    public static IncomeAndExpenseBooking createNew(final GLClosure glClosure, final String transactionId, final Office office, final boolean reversed){
        return new IncomeAndExpenseBooking(glClosure,transactionId,office,reversed);
    }

    public GLClosure getGlClosure() {return this.glClosure;}

    public String getTransactionId() {return this.transactionId;}

    public Office getOffice() {return this.office;}

    public boolean isReversed() {return this.reversed;}

    public void updateReversed(boolean reversed) {this.reversed = reversed;}
}
