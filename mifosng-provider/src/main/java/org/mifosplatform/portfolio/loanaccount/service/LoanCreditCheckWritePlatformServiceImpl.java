/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheck;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheckSeverityLevel;
import org.mifosplatform.portfolio.creditcheck.service.CreditCheckReportParamReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.data.LoanCreditCheckData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCreditCheck;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCreditCheckRepository;
import org.mifosplatform.portfolio.loanaccount.exception.LoanCreditCheckFailedException;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanCreditCheckWritePlatformServiceImpl implements LoanCreditCheckWritePlatformService {
    private final LoanCreditCheckRepository loanCreditCheckRepository;
    private final LoanCreditCheckReadPlatformService loanCreditCheckReadPlatformService;
    
    @Autowired
    public LoanCreditCheckWritePlatformServiceImpl(final CreditCheckReportParamReadPlatformService creditCheckReportParamReadPlatformService, 
            final LoanCreditCheckRepository loanCreditCheckRepository, 
            final GenericDataService genericDataService, 
            final LoanCreditCheckReadPlatformService loanCreditCheckReadPlatformService) {
        this.loanCreditCheckRepository = loanCreditCheckRepository;
        this.loanCreditCheckReadPlatformService = loanCreditCheckReadPlatformService;
    }
    
    /** 
     * Run all credit checks associated with the specified loan, throw an exception if any credit check fails
     * 
     * @param loan -- loan object
     * @return None
     **/
    @Override
    public void runLoanCreditChecks(final Loan loan) {
        final Collection<LoanCreditCheckData> loanCreditCheckDataList = this.loanCreditCheckReadPlatformService.triggerLoanCreditChecks(loan);
        
        if (loanCreditCheckDataList != null && !loanCreditCheckDataList.isEmpty()) {
            for (LoanCreditCheckData loanCreditCheckData : loanCreditCheckDataList) {
                final EnumOptionData severityLevelEnumOptionData = loanCreditCheckData.getSeverityLevel();
                final CreditCheckSeverityLevel severityLevel = CreditCheckSeverityLevel.fromInt(severityLevelEnumOptionData.getId().intValue());
                
                if (severityLevel.isError() && !loanCreditCheckData.actualResultEqualsExpectedResult()) {
                    throw new LoanCreditCheckFailedException(loan.getId(), loanCreditCheckData.getCreditCheckId(), loanCreditCheckData.getMessage());
                }
            }
        }
    }

    /** 
     * Run the credit checks, throw an exception if anyone fails, else add to the "m_loan_credit_check" table 
     * 
     * @param loanId -- loan object
     * @return None
     **/
    @Override
    @Transactional
    public void addLoanCreditChecks(final Loan loan) {
        final Collection<LoanCreditCheckData> loanCreditCheckDataList = this.loanCreditCheckReadPlatformService.triggerLoanCreditChecks(loan);
        final LoanProduct loanProduct = loan.loanProduct();
        
        if (loanCreditCheckDataList != null && !loanCreditCheckDataList.isEmpty()) {
            for (LoanCreditCheckData loanCreditCheckData : loanCreditCheckDataList) {
                final EnumOptionData severityLevelEnumOptionData = loanCreditCheckData.getSeverityLevel();
                final CreditCheckSeverityLevel severityLevel = CreditCheckSeverityLevel.fromInt(severityLevelEnumOptionData.getId().intValue());
                
                if (severityLevel.isError() && !loanCreditCheckData.actualResultEqualsExpectedResult()) {
                    throw new LoanCreditCheckFailedException(loan.getId(), loanCreditCheckData.getCreditCheckId(), loanCreditCheckData.getMessage());
                }
                
                CreditCheck creditCheck = getCreditCheckFromList(loanProduct.getCreditChecks(), loanCreditCheckData.getCreditCheckId());
                
                if (creditCheck != null) {
                    final Integer severityLevelIntValue = loanCreditCheckData.getSeverityLevel().getId().intValue();
                    
                    LoanCreditCheck loanCreditCheck = LoanCreditCheck.instance(creditCheck, loan, loanCreditCheckData.getExpectedResult(), 
                            loanCreditCheckData.getActualResult(), severityLevelIntValue, loanCreditCheckData.getMessage(), 
                            false, loanCreditCheckData.getSqlStatement());
                    
                    this.loanCreditCheckRepository.save(loanCreditCheck);
                }
            }
        } 
    }
    
    /** 
     * Set the "is_deleted" property of all credit checks associated with loan to 1 
     * 
     * @param loanId -- the identifier of the loan
     * @return None
     **/
    @Override
    @Transactional
    public void deleteLoanCreditChecks(final Loan loan) {
        Collection<LoanCreditCheck> loanCreditCheckList = loan.getCreditChecks();
        
        if (loanCreditCheckList != null && !loanCreditCheckList.isEmpty()) {
            for (LoanCreditCheck loanCreditCheck : loanCreditCheckList) {
                loanCreditCheck.updateIsDeleted(true);
                
                this.loanCreditCheckRepository.save(loanCreditCheck);
            }
        }
    }
    
    /** 
     * get credit check by id from a list of credit check objects 
     * 
     * @param creditChecks -- list of credit check objects
     * @param creditCheckId -- the identifier of the credit check to be retrieved
     * @return CreditCheck object if found, else null
     **/
    private CreditCheck getCreditCheckFromList(final Collection<CreditCheck> creditChecks, final Long creditCheckId) {
        CreditCheck creditCheckFound = null;
        
        if (creditChecks != null && !creditChecks.isEmpty()) {
            for (CreditCheck creditCheck : creditChecks) {
                if (creditCheck.getId().equals(creditCheckId)) {
                    creditCheckFound = creditCheck;
                    break;
                }
            }
        }
        
        return creditCheckFound;
    }
}
