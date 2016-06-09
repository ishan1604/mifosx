/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.rescheduleloan.service;

import org.mifosplatform.infrastructure.configuration.domain.ConfigurationDomainService;
import org.mifosplatform.organisation.holiday.domain.Holiday;
import org.mifosplatform.organisation.holiday.domain.HolidayRepository;
import org.mifosplatform.organisation.holiday.domain.HolidayStatusType;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.organisation.workingdays.domain.WorkingDays;
import org.mifosplatform.organisation.workingdays.domain.WorkingDaysRepositoryWrapper;
import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.charge.domain.ChargeCalculationType;
import org.mifosplatform.portfolio.charge.domain.ChargePaymentMode;
import org.mifosplatform.portfolio.charge.domain.ChargeTimeType;
import org.mifosplatform.portfolio.loanaccount.data.HolidayDetailDTO;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;
import org.mifosplatform.portfolio.loanaccount.loanschedule.domain.LoanRepaymentScheduleHistory;
import org.mifosplatform.portfolio.loanaccount.loanschedule.service.LoanScheduleHistoryWritePlatformService;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.DefaultLoanReschedulerFactory;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModel;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModelRepaymentPeriod;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequestRepository;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.exception.LoanRescheduleRequestNotFoundException;
import org.mifosplatform.portfolio.loanproduct.domain.InterestMethod;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProduct;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductMinimumRepaymentScheduleRelatedDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class LoanReschedulePreviewPlatformServiceImpl implements LoanReschedulePreviewPlatformService {

    private final LoanRescheduleRequestRepository loanRescheduleRequestRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final HolidayRepository holidayRepository;
    private final WorkingDaysRepositoryWrapper workingDaysRepository;
    private final LoanScheduleHistoryWritePlatformService loanScheduleHistoryWritePlatformService;

    @Autowired
    public LoanReschedulePreviewPlatformServiceImpl(final LoanRescheduleRequestRepository loanRescheduleRequestRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
            final ConfigurationDomainService configurationDomainService, final HolidayRepository holidayRepository,
            final WorkingDaysRepositoryWrapper workingDaysRepository,
            final LoanScheduleHistoryWritePlatformService loanScheduleHistoryWritePlatformService) {
        this.loanRescheduleRequestRepository = loanRescheduleRequestRepository;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.configurationDomainService = configurationDomainService;
        this.holidayRepository = holidayRepository;
        this.workingDaysRepository = workingDaysRepository;
        this.loanScheduleHistoryWritePlatformService = loanScheduleHistoryWritePlatformService;
    }

    @Override
    @Transactional
    public LoanRescheduleModel previewLoanReschedule(Long requestId) {
        final LoanRescheduleRequest loanRescheduleRequest = this.loanRescheduleRequestRepository.findOne(requestId);

        if (loanRescheduleRequest == null) { throw new LoanRescheduleRequestNotFoundException(requestId); }

        final Loan loan = loanRescheduleRequest.getLoan();
        final LoanProduct loanProduct = loan.getLoanProduct();
        final Collection<LoanCharge> loanChargeList = loan.charges();
        final Collection<Charge> loanProductCharges = loanProduct.getCharges();
        BigDecimal chargesDueAtTimeOfDisbursement = BigDecimal.ZERO;
        
        for (Charge loanProductCharge : loanProductCharges) {
            if (loanProductCharge.isLoanReschedulingFee()) {
                final BigDecimal loanPrincipal = loan.getPrincpal().getAmount();
                final BigDecimal chargeAmount = loanProductCharge.getAmount();
                final ChargeTimeType chargeTimeType = ChargeTimeType.fromInt(loanProductCharge.getChargeTime());
                final ChargeCalculationType chargeCalculationType = ChargeCalculationType.fromInt(loanProductCharge.getChargeCalculation());
                final ChargePaymentMode chargePaymentMode = ChargePaymentMode.fromInt(loanProductCharge.getChargePaymentMode());
                
                LoanCharge loanCharge = new LoanCharge(loan, loanProductCharge, loanPrincipal, chargeAmount, 
                        chargeTimeType, chargeCalculationType, loanRescheduleRequest.getRescheduleFromDate(), chargePaymentMode, null, BigDecimal.ZERO,null);
                
                loanChargeList.add(loanCharge);
            }
        }
        
        for (LoanCharge loanCharge : loanChargeList) {
            if (loanCharge.isDueAtDisbursement()) {
                chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(loanCharge.amount());
            }
        } 

        final boolean isHolidayEnabled = this.configurationDomainService.isRescheduleRepaymentsOnHolidaysEnabled();
        final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(loan.getOfficeId(), loan
                .getDisbursementDate().toDate(), HolidayStatusType.ACTIVE.getValue());
        final WorkingDays workingDays = this.workingDaysRepository.findOne();
        final LoanProductMinimumRepaymentScheduleRelatedDetail loanProductRelatedDetail = loan.getLoanRepaymentScheduleDetail();
        final MonetaryCurrency currency = loanProductRelatedDetail.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);

        final InterestMethod interestMethod = loan.getLoanRepaymentScheduleDetail().getInterestMethod();
        final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
        final MathContext mathContext = new MathContext(8, roundingMode);
        List<LoanRepaymentScheduleHistory> oldPeriods = this.loanScheduleHistoryWritePlatformService.createLoanScheduleArchive(
                loan.getRepaymentScheduleInstallments(), loan, loanRescheduleRequest);
        HolidayDetailDTO holidayDetailDTO = new HolidayDetailDTO(isHolidayEnabled, holidays, workingDays);
        LoanRescheduleModel loanRescheduleModel = new DefaultLoanReschedulerFactory().reschedule(mathContext, interestMethod,
                loanRescheduleRequest, applicationCurrency, holidayDetailDTO, loanChargeList);
        List<LoanRescheduleModelRepaymentPeriod> newPeriods = new ArrayList<>(loanRescheduleModel.getPeriods());
        
        // create a new LoanRescheduleModelRepaymentPeriod for the disbursement period
        LoanRescheduleModelRepaymentPeriod loanRescheduleModelRepaymentPeriod = LoanRescheduleModelRepaymentPeriod
                .instance(0, 0, null, loan.getDisbursementDate(), loan.getPrincpal(), null, Money.zero(currency), Money.of(currency, chargesDueAtTimeOfDisbursement), 
                        Money.zero(currency), Money.of(currency, chargesDueAtTimeOfDisbursement), false);
        
        // add disbursement period to the beginning of the list of new periods
        newPeriods.add(0, loanRescheduleModelRepaymentPeriod);
        
        // update the "periods" property of the LoanRescheduleModel object
        loanRescheduleModel.updatePeriods(newPeriods);
        
        LoanRescheduleModel loanRescheduleModelWithOldPeriods = LoanRescheduleModel.createWithSchedulehistory(loanRescheduleModel,
                oldPeriods);
        return loanRescheduleModelWithOldPeriods;
    }

}
