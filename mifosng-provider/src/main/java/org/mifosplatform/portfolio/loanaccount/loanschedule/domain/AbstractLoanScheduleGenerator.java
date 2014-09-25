/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.loanschedule.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifosplatform.organisation.holiday.domain.Holiday;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.organisation.workingdays.domain.WorkingDays;
import org.mifosplatform.portfolio.loanaccount.data.DisbursementData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.mifosplatform.portfolio.loanaccount.domain.LoanSummary;
import org.mifosplatform.portfolio.loanaccount.domain.LoanSummaryWrapper;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.MultiDisbursementDisbursementDateException;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.MultiDisbursementEmiAmountException;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.MultiDisbursementOutstandingAmoutException;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRepaymentScheduleHistory;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModel;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModelRepaymentPeriod;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductMinimumRepaymentScheduleRelatedDetail;
import org.mifosplatform.useradministration.domain.AppUser;

/**
 *
 */
public abstract class AbstractLoanScheduleGenerator implements LoanScheduleGenerator {

    private final ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
    private final PaymentPeriodsInOneYearCalculator paymentPeriodsInOneYearCalculator = new DefaultPaymentPeriodsInOneYearCalculator();

    @Override
    public LoanScheduleModel generate(final MathContext mc, final ApplicationCurrency applicationCurrency,
            final LoanApplicationTerms loanApplicationTerms, final Set<LoanCharge> loanCharges, final boolean isHolidayEnabled,
            final List<Holiday> holidays, final WorkingDays workingDays) {

        // 1. generate list of proposed schedule due dates
        final LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms, isHolidayEnabled, holidays,
                workingDays);
        loanApplicationTerms.updateLoanEndDate(loanEndDate);

        // 2. determine the total charges due at time of disbursement
        final BigDecimal chargesDueAtTimeOfDisbursement = deriveTotalChargesDueAtTimeOfDisbursement(loanCharges);

        // 3. setup variables for tracking important facts required for loan
        // schedule generation.
        Money principalDisbursed = loanApplicationTerms.getPrincipal();
        final Money expectedPrincipalDisburse = loanApplicationTerms.getPrincipal();
        final MonetaryCurrency currency = principalDisbursed.getCurrency();
        final int numberOfRepayments = loanApplicationTerms.getNumberOfRepayments();

        // variables for cumulative totals
        int loanTermInDays = Integer.valueOf(0);
        BigDecimal totalPrincipalExpected = BigDecimal.ZERO;
        final BigDecimal totalPrincipalPaid = BigDecimal.ZERO;
        BigDecimal totalInterestCharged = BigDecimal.ZERO;
        BigDecimal totalFeeChargesCharged = chargesDueAtTimeOfDisbursement;
        BigDecimal totalPenaltyChargesCharged = BigDecimal.ZERO;
        BigDecimal totalRepaymentExpected = chargesDueAtTimeOfDisbursement;
        final BigDecimal totalOutstanding = BigDecimal.ZERO;

        final Collection<LoanScheduleModelPeriod> periods = createNewLoanScheduleListWithDisbursementDetails(numberOfRepayments,
                loanApplicationTerms, chargesDueAtTimeOfDisbursement);

        // 4. Determine the total interest owed over the full loan.
        final Money totalInterestChargedForFullLoanTerm = loanApplicationTerms.calculateTotalInterestCharged(
                this.paymentPeriodsInOneYearCalculator, mc);

        LocalDate periodStartDate = loanApplicationTerms.getExpectedDisbursementDate();
        LocalDate actualRepaymentDate = periodStartDate;
        boolean isFirstRepayment = true;
        LocalDate firstRepaymentdate = this.scheduledDateGenerator.generateNextRepaymentDate(periodStartDate, loanApplicationTerms,
                isFirstRepayment);
        final LocalDate idealDisbursementDate = this.scheduledDateGenerator.idealDisbursementDateBasedOnFirstRepaymentDate(
                loanApplicationTerms.getLoanTermPeriodFrequencyType(), loanApplicationTerms.getRepaymentEvery(), firstRepaymentdate);

        LocalDate periodStartDateApplicableForInterest = periodStartDate;

