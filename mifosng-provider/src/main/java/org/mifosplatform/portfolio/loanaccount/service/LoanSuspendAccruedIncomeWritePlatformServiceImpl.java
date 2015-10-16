/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;


import org.joda.time.LocalDate;
import org.mifosplatform.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;
import org.mifosplatform.portfolio.loanaccount.data.LoanInstallmentChargeData;
import org.mifosplatform.portfolio.loanaccount.data.LoanScheduleSuspendedAccruedIncomeData;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepository;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransactionType;
import org.mifosplatform.portfolio.loanproduct.service.LoanEnumerations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LoanSuspendAccruedIncomeWritePlatformServiceImpl implements LoanSuspendAccruedIncomeWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanSuspendAccruedIncomeWritePlatformServiceImpl.class);


    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanChargeReadPlatformService loanChargeReadPlatformService;
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final JpaTransactionManager transactionManager;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository;
    private final LoanRepository loanRepository;




    @Autowired
    public LoanSuspendAccruedIncomeWritePlatformServiceImpl(final LoanReadPlatformService loanReadPlatformService, final LoanChargeReadPlatformService loanChargeReadPlatformService,
            final RoutingDataSource dataSource,final JpaTransactionManager transactionManager,final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository,
            final LoanRepository loanRepository) {
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanChargeReadPlatformService = loanChargeReadPlatformService;
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        this.transactionManager = transactionManager;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.repaymentScheduleInstallmentRepository = repaymentScheduleInstallmentRepository;
        this.loanRepository = loanRepository;

    }



    @Override
    public void suspendAccruedIncome() {
        Collection<LoanScheduleSuspendedAccruedIncomeData> npaLoansToSuspendAccruedIncome = this.loanReadPlatformService.retrieveLoanScheduleForNPASuspendedIncome();
        StringBuilder sb = new StringBuilder();
        Set<Long> loansIds = new HashSet<>();
        Map<Long, Collection<LoanChargeData>> loanChargeMap = new HashMap<>();
        if(npaLoansToSuspendAccruedIncome != null && !npaLoansToSuspendAccruedIncome.isEmpty()){
            for (final LoanScheduleSuspendedAccruedIncomeData accrualData : npaLoansToSuspendAccruedIncome) {
                try {

                    // There is more suspended income to be booked on this loan, therefore make those bookings:
                    if(accrualData.getIsReverse() == false) {

                        if (!loansIds.contains(accrualData.getLoanId())) {
                            if (!loanChargeMap.containsKey(accrualData.getLoanId())) {
                                Collection<LoanChargeData> chargeData = this.loanChargeReadPlatformService
                                        .retrieveLoanChargesForAccural(accrualData.getLoanId());
                                loanChargeMap.put(accrualData.getLoanId(), chargeData);
                            }
                            updateLoanChargeAmountAccrued(loanChargeMap.get(accrualData.getLoanId()), accrualData);
                            addSuspendedIncomeAccounting(accrualData);
                        }
                    }

                    // There has been interest/Fees suspended on this loan, that needs to be unsuspended:
                    if(accrualData.getIsReverse() == true) {

                        if (!loansIds.contains(accrualData.getLoanId())) {
                            if (!loanChargeMap.containsKey(accrualData.getLoanId())) {
                                Collection<LoanChargeData> chargeData = this.loanChargeReadPlatformService
                                        .retrieveLoanChargesForAccural(accrualData.getLoanId());
                                loanChargeMap.put(accrualData.getLoanId(), chargeData);
                            }
                            updateLoanChargeAmountAccrued(loanChargeMap.get(accrualData.getLoanId()), accrualData);
                            reverseSuspendedIncomeAccounting(accrualData);
                        }
                    }


                } catch (Exception e) {
                    loansIds.add(accrualData.getLoanId());
                    Throwable realCause = e;
                    if (e.getCause() != null) {
                        realCause = e.getCause();
                    }
                    sb.append("failed to add accural transaction for repayment with id " + accrualData.getRepaymentScheduleId()
                            + " with message " + realCause.getMessage());
                }
            }

        }

    }

    private void addSuspendedIncomeTransaction(LoanScheduleSuspendedAccruedIncomeData loanScheduleSuspendedAccruedIncomeData,BigDecimal amount,BigDecimal interestPortion,
                                               BigDecimal feePortion, BigDecimal penaltyPortion){
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            String transactionSql = "INSERT INTO m_loan_transaction  (loan_id,office_id,is_reversed,transaction_type_enum,transaction_date,amount,suspended_interest_portion_derived,"
                    + "suspended_fee_charges_portion_derived,suspended_penalty_charges_portion_derived, submitted_on_date) VALUES (?, ?, 0, ?, ?, ?, ?, ?, ?, ?)";
            this.jdbcTemplate.update(transactionSql, loanScheduleSuspendedAccruedIncomeData.getLoanId(), loanScheduleSuspendedAccruedIncomeData.getOfficeId(),
                    LoanTransactionType.SUSPENDED_ACCRUED_INCOME.getValue(), DateUtils.getLocalDateOfTenant().toDate(), amount, interestPortion, feePortion, penaltyPortion,
                    DateUtils.getDateOfTenant());
            @SuppressWarnings("deprecation")
            final Long transactionId = this.jdbcTemplate.queryForLong("SELECT LAST_INSERT_ID()");

            Map<String, Object> transactionMap = toMapData(transactionId, amount, interestPortion, feePortion, penaltyPortion,
                    loanScheduleSuspendedAccruedIncomeData, DateUtils.getLocalDateOfTenant(),LoanTransactionType.SUSPENDED_ACCRUED_INCOME.getValue());

            String repaymentUpdateSql = "UPDATE m_loan_repayment_schedule SET suspended_interest_derived=?, suspended_fee_charges_derived=?, "
                    + "suspended_penalty_charges_derived=? WHERE  id=?";
            this.jdbcTemplate.update(repaymentUpdateSql, loanScheduleSuspendedAccruedIncomeData.getSuspendedInterest().add(interestPortion), loanScheduleSuspendedAccruedIncomeData.getSuspendedFee().add(feePortion), loanScheduleSuspendedAccruedIncomeData.getSuspendedPenalty().add(penaltyPortion),
                    loanScheduleSuspendedAccruedIncomeData.getRepaymentScheduleId());
            String updateLoan = "UPDATE m_loan  SET is_suspended_income=?  WHERE  id=?";
            boolean isSuspendedIncome = true;
            this.jdbcTemplate.update(updateLoan,isSuspendedIncome, loanScheduleSuspendedAccruedIncomeData.getLoanId());
            final Map<String, Object> accountingBridgeData = deriveAccountingBridgeData(loanScheduleSuspendedAccruedIncomeData, transactionMap);
            this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);

        }catch(Exception e){
            this.transactionManager.rollback(transactionStatus);
            throw e;
        }
        this.transactionManager.commit(transactionStatus);
    }

    private void reverseSuspendedIncomeTransaction(LoanScheduleSuspendedAccruedIncomeData loanScheduleSuspendedAccruedIncomeData,BigDecimal amount,BigDecimal interestPortion,
                                               BigDecimal feePortion, BigDecimal penaltyPortion){
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            String transactionSql = "INSERT INTO m_loan_transaction  (loan_id,office_id,is_reversed,transaction_type_enum,transaction_date,amount,suspended_interest_portion_derived,"
                    + "suspended_fee_charges_portion_derived,suspended_penalty_charges_portion_derived, submitted_on_date) VALUES (?, ?, 0, ?, ?, ?, ?, ?, ?, ?)";
            this.jdbcTemplate.update(transactionSql, loanScheduleSuspendedAccruedIncomeData.getLoanId(), loanScheduleSuspendedAccruedIncomeData.getOfficeId(),
                    LoanTransactionType.REVERSE_SUSPENDED_INCOME.getValue(), DateUtils.getLocalDateOfTenant().toDate(), amount, interestPortion, feePortion, penaltyPortion,
                    DateUtils.getDateOfTenant());
            @SuppressWarnings("deprecation")
            final Long transactionId = this.jdbcTemplate.queryForLong("SELECT LAST_INSERT_ID()");

            Map<String, Object> transactionMap = toMapData(transactionId, amount, interestPortion, feePortion, penaltyPortion,
                    loanScheduleSuspendedAccruedIncomeData, DateUtils.getLocalDateOfTenant(),LoanTransactionType.REVERSE_SUSPENDED_INCOME.getValue());

            String repaymentUpdateSql = "UPDATE m_loan_repayment_schedule SET suspended_interest_derived=?, suspended_fee_charges_derived=?, "
                    + "suspended_penalty_charges_derived=? WHERE  id=?";
            this.jdbcTemplate.update(repaymentUpdateSql, loanScheduleSuspendedAccruedIncomeData.getSuspendedInterest().min(interestPortion), loanScheduleSuspendedAccruedIncomeData.getSuspendedFee().min(feePortion), loanScheduleSuspendedAccruedIncomeData.getSuspendedPenalty().min(penaltyPortion),
                    loanScheduleSuspendedAccruedIncomeData.getRepaymentScheduleId());
            String updateLoan = "UPDATE m_loan  SET is_suspended_income=?  WHERE  id=?";
            boolean isSuspendedIncome = true;
            this.jdbcTemplate.update(updateLoan,isSuspendedIncome, loanScheduleSuspendedAccruedIncomeData.getLoanId());
            final Map<String, Object> accountingBridgeData = deriveAccountingBridgeData(loanScheduleSuspendedAccruedIncomeData, transactionMap);
            this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);

        }catch(Exception e){
            this.transactionManager.rollback(transactionStatus);
            throw e;
        }
        this.transactionManager.commit(transactionStatus);
    }

    @Transactional
    private void addSuspendedIncomeAccounting(LoanScheduleSuspendedAccruedIncomeData scheduleAccrualData) throws Exception{

        BigDecimal interestPortion = scheduleAccrualData.getInterestToSuspend();
        BigDecimal feePortion = scheduleAccrualData.getFeesToSuspend() ;
        BigDecimal penaltyPortion = scheduleAccrualData.getPenaltyToSuspend();

        BigDecimal amount = interestPortion.add(feePortion).add(penaltyPortion);

        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            addSuspendedIncomeTransaction(scheduleAccrualData,amount,interestPortion,feePortion,penaltyPortion);
        }

    }

    @Transactional
    private void reverseSuspendedIncomeAccounting(LoanScheduleSuspendedAccruedIncomeData scheduleAccrualData) throws Exception{

        BigDecimal interestPortion = scheduleAccrualData.getInterestToSuspend().abs();
        BigDecimal feePortion = scheduleAccrualData.getFeesToSuspend().abs();
        BigDecimal penaltyPortion = scheduleAccrualData.getPenaltyToSuspend().abs();
        BigDecimal amount = interestPortion.add(feePortion).add(penaltyPortion);

        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            reverseSuspendedIncomeTransaction(scheduleAccrualData, amount, interestPortion, feePortion, penaltyPortion);
        }

    }

    public Map<String, Object> deriveAccountingBridgeData(final LoanScheduleSuspendedAccruedIncomeData loanScheduleSuspendedAccruedIncomeData,
                                                          final Map<String, Object> transactionMap) {

        final Map<String, Object> accountingBridgeData = new LinkedHashMap<>();
        accountingBridgeData.put("loanId", loanScheduleSuspendedAccruedIncomeData.getLoanId());
        accountingBridgeData.put("loanProductId", loanScheduleSuspendedAccruedIncomeData.getLoanProductId());
        accountingBridgeData.put("officeId", loanScheduleSuspendedAccruedIncomeData.getOfficeId());
        accountingBridgeData.put("currency", loanScheduleSuspendedAccruedIncomeData.getCurrencyData());
        accountingBridgeData.put("cashBasedAccountingEnabled", false);
        accountingBridgeData.put("upfrontAccrualBasedAccountingEnabled", false);
        accountingBridgeData.put("periodicAccrualBasedAccountingEnabled", true);
        accountingBridgeData.put("isAccountTransfer", false);

        final List<Map<String, Object>> newLoanTransactions = new ArrayList<>();
        newLoanTransactions.add(transactionMap);

        accountingBridgeData.put("newLoanTransactions", newLoanTransactions);
        return accountingBridgeData;
    }

    public Map<String, Object> toMapData(final Long transactionId, final BigDecimal amount, final BigDecimal interestPortion,
                                         final BigDecimal feePortion, final BigDecimal penaltyPortion, final LoanScheduleSuspendedAccruedIncomeData loanScheduleSuspendedAccruedIncomeData,
                                         final LocalDate accruedTill,final Integer loanTransactionType) {
        final Map<String, Object> thisTransactionData = new LinkedHashMap<>();

        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(loanTransactionType);

        thisTransactionData.put("id", transactionId);
        thisTransactionData.put("officeId", loanScheduleSuspendedAccruedIncomeData.getOfficeId());
        thisTransactionData.put("type", transactionType);
        thisTransactionData.put("reversed", false);
        thisTransactionData.put("date", DateUtils.getLocalDateOfTenant());
        thisTransactionData.put("currency", loanScheduleSuspendedAccruedIncomeData.getCurrencyData());
        thisTransactionData.put("amount", amount);
        thisTransactionData.put("principalPortion", null);
        thisTransactionData.put("interestPortion", interestPortion);
        thisTransactionData.put("feeChargesPortion", feePortion);
        thisTransactionData.put("penaltyChargesPortion", penaltyPortion);
        thisTransactionData.put("overPaymentPortion", null);



        Map<LoanChargeData, BigDecimal> applicableCharges = loanScheduleSuspendedAccruedIncomeData.getAppliedCharge();
        if (applicableCharges != null && !applicableCharges.isEmpty()) {
            final List<Map<String, Object>> loanChargesPaidData = new ArrayList<>();
                for (Map.Entry<LoanChargeData, BigDecimal> entry : applicableCharges.entrySet()) {
                    LoanChargeData chargeData = entry.getKey();
                final Map<String, Object> loanChargePaidData = new LinkedHashMap<>();
                loanChargePaidData.put("chargeId", chargeData.getChargeId());
                loanChargePaidData.put("isPenalty", chargeData.isPenalty());
                loanChargePaidData.put("loanChargeId", chargeData.getId());
                loanChargePaidData.put("amount", entry.getValue());

                loanChargesPaidData.add(loanChargePaidData);
            }
            thisTransactionData.put("loanChargesPaid", loanChargesPaidData);
        }

        return thisTransactionData;

    }

    public Map<String, Object> toMapDatas(final Long transactionId, final BigDecimal amount, final BigDecimal interestPortion,
                                         final BigDecimal feePortion, final BigDecimal penaltyPortion, final LoanScheduleSuspendedAccruedIncomeData loanScheduleSuspendedAccruedIncomeData,
                                         final LocalDate accruedTill,final Integer loanTransactionType) {
        final Map<String, Object> thisTransactionData = new LinkedHashMap<>();

        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(loanTransactionType);

        thisTransactionData.put("id", transactionId);
        thisTransactionData.put("officeId", loanScheduleSuspendedAccruedIncomeData.getOfficeId());
        thisTransactionData.put("type", transactionType);
        thisTransactionData.put("reversed", false);
        thisTransactionData.put("date", DateUtils.getLocalDateOfTenant());
        thisTransactionData.put("currency", loanScheduleSuspendedAccruedIncomeData.getCurrencyData());
        thisTransactionData.put("amount", amount);
        thisTransactionData.put("principalPortion", null);
        thisTransactionData.put("suspendedInterestPortion", interestPortion);
        thisTransactionData.put("suspendedFeePortion", feePortion);
        thisTransactionData.put("suspendedPenaltyPortion", penaltyPortion);
        thisTransactionData.put("overPaymentPortion", null);




        Map<LoanChargeData, BigDecimal> applicableCharges = loanScheduleSuspendedAccruedIncomeData.getAppliedCharge();
        if (applicableCharges != null && !applicableCharges.isEmpty()) {
            final List<Map<String, Object>> loanChargesPaidData = new ArrayList<>();
            for (Map.Entry<LoanChargeData, BigDecimal> entry : applicableCharges.entrySet()) {
                LoanChargeData chargeData = entry.getKey();
                final Map<String, Object> loanChargePaidData = new LinkedHashMap<>();
                loanChargePaidData.put("chargeId", chargeData.getChargeId());
                loanChargePaidData.put("isPenalty", chargeData.isPenalty());
                loanChargePaidData.put("loanChargeId", chargeData.getId());
                loanChargePaidData.put("amount", entry.getValue());

                loanChargesPaidData.add(loanChargePaidData);
            }
            thisTransactionData.put("loanChargesPaid", loanChargesPaidData);
        }

        return thisTransactionData;

    }
    /**
     * TODO how do u know if the getAccruedPenaltyIncome()  or loanScheduleSuspendedAccruedIncomeData.getAccruedFeeIncome() is associated with the loancharge
     *
     * @param loanChargeDatas
     * @param loanScheduleSuspendedAccruedIncomeData
     * Function updates loanScheduleSuspendedAccruedIncomeData.updateAppliedCharge
     *
     * since loanScheduleSuspendedAccruedIncomeData may contain penalty charge accrued or fee charge accrued
     * this amount will be the same amount in the m_loan_charge_paid_by with a unique transaction id
     * therefore there is no need to go into that table (m_loan_charge_paid_by) to query for it
     */
    public void updateLoanChargeAmountAccrued(Collection<LoanChargeData> loanChargeDatas,LoanScheduleSuspendedAccruedIncomeData loanScheduleSuspendedAccruedIncomeData){
        Map<LoanChargeData, BigDecimal> appliedCharges = new HashMap<>();
        if(loanChargeDatas != null && !loanChargeDatas.isEmpty()){
            for(LoanChargeData loanChargeData : loanChargeDatas){
                Collection<LoanInstallmentChargeData> installmentChargeDatas = loanChargeData.getInstallmentChargeData();
                for(LoanInstallmentChargeData installmentChargeData : installmentChargeDatas){
                    if(installmentChargeData.getInstallmentNumber().equals(loanScheduleSuspendedAccruedIncomeData.getInstallmentNumber())){
                        if(loanChargeData.isPenalty() && loanScheduleSuspendedAccruedIncomeData.getAccruedPenaltyIncome() != null){
                            appliedCharges.put(loanChargeData,loanScheduleSuspendedAccruedIncomeData.getAccruedPenaltyIncome());
                            loanScheduleSuspendedAccruedIncomeData.updateAppliedCharge(appliedCharges);
                            break;
                        }else if(loanChargeData.isInstallmentFee() && loanScheduleSuspendedAccruedIncomeData.getAccruedFeeIncome() !=null){
                            appliedCharges.put(loanChargeData,loanScheduleSuspendedAccruedIncomeData.getAccruedFeeIncome());
                            loanScheduleSuspendedAccruedIncomeData.updateAppliedCharge(appliedCharges);
                            break;
                        }
                    }

                }

            }
        }
    }



    /**
     * When loan is NPA all (interest + fees + penalties) accrued are suspended in suspendedIncome
     * This function books the opposite of suspended income i.e.(interest + fees + penalties) when
     * loan is out of NPA
     */
    @Transactional
    private void reverseSuspendedIncomeOutOfNPA(LoanScheduleSuspendedAccruedIncomeData loanScheduleSuspendedAccruedIncomeData,BigDecimal amount,BigDecimal interestPortion,
                                                BigDecimal feePortion, BigDecimal penaltyPortion){
        TransactionStatus transactionStatus = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            String transactionSql = "INSERT INTO m_loan_transaction  (loan_id,office_id,is_reversed,transaction_type_enum,transaction_date,amount,suspended_interest_portion_derived,"
                    + "suspended_fee_charges_portion_derived,suspended_penalty_charges_portion_derived, submitted_on_date) VALUES (?, ?, 0, ?, ?, ?, ?, ?, ?, ?)";
            this.jdbcTemplate.update(transactionSql, loanScheduleSuspendedAccruedIncomeData.getLoanId(), loanScheduleSuspendedAccruedIncomeData.getOfficeId(),
                    LoanTransactionType.REVERSE_SUSPENDED_INCOME.getValue(), DateUtils.getLocalDateOfTenant().toDate(), amount, interestPortion, feePortion, penaltyPortion,
                    DateUtils.getDateOfTenant());
            @SuppressWarnings("deprecation")
            final Long transactionId = this.jdbcTemplate.queryForLong("SELECT LAST_INSERT_ID()");

            Map<String, Object> transactionMap = toMapDatas(transactionId, amount, interestPortion, feePortion, penaltyPortion,
                    loanScheduleSuspendedAccruedIncomeData, DateUtils.getLocalDateOfTenant(), LoanTransactionType.REVERSE_SUSPENDED_INCOME.getValue());

            /**
             * don't book remaining cos this figure will be in the interest completed column
             */
        /**
            String repaymentUpdateSql = "UPDATE m_loan_repayment_schedule SET suspended_interest_derived=?, suspended_fee_charges_derived=?, "
                    + "suspended_penalty_charges_derived=? WHERE  id=?";
            BigDecimal remainingSuspendedFeePortion = null;
            if(loanScheduleSuspendedAccruedIncomeData.getSuspendedFee() !=null){
                remainingSuspendedFeePortion = loanScheduleSuspendedAccruedIncomeData.getSuspendedFee().subtract(feePortion);
                if(remainingSuspendedFeePortion.signum() == -1 || remainingSuspendedFeePortion.signum() == 0){remainingSuspendedFeePortion = null;}
            }
            BigDecimal remainingSuspendedInterestPortion = null;
            if(loanScheduleSuspendedAccruedIncomeData.getSuspendedInterest() !=null){
                remainingSuspendedInterestPortion = loanScheduleSuspendedAccruedIncomeData.getSuspendedInterest().subtract(interestPortion);
                if(remainingSuspendedInterestPortion.signum() == -1 || remainingSuspendedInterestPortion.signum() == 0){ remainingSuspendedInterestPortion = null;}
            }
            BigDecimal remainingSuspendedPenaltyPortion = null;
            if(loanScheduleSuspendedAccruedIncomeData.getSuspendedPenalty()  !=null){
                remainingSuspendedPenaltyPortion = loanScheduleSuspendedAccruedIncomeData.getSuspendedPenalty().subtract(penaltyPortion);
                if(remainingSuspendedPenaltyPortion.signum() == -1 || remainingSuspendedPenaltyPortion.signum() == 0){remainingSuspendedPenaltyPortion = null;}
            }

            this.jdbcTemplate.update(repaymentUpdateSql, remainingSuspendedInterestPortion,remainingSuspendedFeePortion, remainingSuspendedPenaltyPortion,
                    loanScheduleSuspendedAccruedIncomeData.getRepaymentScheduleId());
        **/
            String updateLoan = "UPDATE m_loan  SET is_suspended_income=?  WHERE  id=?";
            boolean isSuspendedIncome = false;
            this.jdbcTemplate.update(updateLoan, isSuspendedIncome, loanScheduleSuspendedAccruedIncomeData.getLoanId());
            final Map<String, Object> accountingBridgeData = deriveAccountingBridgeData(loanScheduleSuspendedAccruedIncomeData, transactionMap);
            this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);

        }catch(Exception e){
            this.transactionManager.rollback(transactionStatus);
            throw e;
        }
        this.transactionManager.commit(transactionStatus);
    }

    private void addSuspendIncomeOutOfNPAAccounting(LoanScheduleSuspendedAccruedIncomeData scheduleAccrualData) throws Exception {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal interestPortion = null;
        BigDecimal feePortion = null;
        BigDecimal penaltyPortion = null;

        interestPortion = scheduleAccrualData.getSuspendedInterest();

        if(scheduleAccrualData.getWaivedInterestIncome() !=null){
            interestPortion = interestPortion.subtract(scheduleAccrualData.getWaivedInterestIncome());
        }
        if(scheduleAccrualData.getInterestCompletedDerived() != null){
            interestPortion = interestPortion.subtract(scheduleAccrualData.getInterestCompletedDerived());
        }
        amount = amount.add(interestPortion);

        if (interestPortion.compareTo(BigDecimal.ZERO) == 0) {
            interestPortion = null;
        }


        feePortion = scheduleAccrualData.getSuspendedFee();
        if(feePortion !=null){
            if(scheduleAccrualData.getFeeChargesWaivedDerived() != null){
                feePortion = feePortion.subtract(scheduleAccrualData.getFeeChargesWaivedDerived());
            }
            if(scheduleAccrualData.getFeeChargesCompletedDerived() != null){
                feePortion = feePortion.subtract(scheduleAccrualData.getFeeChargesCompletedDerived());
            }
            amount = amount.add(feePortion);

            if (feePortion.compareTo(BigDecimal.ZERO) == 0) {
                feePortion = null;
            }
        }


        penaltyPortion = scheduleAccrualData.getSuspendedPenalty();

        if(penaltyPortion !=null){
            if(scheduleAccrualData.getPenaltyChargesWaivedDerived() !=null){
                penaltyPortion = penaltyPortion.subtract(scheduleAccrualData.getPenaltyChargesWaivedDerived());
            }
            if(scheduleAccrualData.getPenaltyChargesCompletedDerived() !=null){
                penaltyPortion = penaltyPortion.subtract(scheduleAccrualData.getPenaltyChargesCompletedDerived());
            }
            amount = amount.add(penaltyPortion);
            if (penaltyPortion.compareTo(BigDecimal.ZERO) == 0) {
                penaltyPortion = null;
            }
        }

        if (amount.compareTo(BigDecimal.ZERO) == 1) {
            reverseSuspendedIncomeOutOfNPA(scheduleAccrualData, amount,interestPortion,feePortion,penaltyPortion);
        }

    }

    @Override
    public void suspendedIncomeOutOfNPA(Loan loan) {
        if(!this.loanReadPlatformService.doesLoanHaveSuspendedIncomeAndIsNpa(loan.getId())){
            if (loan.isSuspendedIncome()){
                Collection<LoanScheduleSuspendedAccruedIncomeData> npaLoansSuspendedIncomeOutOfNPA  = this.loanReadPlatformService.retrieveLoanScheduleForSuspendLoanOutOfNPA(loan.getId());
                StringBuilder sb = new StringBuilder();
                Map<Long, Collection<LoanChargeData>> loanChargeMap = new HashMap<>();
                Set<Long> loansIds = new HashSet<>();

                if(npaLoansSuspendedIncomeOutOfNPA !=null  && !npaLoansSuspendedIncomeOutOfNPA.isEmpty()) {
                    for (final LoanScheduleSuspendedAccruedIncomeData accrualData : npaLoansSuspendedIncomeOutOfNPA) {
                        try {
                            if (!loansIds.contains(accrualData.getLoanId())) {
                                if (!loanChargeMap.containsKey(accrualData.getLoanId())) {
                                    Collection<LoanChargeData> chargeData = this.loanChargeReadPlatformService
                                            .retrieveLoanChargesForAccural(accrualData.getLoanId());
                                    loanChargeMap.put(accrualData.getLoanId(), chargeData);
                                }
                                updateLoanChargeAmountAccrued(loanChargeMap.get(accrualData.getLoanId()), accrualData);
                                addSuspendIncomeOutOfNPAAccounting(accrualData);
                            }
                        } catch (Exception e) {
                            loansIds.add(accrualData.getLoanId());
                            Throwable realCause = e;
                            if (e.getCause() != null) {
                                realCause = e.getCause();
                            }
                            sb.append("failed to add suspended transaction for repayment with id " + accrualData.getRepaymentScheduleId()
                                    + " with message " + realCause.getMessage());
                        }

                    }
                }  /** end of this function  **/
            }
        }
    }

    @Override
    public void reverseSuspendedIncomeWhenRepaymentIsReversed(final Loan loan,final Long transactionId) {
        if(this.loanReadPlatformService.isLoanBackToNPA(loan.getId())){
            /**
             * find the first transaction of type 20 and reverse it. using the repayment transaction id which was reversed.
             */
            final List<LoanTransaction> transactions = loan.getLoanTransactions();

            List<LoanTransaction> allTransactionsAfterTransactionId = new ArrayList<>();
            for(LoanTransaction transaction : transactions){
                if(transactionId < transaction.getId() && transaction.getTypeOf().isReverseSuspendedIncome() && transaction.isNotReversed()){
                    allTransactionsAfterTransactionId.add(transaction);
                }
            }
            if(!allTransactionsAfterTransactionId.isEmpty()){
                for(LoanTransaction t : allTransactionsAfterTransactionId){
                   for(LoanTransaction loanTransaction : transactions){
                       if(t.getId().longValue() == loanTransaction.getId().longValue()){
                           Collection<Long> existingTransactionsIds = loan.findExistingTransactionIds();
                           Collection<Long> existingReversedTransactionIds = loan.findExistingReversedTransactionIds();
                           loanTransaction.reverse();
                           loan.updateSuspendIncome(true);
                           this.postJournalEntries(loan, (List) existingTransactionsIds, (List) existingReversedTransactionIds);
                       }
                   }
                }
            }else{ loan.updateSuspendIncome(true);} //when interest pays all suspended income
        }
    }

    private void postJournalEntries(final Loan loan, final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds) {
        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        boolean isAccountTransfer = false;
        final Map<String, Object> accountingBridgeData = loan.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);
    }
    private void saveAndFlushLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            for (LoanRepaymentScheduleInstallment installment : installments) {
                if (installment.getId() == null) {
                    this.repaymentScheduleInstallmentRepository.save(installment);
                } else {
                    break;
                }
            }
            this.loanRepository.saveAndFlush(loan);
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");
            if (realCause.getMessage().toLowerCase().contains("external_id_unique")) {
                baseDataValidator.reset().parameter("externalId").failWithCode("value.must.be.unique");
            }
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors); }
        }
    }

    @Override
    public void handleTransactionsOnSuspendedIncomeOutNPA(Loan loan) {

    }
}
