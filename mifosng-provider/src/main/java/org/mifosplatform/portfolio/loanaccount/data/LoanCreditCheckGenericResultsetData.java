/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;

/**  
 * immutable object that represents a loan credit check generic resultset data
 **/
public class LoanCreditCheckGenericResultsetData {
    private final GenericResultsetData genericResultsetData;
    private final String sqlStatement;

    /** 
     * LoanCreditCheckGenericResultsetData constructor
     * 
     * @return None
     **/
    private LoanCreditCheckGenericResultsetData(final GenericResultsetData genericResultsetData, 
            final String sqlStatement) {
        this.genericResultsetData = genericResultsetData;
        this.sqlStatement = sqlStatement;
    }
    
    /**
     * @param sql -- SQL string with variables replaced by string values
     * @param genericResultsetData -- GenericResultsetData object
     * @return LoanCreditCheckGenericResultsetData object
     **/
    public static LoanCreditCheckGenericResultsetData instance(final GenericResultsetData genericResultsetData, 
            final String sqlStatement) {
        return new LoanCreditCheckGenericResultsetData(genericResultsetData, sqlStatement);
    }

    /**
     * @return the genericResultsetData
     */
    public GenericResultsetData getGenericResultsetData() {
        return genericResultsetData;
    }

    /**
     * @return the SQL statement
     */
    public String getSqlStatement() {
        return sqlStatement;
    }
}
