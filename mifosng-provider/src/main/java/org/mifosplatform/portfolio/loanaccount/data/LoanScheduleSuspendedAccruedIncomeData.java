/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;


import org.joda.time.LocalDate;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.common.domain.PeriodFrequencyType;

import java.math.BigDecimal;
import java.util.Map;

public class LoanScheduleSuspendedAccruedIncomeData {
    private final Long loanId;
    private final Long officeId;
    private final LocalDate accruedTill;
    private final PeriodFrequencyType repaymentFrequency;
    private final Integer repayEvery;
    private final Integer installmentNumber;
    private final LocalDate dueDate;
    private final LocalDate fromDate;
    private final Long repaymentScheduleId;
    private final Long loanProductId;
    private final BigDecimal interestIncome;
    private final BigDecimal feeIncome;
    private final BigDecimal penaltyIncome;
    private final BigDecimal waivedInterestIncome;
    private final BigDecimal accruedInterestIncome;
    private final BigDecimal accruedFeeIncome;
    private final BigDecimal accruedPenaltyIncome;
    private final CurrencyData currencyData;
    private final LocalDate interestCalculatedFrom;
    private final BigDecimal suspendedInterest;
    private final BigDecimal suspendedFee;
    private final BigDecimal suspendedPenalty;

    private final BigDecimal interestCompletedDerived;
    private final BigDecimal interestWaivedDerived;

    private final BigDecimal feeChargesCompletedDerived;
    private final BigDecimal feeChargesWaivedDerived;

    private final BigDecimal penaltyChargesCompletedDerived;
    private final BigDecimal penaltyChargesWaivedDerived;

    private  BigDecimal interestToSuspend;
    private BigDecimal feesToSuspend;
    private  BigDecimal penaltyToSuspend;

    private Boolean isReverse;


    private Map<LoanChargeData, BigDecimal> appliedCharge;



    public LoanScheduleSuspendedAccruedIncomeData(Long loanId, Long officeId, LocalDate accruedTill, PeriodFrequencyType repaymentFrequency, Integer repayEvery,
                                                  Integer installmentNumber, LocalDate dueDate, LocalDate fromDate, Long repaymentScheduleId, Long loanProductId, BigDecimal interestIncome, BigDecimal feeIncome,
                                                  BigDecimal penaltyIncome, BigDecimal waivedInterestIncome, BigDecimal accruedInterestIncome, BigDecimal accruedFeeIncome,
                                                  BigDecimal accruedPenaltyIncome, CurrencyData currencyData, LocalDate interestCalculatedFrom, BigDecimal suspendedInterest,
                                                  BigDecimal suspendedFee, BigDecimal suspendedPenalty, BigDecimal interestCompletedDerived, BigDecimal interestWaivedDerived,
                                                  BigDecimal feeChargesCompletedDerived, BigDecimal feeChargesWaivedDerived, BigDecimal penaltyChargesCompletedDerived,
                                                  BigDecimal penaltyChargesWaivedDerived, BigDecimal interestToSuspend, BigDecimal feesToSuspend, BigDecimal penaltyToSuspend) {
        this.loanId = loanId;
        this.officeId = officeId;
        this.accruedTill = accruedTill;
        this.repaymentFrequency = repaymentFrequency;
        this.repayEvery = repayEvery;
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.fromDate = fromDate;
        this.repaymentScheduleId = repaymentScheduleId;
        this.loanProductId = loanProductId;
        this.interestIncome = interestIncome;
        this.feeIncome = feeIncome;
        this.penaltyIncome = penaltyIncome;
        this.waivedInterestIncome = waivedInterestIncome;
        this.accruedInterestIncome = accruedInterestIncome;
        this.accruedFeeIncome = accruedFeeIncome;
        this.accruedPenaltyIncome = accruedPenaltyIncome;
        this.currencyData = currencyData;
        this.interestCalculatedFrom = interestCalculatedFrom;
        this.suspendedInterest = suspendedInterest;
        this.suspendedFee = suspendedFee;
        this.suspendedPenalty = suspendedPenalty;
        this.interestCompletedDerived = interestCompletedDerived;
        this.interestWaivedDerived = interestWaivedDerived;
        this.feeChargesCompletedDerived = feeChargesCompletedDerived;
        this.feeChargesWaivedDerived = feeChargesWaivedDerived;
        this.penaltyChargesCompletedDerived = penaltyChargesCompletedDerived;
        this.penaltyChargesWaivedDerived = penaltyChargesWaivedDerived;
        this.interestToSuspend = interestToSuspend.abs();
        this.feesToSuspend = feesToSuspend.abs();
        this.penaltyToSuspend = penaltyToSuspend.abs();
        this.isReverse = false;
        if (interestToSuspend.compareTo(BigDecimal.ZERO) == -1 || feesToSuspend.compareTo(BigDecimal.ZERO) == -1 || penaltyToSuspend.compareTo(BigDecimal.ZERO) == -1)
        {
            this.isReverse = true;
        }

    }

    public Boolean getIsReverse() {
        return isReverse;
    }

