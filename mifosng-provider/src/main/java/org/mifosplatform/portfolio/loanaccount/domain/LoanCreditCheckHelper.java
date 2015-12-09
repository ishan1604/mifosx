/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.data.ReportData;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.portfolio.creditcheck.CreditCheckConstants;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckReportParamData;
import org.mifosplatform.portfolio.creditcheck.service.CreditCheckReportParamReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.data.LoanCreditCheckGenericResultsetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Loan credit check helper class 
 **/
public class LoanCreditCheckHelper {
    private GenericDataService genericDataService = null;
    private CreditCheckReportParamReadPlatformService creditCheckReportParamReadPlatformService = null;
    private final static Logger logger = LoggerFactory.getLogger(LoanCreditCheckHelper.class);

    public LoanCreditCheckHelper(final GenericDataService genericDataService, 
            final CreditCheckReportParamReadPlatformService creditCheckReportParamReadPlatformService) { 
        this.genericDataService = genericDataService;
        this.creditCheckReportParamReadPlatformService = creditCheckReportParamReadPlatformService;
    }
    
    /** 
     * @param stretchyReport -- Report object
     * @param loanId -- loan identifier
     * @param userId -- app user identifier
     * @return GenericResultsetData object
     **/
    public LoanCreditCheckGenericResultsetData retrieveGenericResultsetForCreditCheck(final ReportData reportData, final Long loanId, 
            final Long userId, final Boolean isGroupLoan) {
        final long startTime = System.currentTimeMillis();
        logger.info("STARTING REPORT: " + reportData.getReportName() + "   Type: " + reportData.getReportType());
        
        final String sqlStatement = searchAndReplaceParamsInSQLString(loanId, userId, reportData.getReportSql(), isGroupLoan);
        final GenericResultsetData genericResultsetData = this.genericDataService.fillGenericResultSet(sqlStatement);
        
        logger.info("SQL: " + sqlStatement);
        
        final long elapsed = System.currentTimeMillis() - startTime;
        logger.info("FINISHING REPORT: " + reportData.getReportName() + " - " + reportData.getReportType() 
                + "     Elapsed Time: " + elapsed + " milliseconds");
        
        return LoanCreditCheckGenericResultsetData.instance(genericResultsetData, sqlStatement);
    }
    
    /** 
     * @param loanId -- loan identifier
     * @param userId -- app user identifier
     * @param sql -- the initial SQL string containing variables
     * @return SQL string with variables replaced by string values 
     **/
    private String searchAndReplaceParamsInSQLString(final Long loanId, final Long userId, String sql, final Boolean isGroupLoan) {
        CreditCheckReportParamData creditCheckReportParamData = this.creditCheckReportParamReadPlatformService
                .retrieveCreditCheckReportParameters(loanId, userId, isGroupLoan);
        
        sql = this.genericDataService.replace(sql, CreditCheckConstants.CLIENT_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getClientId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.GROUP_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getGroupId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.LOAN_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getLoanId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.USER_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getUserId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.STAFF_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getStaffId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.OFFICE_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getOfficeId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.PRODUCT_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getProductId().toString());
        
        return this.genericDataService.wrapSQL(sql);
    }
}
