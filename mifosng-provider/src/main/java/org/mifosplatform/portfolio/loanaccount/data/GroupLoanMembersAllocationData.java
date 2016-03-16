/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import org.mifosplatform.portfolio.client.data.ClientData;

import java.math.BigDecimal;

/**
 * Immutable data object for loan charge data.
 */
public class GroupLoanMembersAllocationData {

    private final Long id;
    private final Long loanId;
    private final ClientData clientData;
    private final BigDecimal amount;

    /**
     * used when populating with details from charge definition (for crud on
     * charges)
     */
    public static GroupLoanMembersAllocationData newOne(final Long id, final Long loanId, final ClientData clientData, final BigDecimal amount) {
        return new GroupLoanMembersAllocationData(id, loanId, clientData, amount);
    }

    public GroupLoanMembersAllocationData(final Long id, final Long loanId, final ClientData clientData, final BigDecimal amount) {
        this.id = id;
        this.loanId = loanId;
        this.clientData = clientData;
        this.amount = amount;
    }

    public Long getId() {
        return this.id;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public ClientData clientData() {

        return clientData;
    }


    public BigDecimal getAmount() {
        return this.amount;
    }
}
