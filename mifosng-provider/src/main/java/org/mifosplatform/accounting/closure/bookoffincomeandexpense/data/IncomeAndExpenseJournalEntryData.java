/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.bookoffincomeandexpense.data;


import org.joda.time.LocalDate;
import org.mifosplatform.accounting.glaccount.domain.GLAccountType;

import java.math.BigDecimal;

public class IncomeAndExpenseJournalEntryData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final Long accountId;
    @SuppressWarnings("unused")
    private final Long officeId;
    @SuppressWarnings("unused")
    private final LocalDate entryDate;
    @SuppressWarnings("unused")
    private final boolean reversed;
    @SuppressWarnings("unused")
    private final boolean isRunningBalanceCalculated;
    @SuppressWarnings("unused")
    private final String comments;
    @SuppressWarnings("unused")
    private final BigDecimal officeRunningBalance;
    @SuppressWarnings("unused")
    private final BigDecimal organizationRunningBalance;
    @SuppressWarnings("unused")
    private final int accountTypeId;
    @SuppressWarnings("unused")
    private final int entryTypeId;
    @SuppressWarnings("unused")
    private final String glAccountName;
    @SuppressWarnings("unused")
    private final String officeName;







    public IncomeAndExpenseJournalEntryData(final Long id, final Long accountId, final Long officeId, final LocalDate entryDate, final boolean reversed,
            final boolean isRunningBalanceCalculated, final String comments, final BigDecimal officeRunningBalance,
            final BigDecimal organizationRunningBalance, final int accountTypeId, final int entryTypeId,
            final String glAccountName, final String officeName) {
        this.id = id;
        this.accountId = accountId;
        this.officeId = officeId;
        this.entryDate = entryDate;
        this.reversed = reversed;
        this.isRunningBalanceCalculated = isRunningBalanceCalculated;
        this.comments = comments;
        this.officeRunningBalance = officeRunningBalance;
        this.organizationRunningBalance = organizationRunningBalance;
        this.accountTypeId = accountTypeId;
        this.entryTypeId = entryTypeId;
        this.glAccountName = glAccountName;
        this.officeName = officeName;
    }

    public Long getId() {
        return this.id;
    }

    public int getEntryTypeId() {return this.entryTypeId;}

    public Long getAccountId() {
        return this.accountId;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public LocalDate getEntryDate() {
        return this.entryDate;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public boolean isRunningBalanceCalculated() {
        return this.isRunningBalanceCalculated;
    }

    public String getComments() {
        return this.comments;
    }

    public BigDecimal getOfficeRunningBalance() {return this.officeRunningBalance;}

    public BigDecimal getOrganizationRunningBalance() {
        return this.organizationRunningBalance;
    }

    public int getAccountTypeId() {
        return this.accountTypeId;
    }

    public boolean isIncomeAccountType(){
       return  (this.accountTypeId == (GLAccountType.INCOME.getValue()));
    }

    public boolean isExpenseAccountType(){
        return (this.accountTypeId == GLAccountType.EXPENSE.getValue());
    }

    public String getGlAccountName() {return this.glAccountName;}

    public String getOfficeName() {return this.officeName;}
}
