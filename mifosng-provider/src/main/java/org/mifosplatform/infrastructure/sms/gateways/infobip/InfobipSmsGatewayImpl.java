/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 **/
package org.mifosplatform.infrastructure.sms.gateways.infobip;

import java.util.ArrayList;
import java.util.List;

import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.MessageType;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.mifosplatform.infrastructure.sms.domain.SmsMessageStatusType;
import org.mifosplatform.infrastructure.sms.gateways.SmsGateway;
import org.mifosplatform.infrastructure.sms.gateways.SmsGatewayDeliveryReport;
import org.mifosplatform.infrastructure.sms.gateways.SmsGatewayMessage;
import org.mifosplatform.infrastructure.sms.scheduler.SmsMessageScheduledJobServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Infobip SMS gateway services is use in sending out sms messages.
 * More information - http://www.infobip.com/messaging/wholesale/apis/ 
 **/
public class InfobipSmsGatewayImpl implements SmsGateway {
	
	public List<SmsGatewayDeliveryReport> smsGatewayDeliveryReports = new ArrayList<SmsGatewayDeliveryReport>();
	private static final Logger logger = LoggerFactory.getLogger(SmsMessageScheduledJobServiceImpl.class);
	
	/** 
	 * InfobipSmsGatewayImpl constructor 
	 **/
	public InfobipSmsGatewayImpl() {}

	/**
	 * @see org.mifosplatform.infrastructure.sms.gateways.SmsGateway#sendMessages(java.util.List)
	 **/
	@Override
	public List<SmsGatewayMessage> sendMessages(List<SmsGatewayMessage> smsGatewayMessages, SMPPSession session) {
		List<SmsGatewayMessage> sentSmsGatewayMessages = new ArrayList<SmsGatewayMessage>(smsGatewayMessages.size());
		
		if(smsGatewayMessages.size() > 0) {
			for(SmsGatewayMessage smsGatewayMessage : smsGatewayMessages) {
				sentSmsGatewayMessages.add(InfobipSmsGatewayUtils.submitShortMessage(smsGatewayMessage, session));
			}
		}
		
		return sentSmsGatewayMessages;
	}
	
	/**
	 * @see org.mifosplatform.infrastructure.sms.gateways.SmsGateway#sendMessage
	 **/
	@Override
	public SmsGatewayMessage sendMessage(SmsGatewayMessage smsGatewayMessage, SMPPSession session) {
		return InfobipSmsGatewayUtils.submitShortMessage(smsGatewayMessage, session);
	}
	
	/** 
	 * Setup the SMS message delivery listener, listens for delivery reports 
	 * 
	 * param session SMPPSession object
	 * @return void
	 **/
	public void setupMessageDeliveryListener(SMPPSession session) {
		session.setMessageReceiverListener(new MessageReceiverListener() {
            @Override
			public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
            	if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
                   try {
                        DeliveryReceipt deliveryReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
                        String messageId = deliveryReceipt.getId();
                        
                        SmsMessageStatusType messageStatus = null;
                        
                        switch(deliveryReceipt.getFinalStatus()) {
                        	case DELIVRD:
                        		messageStatus = SmsMessageStatusType.DELIVERED;
                        		break;
                        		
                        	case REJECTD:
                        	case EXPIRED:
                        	case UNDELIV:
                        		// rejected, expired and undelivered are grouped as failed 
                        		messageStatus = SmsMessageStatusType.FAILED;
                        		break;
                        		
							default:
								// in all other cases the status is invalid
								messageStatus = SmsMessageStatusType.INVALID;
								break;
                        }
                        
                        // create a new SmsGatewayDeliveryReport object with data received from the SMS gateway
                        SmsGatewayDeliveryReport smsGatewayDeliveryReport = new SmsGatewayDeliveryReport(messageId, deliveryReceipt.getSubmitDate(), deliveryReceipt.getDoneDate(), messageStatus);
                        
                        // add the SmsGatewayDeliveryReport object to the smsGatewayDeliveryReports List
                        smsGatewayDeliveryReports.add(smsGatewayDeliveryReport);
                        
                        logger.info("Receiving delivery report for message '" + messageId + "' : " + smsGatewayDeliveryReport.toString());
                   } catch (InvalidDeliveryReceiptException e) {
                	   logger.error("Failed getting delivery report");
                        
                        e.printStackTrace();
                    }
                } else {
                    // inbound SMS message, this is currently not enabled - should never get in here
                	logger.info("Receiving message : " + new String(deliverSm.getShortMessage()));
                }
            }
            
            @Override
			public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
                    throws ProcessRequestException {
                return null;
            }
            
            @Override
			public void onAcceptAlertNotification(
                    AlertNotification alertNotification) {
            }
        });
	}
}