    public BigDecimal getSuspendedFee() {return this.suspendedFee;}

    public Long getLoanId() {return this.loanId;}

    public Long getOfficeId() {return this.officeId;}

    public LocalDate getAccruedTill() {return this.accruedTill;}

    public PeriodFrequencyType getRepaymentFrequency() {return this.repaymentFrequency;}

    public Integer getRepayEvery() {return this.repayEvery;}

    public Integer getInstallmentNumber() { return this.installmentNumber;}

    public LocalDate getDueDate() {return this.dueDate;}

    public LocalDate getFromDate() {return this.fromDate;}

    public Long getRepaymentScheduleId() {return this.repaymentScheduleId;}

    public Long getLoanProductId() { return this.loanProductId;}

    public BigDecimal getInterestIncome() {return this.interestIncome;}

    public BigDecimal getFeeIncome() {return this.feeIncome;}

    public BigDecimal getPenaltyIncome() {return this.penaltyIncome;}

    public BigDecimal getWaivedInterestIncome() {return this.waivedInterestIncome;}

    public BigDecimal getAccruedInterestIncome() {return this.accruedInterestIncome;}

    public BigDecimal getAccruedFeeIncome() {return this.accruedFeeIncome;}

    public BigDecimal getAccruedPenaltyIncome() {return this.accruedPenaltyIncome;}

    public CurrencyData getCurrencyData() { return this.currencyData; }

    public LocalDate getInterestCalculatedFrom() {return this.interestCalculatedFrom;}

    public BigDecimal getSuspendedInterest() { return this.suspendedInterest;}

    public BigDecimal getSuspendedPenalty() { return this.suspendedPenalty; }

    public BigDecimal getInterestCompletedDerived() {return this.interestCompletedDerived;}

    public BigDecimal getInterestWaivedDerived() {return this.interestWaivedDerived;}

    public BigDecimal getFeeChargesCompletedDerived() {return this.feeChargesCompletedDerived;
    }

    public BigDecimal getFeeChargesWaivedDerived() {return this.feeChargesWaivedDerived;}

    public BigDecimal getPenaltyChargesCompletedDerived() {return this.penaltyChargesCompletedDerived;}

    public BigDecimal getPenaltyChargesWaivedDerived() {return this.penaltyChargesWaivedDerived;}

    public Map<LoanChargeData, BigDecimal> getAppliedCharge() {return this.appliedCharge;}

    public void updateAppliedCharge(Map<LoanChargeData, BigDecimal> appliedCharge) {this.appliedCharge = appliedCharge;}

    public BigDecimal getInterestToSuspend() {
        return this.interestToSuspend;
    }

    public BigDecimal getFeesToSuspend() {
        return this.feesToSuspend;
    }


    public void updateInterestToSuspend(){
        if(this.interestToSuspend == null ){
            this.interestToSuspend = BigDecimal.ZERO;
        }
    }

    public void updateFeesToSuspend() {
        if(this.feesToSuspend == null){
            this.feesToSuspend = BigDecimal.ZERO;
        }
    }

    public void updatePenaltyToSuspend() {
        if(this.penaltyToSuspend == null){
            this.penaltyToSuspend = BigDecimal.ZERO;
        }

    }

    public BigDecimal getPenaltyToSuspend() {
        return penaltyToSuspend;
    }

    @Override
    public String toString() {
        return "LoanScheduleSuspendedAccruedIncomeData{" +
                "loanId=" + loanId +
                ", officeId=" + officeId +
                ", accruedTill=" + accruedTill +
                ", repaymentFrequency=" + repaymentFrequency +
                ", repayEvery=" + repayEvery +
                ", installmentNumber=" + installmentNumber +
                ", dueDate=" + dueDate +
                ", fromDate=" + fromDate +
                ", repaymentScheduleId=" + repaymentScheduleId +
                ", loanProductId=" + loanProductId +
                ", interestIncome=" + interestIncome +
                ", feeIncome=" + feeIncome +
                ", penaltyIncome=" + penaltyIncome +
                ", waivedInterestIncome=" + waivedInterestIncome +
                ", accruedInterestIncome=" + accruedInterestIncome +
                ", accruedFeeIncome=" + accruedFeeIncome +
                ", accruedPenaltyIncome=" + accruedPenaltyIncome +
                ", currencyData=" + currencyData +
                ", interestCalculatedFrom=" + interestCalculatedFrom +
                ", suspendedInterest=" + suspendedInterest +
                ", suspendedFee=" + suspendedFee +
                ", suspendedPenalty=" + suspendedPenalty +
                ", interestCompletedDerived=" + interestCompletedDerived +
                ", interestWaivedDerived=" + interestWaivedDerived +
                ", feeChargesCompletedDerived=" + feeChargesCompletedDerived +
                ", feeChargesWaivedDerived=" + feeChargesWaivedDerived +
                ", penaltyChargesCompletedDerived=" + penaltyChargesCompletedDerived +
                ", penaltyChargesWaivedDerived=" + penaltyChargesWaivedDerived +
                ", appliedCharge=" + appliedCharge +
                '}';
    }
}
