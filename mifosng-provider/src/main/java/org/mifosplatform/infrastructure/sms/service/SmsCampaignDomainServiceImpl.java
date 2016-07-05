/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepository;
import org.mifosplatform.infrastructure.sms.domain.*;
import org.mifosplatform.portfolio.account.service.AccountTransfersReadPlatformService;
import org.mifosplatform.portfolio.account.service.AccountTransfersWritePlatformService;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.common.service.BusinessEventListner;
import org.mifosplatform.portfolio.common.service.BusinessEventNotifierService;
import org.mifosplatform.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.mifosplatform.portfolio.savings.domain.DepositAccountOnHoldTransactionRepository;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountDomainService;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountTransactionSummaryWrapper;
import org.mifosplatform.portfolio.savings.domain.SavingsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefan on 4-7-2016.
 */

@Service
public class SmsCampaignDomainServiceImpl implements SmsCampaignDomainService {

    private final SmsCampaignRepository smsCampaignRepository;
    private final SmsMessageRepository smsMessageRepository;
    private final ClientRepository clientRepository;
    private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository;
    private final Map<Long, Long> releaseLoanIds = new HashMap<>(2);
    private final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final SavingsAccountTransactionSummaryWrapper savingsAccountTransactionSummaryWrapper;
    private final SavingsHelper savingsHelper;
    private final AccountTransfersReadPlatformService accountTransfersReadPlatformService;
    private final SmsCampaignWritePlatformCommandHandlerImpl smsCampaignWritePlatformCommandHandler;
    private final CodeValueRepository codeValueRepository;
    private final PaymentDetailRepository paymentDetailRepository;

    @Autowired
    public SmsCampaignDomainServiceImpl(final SmsCampaignRepository smsCampaignRepository, final SmsMessageRepository smsMessageRepository,
                                        final AccountTransfersWritePlatformService accountTransfersWritePlatformService,
                                        final BusinessEventNotifierService businessEventNotifierService,
                                        final DepositAccountOnHoldTransactionRepository depositAccountOnHoldTransactionRepository,
                                        final SavingsAccountDomainService savingsAccountDomainService,
                                        final SavingsAccountTransactionSummaryWrapper savingsAccountTransactionSummaryWrapper,
                                        final AccountTransfersReadPlatformService accountTransfersReadPlatformService,
                                        final CodeValueRepository codeValueRepository, final ClientRepository clientRepository,
                                        final PaymentDetailRepository paymentDetailRepository, final SmsCampaignWritePlatformCommandHandlerImpl smsCampaignWritePlatformCommandHandler){
        this.smsCampaignRepository = smsCampaignRepository;
        this.smsMessageRepository = smsMessageRepository;
        this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
        this.businessEventNotifierService = businessEventNotifierService;
        this.depositAccountOnHoldTransactionRepository = depositAccountOnHoldTransactionRepository;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.savingsAccountTransactionSummaryWrapper = savingsAccountTransactionSummaryWrapper;
        this.accountTransfersReadPlatformService = accountTransfersReadPlatformService;
        this.savingsHelper = new SavingsHelper(this.accountTransfersReadPlatformService);
        this.codeValueRepository = codeValueRepository;
        this.paymentDetailRepository = paymentDetailRepository;
        this.clientRepository = clientRepository;
        this.smsCampaignWritePlatformCommandHandler = smsCampaignWritePlatformCommandHandler;
    }

    @PostConstruct
    public void addListners() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_APPROVED, new SendSmsOnLoanApproved());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REJECTED, new SendSmsOnLoanRejected());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT, new SendSmsOnLoanRepayment());
    }

    private void notifyRejectedLoanOwner(Loan loan) {
        ArrayList<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("loan rejected");
        if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
                this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(campaign.getParamValue(),campaign.getMessage(),campaign.getCampaignName());
            }
        }
    }

    private void notifyAcceptedLoanOwner(Loan loan) {
        ArrayList<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("loan accepted");

        ///Find sms campaign by id or something
        //call insertDirecet message
        if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
                this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(campaign.getParamValue(),campaign.getMessage(),campaign.getCampaignName());
            }
        }
    }

    private void sendSmsForLoanRepayment(Loan loan) {
        ArrayList<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("loan repayment");
        if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
                this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(campaign.getParamValue(),campaign.getMessage(),campaign.getCampaignName());
            }
        }
    }

    private ArrayList<SmsCampaign> retrieveSmsCampaigns(String paramValue){
        List<SmsCampaign> initialSmsCampaignList = smsCampaignRepository.findByCampaignType(SmsCampaignType.TRIGGERED.getValue());
        ArrayList<SmsCampaign> smsCampaigns = new ArrayList();

        for(SmsCampaign campaign : initialSmsCampaignList){
            if(campaign.getParamValue().toLowerCase().contains(paramValue)){
                smsCampaigns.add(campaign);
            }
        }
        return smsCampaigns;
    }

    private class SendSmsOnLoanApproved implements BusinessEventListner{

        @Override
        public void businessEventToBeExecuted(@SuppressWarnings("unused") AbstractPersistable<Long> businessEventEntity) {

        }

        @Override
        public void businessEventWasExecuted(AbstractPersistable<Long> businessEventEntity) {
            if (businessEventEntity instanceof Loan) {
                Loan loan = (Loan) businessEventEntity;
                notifyAcceptedLoanOwner(loan);
            }
        }
    }

    private class SendSmsOnLoanRejected implements BusinessEventListner{

        @Override
        public void businessEventToBeExecuted(AbstractPersistable<Long> businessEventEntity) {

        }

        @Override
        public void businessEventWasExecuted(AbstractPersistable<Long> businessEventEntity) {
            if (businessEventEntity instanceof Loan) {
                Loan loan = (Loan) businessEventEntity;
                notifyRejectedLoanOwner(loan);
            }
        }
    }

    private class SendSmsOnLoanRepayment implements BusinessEventListner{

        @Override
        public void businessEventToBeExecuted(@SuppressWarnings("unused") AbstractPersistable<Long> businessEventEntity) {

        }

        @Override
        public void businessEventWasExecuted(AbstractPersistable<Long> businessEventEntity) {
            if (businessEventEntity instanceof Loan) {
                Loan loan = (Loan) businessEventEntity;
                sendSmsForLoanRepayment(loan);
            }
        }
    }
}