        int periodNumber = 1;
        Money totalCumulativePrincipal = principalDisbursed.zero();
        Money totalCumulativeInterest = principalDisbursed.zero();
        Money totalOutstandingInterestPaymentDueToGrace = principalDisbursed.zero();
        Money outstandingBalance = principalDisbursed;
        if (loanApplicationTerms.isMultiDisburseLoan()) {
            BigDecimal disburseAmt = getDisbursementAmount(loanApplicationTerms, periodStartDate, periods, chargesDueAtTimeOfDisbursement);
            principalDisbursed = principalDisbursed.zero().plus(disburseAmt);
            loanApplicationTerms.setPrincipal(loanApplicationTerms.getPrincipal().zero().plus(disburseAmt));
            outstandingBalance = outstandingBalance.zero().plus(disburseAmt);
        }
        while (!outstandingBalance.isZero()) {
            actualRepaymentDate = this.scheduledDateGenerator.generateNextRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                    isFirstRepayment);
            isFirstRepayment = false;
            LocalDate scheduledDueDate = this.scheduledDateGenerator.adjustRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                    isHolidayEnabled, holidays, workingDays);
            final int daysInPeriod = Days.daysBetween(periodStartDate, scheduledDueDate).getDays();
            if (loanApplicationTerms.isMultiDisburseLoan()) {
                loanApplicationTerms.setFixedEmiAmountForPeriod(scheduledDueDate);
                BigDecimal disburseAmt = disbursementForPeriod(loanApplicationTerms, periodStartDate, scheduledDueDate, periods,
                        BigDecimal.ZERO);
                principalDisbursed = principalDisbursed.plus(disburseAmt);
                loanApplicationTerms.setPrincipal(loanApplicationTerms.getPrincipal().plus(disburseAmt));
                outstandingBalance = outstandingBalance.plus(disburseAmt);
                if (loanApplicationTerms.getMaxOutstandingBalance() != null
                        && outstandingBalance.isGreaterThan(loanApplicationTerms.getMaxOutstandingBalance())) {
                    String errorMsg = "Outstanding balance must not exceed the amount: " + loanApplicationTerms.getMaxOutstandingBalance();
                    throw new MultiDisbursementOutstandingAmoutException(errorMsg, loanApplicationTerms.getMaxOutstandingBalance()
                            .getAmount(), disburseAmt);
                }
            }
            int daysInPeriodApplicableForInterest = daysInPeriod;

            if (periodStartDate.isBefore(idealDisbursementDate)) {
                if (loanApplicationTerms.getInterestChargedFromLocalDate() != null) {
                    periodStartDateApplicableForInterest = loanApplicationTerms.getInterestChargedFromLocalDate();
                } else {
                    periodStartDateApplicableForInterest = idealDisbursementDate;
                }
                daysInPeriodApplicableForInterest = Days.daysBetween(periodStartDateApplicableForInterest, scheduledDueDate).getDays();
            }

            final double interestCalculationGraceOnRepaymentPeriodFraction = this.paymentPeriodsInOneYearCalculator
                    .calculatePortionOfRepaymentPeriodInterestChargingGrace(periodStartDateApplicableForInterest, scheduledDueDate,
                            loanApplicationTerms.getInterestChargedFromLocalDate(), loanApplicationTerms.getLoanTermPeriodFrequencyType(),
                            loanApplicationTerms.getRepaymentEvery());

            // 5 determine principal,interest of repayment period
            final PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                    this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalCumulativePrincipal,
                    totalCumulativeInterest, totalInterestChargedForFullLoanTerm, totalOutstandingInterestPaymentDueToGrace,
                    daysInPeriodApplicableForInterest, outstandingBalance, loanApplicationTerms, periodNumber, mc);

            if (loanApplicationTerms.getFixedEmiAmount() != null
                    && loanApplicationTerms.getFixedEmiAmount().compareTo(principalInterestForThisPeriod.interest().getAmount()) != 1) {
                String errorMsg = "EMI amount must be greter than : " + principalInterestForThisPeriod.interest().getAmount();
                throw new MultiDisbursementEmiAmountException(errorMsg, principalInterestForThisPeriod.interest().getAmount(),
                        loanApplicationTerms.getFixedEmiAmount());
            }
            // update cumulative fields for principal & interest
            totalCumulativePrincipal = totalCumulativePrincipal.plus(principalInterestForThisPeriod.principal());
            totalCumulativeInterest = totalCumulativeInterest.plus(principalInterestForThisPeriod.interest());
            totalOutstandingInterestPaymentDueToGrace = principalInterestForThisPeriod.interestPaymentDueToGrace();

            // 6. update outstandingLoanBlance using correct 'principalDue'
            outstandingBalance = outstandingBalance.minus(principalInterestForThisPeriod.principal());

            Money feeChargesForInstallment = principalDisbursed.zero();
            Money penaltyChargesForInstallment = principalDisbursed.zero();
            if (!(loanApplicationTerms.isMultiDisburseLoan() && loanApplicationTerms.getFixedEmiAmount() != null)) {
                // 7. determine fees and penalties
                feeChargesForInstallment = cumulativeFeeChargesDueWithin(periodStartDate, scheduledDueDate, loanCharges, currency,
                        principalInterestForThisPeriod, principalDisbursed, totalInterestChargedForFullLoanTerm, numberOfRepayments);
                penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(periodStartDate, scheduledDueDate, loanCharges, currency,
                        principalInterestForThisPeriod, principalDisbursed, totalInterestChargedForFullLoanTerm, numberOfRepayments);
            }
            // 8. sum up real totalInstallmentDue from components
            final Money totalInstallmentDue = principalInterestForThisPeriod.principal() //
                    .plus(principalInterestForThisPeriod.interest()) //
                    .plus(feeChargesForInstallment) //
                    .plus(penaltyChargesForInstallment);

            // 9. create repayment period from parts
            final LoanScheduleModelPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(periodNumber, periodStartDate,
                    scheduledDueDate, principalInterestForThisPeriod.principal(), outstandingBalance,
                    principalInterestForThisPeriod.interest(), feeChargesForInstallment, penaltyChargesForInstallment, totalInstallmentDue);
            periods.add(installment);

            // handle cumulative fields
            loanTermInDays += daysInPeriod;
            totalPrincipalExpected = totalPrincipalExpected.add(principalInterestForThisPeriod.principal().getAmount());
            totalInterestCharged = totalInterestCharged.add(principalInterestForThisPeriod.interest().getAmount());
            totalFeeChargesCharged = totalFeeChargesCharged.add(feeChargesForInstallment.getAmount());
            totalPenaltyChargesCharged = totalPenaltyChargesCharged.add(penaltyChargesForInstallment.getAmount());
            totalRepaymentExpected = totalRepaymentExpected.add(totalInstallmentDue.getAmount());
            periodStartDate = scheduledDueDate;
            periodStartDateApplicableForInterest = periodStartDate;

            periodNumber++;
        }

        if (principalDisbursed.isNotEqualTo(expectedPrincipalDisburse)) {
            final String errorMsg = "One of the Disbursement date is not falling on Loan Schedule";
            throw new MultiDisbursementDisbursementDateException(errorMsg);
        }

        if (loanApplicationTerms.isMultiDisburseLoan() && loanApplicationTerms.getFixedEmiAmount() != null && !loanCharges.isEmpty()) {
            // 7. determine fees and penalties
            for (LoanScheduleModelPeriod loanScheduleModelPeriod : periods) {
                if (loanScheduleModelPeriod.isRepaymentPeriod()) {
                    PrincipalInterest principalInterest = new PrincipalInterest(Money.of(currency, loanScheduleModelPeriod.principalDue()),
                            Money.of(currency, loanScheduleModelPeriod.interestDue()), null);
                    Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(loanScheduleModelPeriod.periodFromDate(),
                            loanScheduleModelPeriod.periodDueDate(), loanCharges, currency, principalInterest, principalDisbursed,
                            totalCumulativeInterest, numberOfRepayments);
                    Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(loanScheduleModelPeriod.periodFromDate(),
                            loanScheduleModelPeriod.periodDueDate(), loanCharges, currency, principalInterest, principalDisbursed,
                            totalCumulativeInterest, numberOfRepayments);
                    totalFeeChargesCharged = totalFeeChargesCharged.add(feeChargesForInstallment.getAmount());
                    totalPenaltyChargesCharged = totalPenaltyChargesCharged.add(penaltyChargesForInstallment.getAmount());
                    loanScheduleModelPeriod.addLoanCharges(feeChargesForInstallment.getAmount(), penaltyChargesForInstallment.getAmount());
                }
            }
        }

        return LoanScheduleModel.from(periods, applicationCurrency, loanTermInDays, principalDisbursed, totalPrincipalExpected,
                totalPrincipalPaid, totalInterestCharged, totalFeeChargesCharged, totalPenaltyChargesCharged, totalRepaymentExpected,
                totalOutstanding);
    }
    
    @Override
	public LoanRescheduleModel reschedule(final MathContext mathContext, final LoanRescheduleRequest loanRescheduleRequest, 
			final ApplicationCurrency applicationCurrency, final boolean isHolidayEnabled, 
			final List<Holiday> holidays, final WorkingDays workingDays) {
		
		final Loan loan = loanRescheduleRequest.getLoan();
		final LoanSummary loanSummary = loan.getSummary();
		final LoanProductMinimumRepaymentScheduleRelatedDetail loanProductRelatedDetail = loan.getLoanRepaymentScheduleDetail();
		final MonetaryCurrency currency = loanProductRelatedDetail.getCurrency();
		
		// create an archive of the current loan schedule installments
		Collection<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = createLoanScheduleArchive(loanRescheduleRequest);
		
		// get the initial list of repayment installments
		List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments = loan.getRepaymentScheduleInstallments();
		
		// sort list by installment number in ASC order
		Collections.sort(repaymentScheduleInstallments, LoanRepaymentScheduleInstallment.installmentNumberComparator);
		
		final Collection<LoanRescheduleModelRepaymentPeriod> periods = new ArrayList<LoanRescheduleModelRepaymentPeriod>();
		
		Money outstandingLoanBalance = loan.getPrincpal();
		
		for(LoanRepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallments) {
			
			Integer oldPeriodNumber = repaymentScheduleInstallment.getInstallmentNumber();
			LocalDate fromDate = repaymentScheduleInstallment.getFromDate();
			LocalDate dueDate = repaymentScheduleInstallment.getDueDate();
			Money principalDue = repaymentScheduleInstallment.getPrincipal(currency);
			Money interestDue = repaymentScheduleInstallment.getInterestCharged(currency);
			Money feeChargesDue = repaymentScheduleInstallment.getFeeChargesCharged(currency);
			Money penaltyChargesDue = repaymentScheduleInstallment.getPenaltyChargesCharged(currency);
			Money totalDue = principalDue.plus(interestDue).plus(feeChargesDue).plus(penaltyChargesDue);
			
			outstandingLoanBalance = outstandingLoanBalance.minus(principalDue);
			
			LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod.instance(oldPeriodNumber, oldPeriodNumber, 
					fromDate, dueDate, principalDue, outstandingLoanBalance, interestDue, feeChargesDue, penaltyChargesDue, 
					totalDue, false);
			
			periods.add(period);
		}
		
        Money outstandingBalance = loan.getPrincpal();
		Money totalCumulativePrincipal = Money.zero(currency);
		Money totalCumulativeInterest = Money.zero(currency);
		Money actualTotalCumulativeInterest = Money.zero(currency);
		Money totalOutstandingInterestPaymentDueToGrace = Money.zero(currency);
		Money totalPrincipalBeforeReschedulePeriod = Money.zero(currency);
        
		LocalDate installmentDueDate = null;
		LocalDate adjustedInstallmentDueDate = null;
		LocalDate installmentFromDate = null;
		Integer rescheduleFromInstallmentNo = defaultToZeroIfNull(loanRescheduleRequest.getRescheduleFromInstallment());
		Integer installmentNumber = rescheduleFromInstallmentNo;
		Integer graceOnPrincipal = defaultToZeroIfNull(loanRescheduleRequest.getGraceOnPrincipal());
		Integer graceOnInterest = defaultToZeroIfNull(loanRescheduleRequest.getGraceOnInterest());
		Integer extraTerms = defaultToZeroIfNull(loanRescheduleRequest.getExtraTerms());
		final boolean recalculateInterest = loanRescheduleRequest.getRecalculateInterest();
		Integer numberOfRepayments = repaymentScheduleInstallments.size();
		Integer rescheduleNumberOfRepayments = numberOfRepayments;
		final Money principal = loan.getPrincpal();
		final Money totalPrincipalOutstanding = Money.of(currency, loanSummary.getTotalPrincipalOutstanding());
		LocalDate adjustedDueDate = loanRescheduleRequest.getAdjustedDueDate();
		BigDecimal newInterestRate = loanRescheduleRequest.getInterestRate();
		int loanTermInDays = Integer.valueOf(0);
		
		if(rescheduleFromInstallmentNo > 0) {
			// this will hold the loan repayment installment that is before the reschedule start installment 
			// (rescheduleFrominstallment)
			LoanRepaymentScheduleInstallment previousInstallment = null;
			
			// get the install number of the previous installment
			int previousInstallmentNo = rescheduleFromInstallmentNo - 1;
			
			// only fetch the installment if the number is greater than 0
			if(previousInstallmentNo > 0) {
				previousInstallment = loan.fetchRepaymentScheduleInstallment(previousInstallmentNo);
			}
			
			LoanRepaymentScheduleInstallment firstInstallment = loan.fetchRepaymentScheduleInstallment(1);
			
			// the "installment from date" is equal to the due date of the previous installment, if it exists
			if(previousInstallment != null) {
				installmentFromDate = previousInstallment.getDueDate();
			}
			
			else {
				installmentFromDate = firstInstallment.getFromDate();
			}
			
			installmentDueDate = installmentFromDate;
			LocalDate periodStartDateApplicableForInterest = installmentFromDate;
			Integer periodNumber = 1;
			outstandingLoanBalance = loan.getPrincpal();
			
			for(LoanRescheduleModelRepaymentPeriod period : periods) {
				
				if(period.periodDueDate().isBefore(loanRescheduleRequest.getRescheduleFromDate())) {
					
					totalPrincipalBeforeReschedulePeriod = totalPrincipalBeforeReschedulePeriod.plus(period.principalDue());
					actualTotalCumulativeInterest = actualTotalCumulativeInterest.plus(period.interestDue());
					rescheduleNumberOfRepayments--;
					outstandingLoanBalance = outstandingLoanBalance.minus(period.principalDue());
					outstandingBalance = outstandingBalance.minus(period.principalDue());
				}
			}
			
			while(graceOnPrincipal > 0 || graceOnInterest > 0) {
				
				LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod.instance(0, 0, new LocalDate(), 
						new LocalDate(), Money.zero(currency), Money.zero(currency), Money.zero(currency), Money.zero(currency), 
						Money.zero(currency), Money.zero(currency), true);
				
				periods.add(period);
				
				if(graceOnPrincipal > 0) {
					graceOnPrincipal--;
				}
				
				if(graceOnInterest > 0) {
					graceOnInterest--;
				}
				
				rescheduleNumberOfRepayments++;
				numberOfRepayments++;
			}
			
			while(extraTerms > 0) {
				
				LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod.instance(0, 0, new LocalDate(), 
						new LocalDate(), Money.zero(currency), Money.zero(currency), Money.zero(currency), Money.zero(currency), 
						Money.zero(currency), Money.zero(currency), true);
				
				periods.add(period);
				
				extraTerms--;
				rescheduleNumberOfRepayments++;
				numberOfRepayments++;
			}
			
			// get the loan application terms from the Loan object
			final LoanApplicationTerms loanApplicationTerms = loan.getLoanApplicationTerms(applicationCurrency);
			
			// update the number of repayments
			loanApplicationTerms.updateNumberOfRepayments(numberOfRepayments);
			
			LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms, isHolidayEnabled, 
					holidays, workingDays);
	        loanApplicationTerms.updateLoanEndDate(loanEndDate);
			
			if(newInterestRate != null) {
				loanApplicationTerms.updateAnnualNominalInterestRate(newInterestRate);
				loanApplicationTerms.updateInterestRatePerPeriod(newInterestRate);
			}
			
			graceOnPrincipal = defaultToZeroIfNull(loanRescheduleRequest.getGraceOnPrincipal());
			graceOnInterest = defaultToZeroIfNull(loanRescheduleRequest.getGraceOnInterest());
			
			loanApplicationTerms.updateInterestPaymentGrace(graceOnInterest);
			loanApplicationTerms.updatePrincipalGrace(graceOnPrincipal); 
			
			loanApplicationTerms.setPrincipal(totalPrincipalOutstanding);
            loanApplicationTerms.updateNumberOfRepayments(rescheduleNumberOfRepayments);
            loanApplicationTerms.updateLoanTermFrequency(rescheduleNumberOfRepayments);
            loanApplicationTerms.updateInterestChargedFromDate(periodStartDateApplicableForInterest);
            
            Money totalInterestChargedForFullLoanTerm = loanApplicationTerms.calculateTotalInterestCharged(
	                this.paymentPeriodsInOneYearCalculator, mathContext);
            
            if(!recalculateInterest && newInterestRate == null) {
            	totalInterestChargedForFullLoanTerm = Money.of(currency, loanSummary.getTotalInterestCharged());
	            totalInterestChargedForFullLoanTerm = totalInterestChargedForFullLoanTerm.minus(actualTotalCumulativeInterest);
	            
	            loanApplicationTerms.updateTotalInterestDue(totalInterestChargedForFullLoanTerm);
            }
            
            for(LoanRescheduleModelRepaymentPeriod period : periods) {
				
				if(period.periodDueDate().isEqual(loanRescheduleRequest.getRescheduleFromDate()) || 
						period.periodDueDate().isAfter(loanRescheduleRequest.getRescheduleFromDate()) ||
						period.isNew()) {
					
					installmentDueDate = this.scheduledDateGenerator.generateNextRepaymentDate(installmentDueDate, loanApplicationTerms, 
							false);
					
					if(adjustedDueDate != null && periodNumber == 1) {
						installmentDueDate = adjustedDueDate;
					}
					
					adjustedInstallmentDueDate = this.scheduledDateGenerator.adjustRepaymentDate(installmentDueDate, loanApplicationTerms, 
							isHolidayEnabled, holidays, workingDays);
					
					final int daysInInstallment = Days.daysBetween(installmentFromDate, adjustedInstallmentDueDate).getDays();
					
					period.updatePeriodNumber(installmentNumber);
		            period.updatePeriodFromDate(installmentFromDate);
		            period.updatePeriodDueDate(adjustedInstallmentDueDate);
					
					double interestCalculationGraceOnRepaymentPeriodFraction = this.paymentPeriodsInOneYearCalculator
		                    .calculatePortionOfRepaymentPeriodInterestChargingGrace(periodStartDateApplicableForInterest, adjustedInstallmentDueDate,
		                    		periodStartDateApplicableForInterest, loanApplicationTerms.getLoanTermPeriodFrequencyType(),
		                            loanApplicationTerms.getRepaymentEvery());
					
					// ========================= Calculate the interest due ========================================
					
					// change the principal to => Principal Disbursed - Total Principal Paid
					// interest calculation is always based on the total principal outstanding
					loanApplicationTerms.setPrincipal(totalPrincipalOutstanding);
		            
					// determine the interest & principal for the period
		            PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
		                    this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalCumulativePrincipal,
		                    totalCumulativeInterest, totalInterestChargedForFullLoanTerm, totalOutstandingInterestPaymentDueToGrace,
		                    daysInInstallment, outstandingBalance, loanApplicationTerms, periodNumber, mathContext);
		            
		            // update the interest due for the period
		            period.updateInterestDue(principalInterestForThisPeriod.interest());
		            
		            // =============================================================================================
		            
		            // ========================== Calculate the principal due ======================================
		            
		            // change the principal to => Principal Disbursed - Total cumulative Principal Amount before the reschedule installment
		            loanApplicationTerms.setPrincipal(principal.minus(totalPrincipalBeforeReschedulePeriod));
		            
		            principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
		                    this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalCumulativePrincipal,
		                    totalCumulativeInterest, totalInterestChargedForFullLoanTerm, totalOutstandingInterestPaymentDueToGrace,
		                    daysInInstallment, outstandingBalance, loanApplicationTerms, periodNumber, mathContext);
		            
		            period.updatePrincipalDue(principalInterestForThisPeriod.principal());
		            
		            // ==============================================================================================
		            
		            outstandingLoanBalance = outstandingLoanBalance.minus(period.principalDue());
		            period.updateOutstandingLoanBalance(outstandingLoanBalance);
		            
		            Money principalDue = Money.of(currency, period.principalDue());
		            Money interestDue = Money.of(currency, period.interestDue());
		            
		            if(principalDue.isZero() && interestDue.isZero()) {
		            	period.updateFeeChargesDue(Money.zero(currency));
		            	period.updatePenaltyChargesDue(Money.zero(currency));
		            }
		            
		            Money feeChargesDue = Money.of(currency, period.feeChargesDue());
		            Money penaltyChargesDue = Money.of(currency, period.penaltyChargesDue());
		            
		            Money totalDue = principalDue
		            		.plus(interestDue)
		            		.plus(feeChargesDue)
		            		.plus(penaltyChargesDue);
		            
		            period.updateTotalDue(totalDue);
		            
		            // update cumulative fields for principal & interest
		            totalCumulativePrincipal = totalCumulativePrincipal.plus(period.principalDue());
		            totalCumulativeInterest = totalCumulativeInterest.plus(period.interestDue());
		            actualTotalCumulativeInterest = actualTotalCumulativeInterest.plus(period.interestDue());
		            totalOutstandingInterestPaymentDueToGrace = principalInterestForThisPeriod.interestPaymentDueToGrace();
					
					installmentFromDate = adjustedInstallmentDueDate;
					installmentNumber++;
					periodNumber++;
					loanTermInDays += daysInInstallment;
					
					outstandingBalance = outstandingBalance.minus(period.principalDue());
				}
			}
		}
		
		final Money totalRepaymentExpected = principal // get the loan Principal amount
                .plus(actualTotalCumulativeInterest) // add the actual total cumulative interest
                .plus(loanSummary.getTotalFeeChargesCharged()) // add the total fees charged
                .plus(loanSummary.getTotalPenaltyChargesCharged()); // finally add the total penalty charged
		
		return LoanRescheduleModel.instance(periods, loanRepaymentScheduleHistoryList, applicationCurrency, loanTermInDays, 
				loan.getPrincpal(), loan.getPrincpal().getAmount(), loanSummary.getTotalPrincipalRepaid(), actualTotalCumulativeInterest.getAmount(), 
				loanSummary.getTotalFeeChargesCharged(), loanSummary.getTotalPenaltyChargesCharged(), totalRepaymentExpected.getAmount(), 
				loanSummary.getTotalOutstanding());
	}
    
    private Collection<LoanRepaymentScheduleHistory> createLoanScheduleArchive(final LoanRescheduleRequest loanRescheduleRequest) {
    	final Loan loan = loanRescheduleRequest.getLoan();
    	final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments = loan.getRepaymentScheduleInstallments();
    	final LoanProductMinimumRepaymentScheduleRelatedDetail loanProductRelatedDetail = loan.getLoanRepaymentScheduleDetail();
		final MonetaryCurrency currency = loanProductRelatedDetail.getCurrency();
		List<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = new ArrayList<LoanRepaymentScheduleHistory>();
    	
    	for(LoanRepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallments) {
    		final Integer installmentNumber = repaymentScheduleInstallment.getInstallmentNumber();
    		Date fromDate = null;
    		Date dueDate = null;
    		
    		if(repaymentScheduleInstallment.getFromDate() != null) {
    			fromDate = repaymentScheduleInstallment.getFromDate().toDate();
    		}
    		
    		if(repaymentScheduleInstallment.getDueDate() != null) {
    			dueDate = repaymentScheduleInstallment.getDueDate().toDate();
    		}
    		
    		final BigDecimal principal = repaymentScheduleInstallment.getPrincipal(currency).getAmount();
    		final BigDecimal principalCompleted = repaymentScheduleInstallment.getPrincipalCompleted(currency).getAmount();
    		final BigDecimal principalWrittenOff = repaymentScheduleInstallment.getPrincipalWrittenOff(currency).getAmount();
    		final BigDecimal interestCharged = repaymentScheduleInstallment.getInterestCharged(currency).getAmount();
    		final BigDecimal interestPaid = repaymentScheduleInstallment.getInterestPaid(currency).getAmount();
    		final BigDecimal interestWaived = repaymentScheduleInstallment.getInterestWaived(currency).getAmount();
    		final BigDecimal interestWrittenOff = repaymentScheduleInstallment.getInterestWrittenOff(currency).getAmount();
    		final BigDecimal feeChargesCharged = repaymentScheduleInstallment.getFeeChargesCharged(currency).getAmount();
    		final BigDecimal feeChargesPaid = repaymentScheduleInstallment.getFeeChargesPaid(currency).getAmount();
    		final BigDecimal feeChargesWrittenOff = repaymentScheduleInstallment.getFeeChargesWrittenOff(currency).getAmount();
    		final BigDecimal feeChargesWaived = repaymentScheduleInstallment.getFeeChargesWaived(currency).getAmount();
    		final BigDecimal penaltyCharges = repaymentScheduleInstallment.getPenaltyChargesCharged(currency).getAmount();
    		final BigDecimal penaltyChargesPaid = repaymentScheduleInstallment.getPenaltyChargesPaid(currency).getAmount();
    		final BigDecimal penaltyChargesWrittenOff = repaymentScheduleInstallment.getPenaltyChargesWrittenOff(currency).getAmount();
    		final BigDecimal penaltyChargesWaived = repaymentScheduleInstallment.getPenaltyChargesWaived(currency).getAmount();
    		final BigDecimal totalPaidInAdvance = repaymentScheduleInstallment.getTotalPaidInAdvance();
    		final BigDecimal totalPaidLate = repaymentScheduleInstallment.getTotalPaidLate();
    		final boolean obligationsMet = repaymentScheduleInstallment.isObligationsMet();
    		Date obligationsMetOnDate = null;
    		
    		if(repaymentScheduleInstallment.getObligationsMetOnDate() != null) {
    			obligationsMetOnDate = repaymentScheduleInstallment.getObligationsMetOnDate().toDate();
    		}
    		
    		Date createdOnDate = null;
    		
    		if(repaymentScheduleInstallment.getCreatedDate() != null) {
    			createdOnDate = repaymentScheduleInstallment.getCreatedDate().toDate();
    		}
    		
    		final AppUser createdByUser = repaymentScheduleInstallment.getCreatedBy();
    		final AppUser lastModifiedByUser = repaymentScheduleInstallment.getLastModifiedBy();
    		
    		Date lastModifiedOnDate = null;
    		
    		if(repaymentScheduleInstallment.getLastModifiedDate() != null) {
    			lastModifiedOnDate = repaymentScheduleInstallment.getLastModifiedDate().toDate();
    		}
    		
    		LoanRepaymentScheduleHistory loanRepaymentScheduleHistory = LoanRepaymentScheduleHistory.instance(loan, 
    				loanRescheduleRequest, installmentNumber, fromDate, dueDate, principal, principalCompleted, 
    				principalWrittenOff, interestCharged, interestPaid, interestWaived, interestWrittenOff, 
    				feeChargesCharged, feeChargesPaid, feeChargesWrittenOff, feeChargesWaived, penaltyCharges, 
    				penaltyChargesPaid, penaltyChargesWrittenOff, penaltyChargesWaived, totalPaidInAdvance, totalPaidLate, 
    				obligationsMet, obligationsMetOnDate, createdOnDate, createdByUser, lastModifiedByUser, lastModifiedOnDate);
    		
    		loanRepaymentScheduleHistoryList.add(loanRepaymentScheduleHistory);
    	}
    	
    	return loanRepaymentScheduleHistoryList;
    }

    public abstract PrincipalInterest calculatePrincipalInterestComponentsForPeriod(PaymentPeriodsInOneYearCalculator calculator,
            double interestCalculationGraceOnRepaymentPeriodFraction, Money totalCumulativePrincipal, Money totalCumulativeInterest,
            Money totalInterestDueForLoan, Money cumulatingInterestPaymentDueToGrace, int daysInPeriodApplicableForInterest,
            Money outstandingBalance, LoanApplicationTerms loanApplicationTerms, int periodNumber, MathContext mc);

    protected final boolean isLastRepaymentPeriod(final int numberOfRepayments, final int periodNumber) {
        return periodNumber == numberOfRepayments;
    }

    private BigDecimal deriveTotalChargesDueAtTimeOfDisbursement(final Set<LoanCharge> loanCharges) {
        BigDecimal chargesDueAtTimeOfDisbursement = BigDecimal.ZERO;
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isDueAtDisbursement()) {
                chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(loanCharge.amount());
            }
        }
        return chargesDueAtTimeOfDisbursement;
    }

    private BigDecimal disbursementForPeriod(final LoanApplicationTerms loanApplicationTerms, LocalDate startDate, LocalDate endDate,
            final Collection<LoanScheduleModelPeriod> periods, final BigDecimal chargesDueAtTimeOfDisbursement) {
        BigDecimal principal = BigDecimal.ZERO;
        MonetaryCurrency currency = loanApplicationTerms.getPrincipal().getCurrency();
        for (DisbursementData disbursementData : loanApplicationTerms.getDisbursementDatas()) {
            if (disbursementData.isDueForDisbursement(startDate, endDate)) {
                final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                        disbursementData.disbursementDate(), Money.of(currency, disbursementData.amount()), chargesDueAtTimeOfDisbursement);
                periods.add(disbursementPeriod);
                principal = principal.add(disbursementData.amount());
            }
        }
        return principal;
    }

    private BigDecimal getDisbursementAmount(final LoanApplicationTerms loanApplicationTerms, LocalDate disbursementDate,
            final Collection<LoanScheduleModelPeriod> periods, final BigDecimal chargesDueAtTimeOfDisbursement) {
        BigDecimal principal = BigDecimal.ZERO;
        MonetaryCurrency currency = loanApplicationTerms.getPrincipal().getCurrency();
        for (DisbursementData disbursementData : loanApplicationTerms.getDisbursementDatas()) {
            if (disbursementData.disbursementDate().equals(disbursementDate)) {
                final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                        disbursementData.disbursementDate(), Money.of(currency, disbursementData.amount()), chargesDueAtTimeOfDisbursement);
                periods.add(disbursementPeriod);
                principal = principal.add(disbursementData.amount());
            }
        }
        return principal;
    }

    private Collection<LoanScheduleModelPeriod> createNewLoanScheduleListWithDisbursementDetails(final int numberOfRepayments,
            final LoanApplicationTerms loanApplicationTerms, final BigDecimal chargesDueAtTimeOfDisbursement) {

        Collection<LoanScheduleModelPeriod> periods = null;
        if (loanApplicationTerms.isMultiDisburseLoan()) {
            periods = new ArrayList<LoanScheduleModelPeriod>(numberOfRepayments + loanApplicationTerms.getDisbursementDatas().size());
        } else {
            periods = new ArrayList<LoanScheduleModelPeriod>(numberOfRepayments + 1);
            final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                    loanApplicationTerms, chargesDueAtTimeOfDisbursement);
            periods.add(disbursementPeriod);
        }

        return periods;
    }

    private Money cumulativeFeeChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd, final Set<LoanCharge> loanCharges,
            final MonetaryCurrency monetaryCurrency, final PrincipalInterest principalInterestForThisPeriod,
            final Money principalDisbursed, final Money totalInterestChargedForFullLoanTerm, int numberOfRepayments) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isFeeCharge()) {
                if (loanCharge.isInstalmentFee()) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        BigDecimal amount = BigDecimal.ZERO;
                        if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                            amount = amount.add(principalInterestForThisPeriod.principal().getAmount()).add(
                                    principalInterestForThisPeriod.interest().getAmount());
                        } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                            amount = amount.add(principalInterestForThisPeriod.interest().getAmount());
                        } else {
                            amount = amount.add(principalInterestForThisPeriod.principal().getAmount());
                        }
                        BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                        cumulative = cumulative.plus(loanChargeAmt);
                    } else {
                        cumulative = cumulative.plus(loanCharge.amount().divide(BigDecimal.valueOf(numberOfRepayments)));
                    }
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    BigDecimal amount = BigDecimal.ZERO;
                    if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                        amount = amount.add(principalDisbursed.getAmount()).add(totalInterestChargedForFullLoanTerm.getAmount());
                    } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                        amount = amount.add(totalInterestChargedForFullLoanTerm.getAmount());
                    } else {
                        amount = amount.add(principalDisbursed.getAmount());
                    }
                    BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                    cumulative = cumulative.plus(loanChargeAmt);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                }
            }
        }

        return cumulative;
    }

    private Money cumulativePenaltyChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency monetaryCurrency,
            final PrincipalInterest principalInterestForThisPeriod, final Money principalDisbursed,
            final Money totalInterestChargedForFullLoanTerm, int numberOfRepayments) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                if (loanCharge.isInstalmentFee()) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        BigDecimal amount = BigDecimal.ZERO;
                        if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                            amount = amount.add(principalInterestForThisPeriod.principal().getAmount()).add(
                                    principalInterestForThisPeriod.interest().getAmount());
                        } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                            amount = amount.add(principalInterestForThisPeriod.interest().getAmount());
                        } else {
                            amount = amount.add(principalInterestForThisPeriod.principal().getAmount());
                        }
                        BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                        cumulative = cumulative.plus(loanChargeAmt);
                    } else {
                        cumulative = cumulative.plus(loanCharge.amount().divide(BigDecimal.valueOf(numberOfRepayments)));
                    }
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                }else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    BigDecimal amount = BigDecimal.ZERO;
                    if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                        amount = amount.add(principalDisbursed.getAmount()).add(totalInterestChargedForFullLoanTerm.getAmount());
                    } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                        amount = amount.add(totalInterestChargedForFullLoanTerm.getAmount());
                    } else {
                        amount = amount.add(principalDisbursed.getAmount());
                    }
                    BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                    cumulative = cumulative.plus(loanChargeAmt);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                }
            }
        }

        return cumulative;
    }
    
    /** 
     * set the value to zero if the provided value is null 
     * 
     * @return integer value equal/greater than 0
     **/
    private Integer defaultToZeroIfNull(Integer value) {
    	
    	return (value != null) ? value : 0;
    }
}