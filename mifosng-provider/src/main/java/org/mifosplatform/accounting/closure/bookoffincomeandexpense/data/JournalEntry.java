/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.bookoffincomeandexpense.data;


import java.util.List;

public class JournalEntry {
    private Long officeId;
    private String transactionDate;
    private String comments;
    private String referenceNumber;
    private boolean useAccountingRule;
    private String currencyCode;
    private List<SingleDebitOrCreditEntry> debits;
    private List<SingleDebitOrCreditEntry> credits;

    public JournalEntry(final Long officeId, final String transactionDate,
                        final String comments, final List<SingleDebitOrCreditEntry> credits,
                        final List<SingleDebitOrCreditEntry> debits,
                        final String referenceNumber,final  boolean useAccountingRule,
                        final String currencyCode) {
        this.officeId = officeId;
        this.transactionDate = transactionDate;
        this.comments = comments;
        this.credits = credits;
        this.debits = debits;
        this.referenceNumber = referenceNumber;
        this.useAccountingRule = useAccountingRule;
        this.currencyCode = currencyCode;
    }

    public Long getOfficeId() {return this.officeId;}

    public List<SingleDebitOrCreditEntry> getCredits() {return this.credits;}

    public String getTransactionDate() {return this.transactionDate;}

    public String getComments() {return this.comments;}

    public String getReferenceNumber() {return this.referenceNumber;}

    public boolean isUseAccountingRule() {return this.useAccountingRule;}

    public String getCurrencyCode() {return this.currencyCode;}

    public List<SingleDebitOrCreditEntry> getDebits() {return this.debits;}
}
