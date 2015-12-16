/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.bookoffincomeandexpense.data;

import java.math.BigDecimal;


public class SingleDebitOrCreditEntry {

    private final Long glAccountId;
    private final String glAccountName;
    private final BigDecimal amount;
    private final String comments;

    public SingleDebitOrCreditEntry(Long glAccountId, String glAccountName, BigDecimal amount, String comments) {
        this.glAccountId = glAccountId;
        this.glAccountName = glAccountName;
        this.amount = amount;
        this.comments = comments;
    }

    public Long getGlAccountId() {return this.glAccountId;}

    public String getGlAccountName() {return this.glAccountName;}

    public BigDecimal getAmount() {return this.amount;}

    public String getComments() {return this.comments;}
}
