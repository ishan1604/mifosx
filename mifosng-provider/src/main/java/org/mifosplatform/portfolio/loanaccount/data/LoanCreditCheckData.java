/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckEnumerations;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheck;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCreditCheck;
import org.springframework.util.CollectionUtils;

public class LoanCreditCheckData {
    private final Long id;
    private final Long creditCheckId;
    private final String name;
    private final String expectedResult;
    private final String actualResult;
    private final EnumOptionData severityLevel;
    private final String message;
    private final boolean isDeleted;
    private final List<EnumOptionData> severityLevelOptions;
    private final String sqlStatement;
    
    private LoanCreditCheckData(final Long id, final Long creditCheckId, final String name, final String expectedResult, 
            final String actualResult, final EnumOptionData severityLevel, final String message, 
            final boolean isDeleted, final List<EnumOptionData> severityLevelOptions, final String sqlStatement) {
        this.id = id;
        this.creditCheckId = creditCheckId;
        this.name = name;
        this.expectedResult = expectedResult;
        this.actualResult = actualResult;
        this.severityLevel = severityLevel;
        this.message = message;
        this.isDeleted = isDeleted;
        this.severityLevelOptions = severityLevelOptions;
        this.sqlStatement = sqlStatement;
    }
    
    public static LoanCreditCheckData instance(final Long id, final Long creditCheckId, final String name, 
            final String expectedResult, final String actualResult, final EnumOptionData severityLevel, final String message, 
            final boolean isDeleted, final List<EnumOptionData> severityLevelOptions, final String sqlStatement) {
        return new LoanCreditCheckData(id, creditCheckId, name, expectedResult, actualResult, severityLevel, 
                message, isDeleted, severityLevelOptions, sqlStatement);
    }
    
    public static LoanCreditCheckData instance(final Long id, final Long creditCheckId, final String name, 
            final String expectedResult, final String actualResult, final EnumOptionData severityLevel, 
            final String message, final boolean isDeleted, final String sqlStatement) {
        return new LoanCreditCheckData(id, creditCheckId, name, expectedResult, actualResult, severityLevel, 
                message, isDeleted, null, sqlStatement);
    }
    
    public static LoanCreditCheckData instance(final List<EnumOptionData> severityLevelOptions) {
        return new LoanCreditCheckData(null, null, null, null, null, null, 
                null, false, severityLevelOptions, null);
    }
    
    public static LoanCreditCheckData instance(final LoanCreditCheck loanCreditCheck) {
        final CreditCheck creditCheck = loanCreditCheck.getCreditCheck();
        EnumOptionData severityLevel = null;
        
        if (loanCreditCheck.getSeverityLevelEnumValue() != null) {
            severityLevel = CreditCheckEnumerations.severityLevel(loanCreditCheck.getSeverityLevelEnumValue());
        }
        
        return new LoanCreditCheckData(loanCreditCheck.getId(), creditCheck.getId(), creditCheck.getName(), 
                loanCreditCheck.getExpectedResult(), loanCreditCheck.getActualResult(), severityLevel, loanCreditCheck.getMessage(), 
                loanCreditCheck.isDeleted(), null, loanCreditCheck.getSqlStatement());
    }
    
    public static Collection<LoanCreditCheckData> instance(final Collection<LoanCreditCheck> loanCreditChecks) {
        final Collection<LoanCreditCheckData> loanCreditCheckDataList = new ArrayList<>();
        
        if (CollectionUtils.isEmpty(loanCreditChecks)) {
            for (LoanCreditCheck loanCreditCheck : loanCreditChecks) {
                LoanCreditCheckData loanCreditCheckData = LoanCreditCheckData.instance(loanCreditCheck);
                
                loanCreditCheckDataList.add(loanCreditCheckData);
            }
        }
        
        return loanCreditCheckDataList;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the creditCheckId
     */
    public Long getCreditCheckId() {
        return creditCheckId;
    }

    /**
     * @return the expectedResult
     */
    public String getExpectedResult() {
        return expectedResult;
    }

    /**
     * @return the actualResult
     */
    public String getActualResult() {
        return actualResult;
    }

    /**
     * @return the severityLevel
     */
    public EnumOptionData getSeverityLevel() {
        return severityLevel;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the isDeleted
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the severityLevelOptions
     */
    public List<EnumOptionData> getSeverityLevelOptions() {
        return severityLevelOptions;
    }
    
    /** 
     * @return true if actual result equals expected result 
     **/
    public boolean actualResultEqualsExpectedResult() {
        boolean result = false;
        
        if (this.actualResult != null && this.expectedResult != null) {
            result = this.actualResult.trim().equals(this.expectedResult.trim());
        }
        
        return result;
    }

    /**
     * @return the SQL statement
     */
    public String getSqlStatement() {
        return sqlStatement;
    }
}
