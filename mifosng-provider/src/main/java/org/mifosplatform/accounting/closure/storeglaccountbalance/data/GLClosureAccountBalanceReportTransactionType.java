/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.storeglaccountbalance.data;

/**
 * Represents the nominal transaction type. Use the value 1 to indicate new transactions.
 */
public enum GLClosureAccountBalanceReportTransactionType {
    NEW(1, "glClosureAccountBalanceReportTransactionType.new");
    
    private final Integer value;
    private final String code;
    
    /**
     * @param value
     * @param code
     */
    private GLClosureAccountBalanceReportTransactionType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }
    
    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
}
