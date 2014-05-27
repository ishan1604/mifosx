/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 **/
package org.mifosplatform.infrastructure.sms.scheduler;

import java.util.Iterator;
import java.util.List;

import org.jsmpp.session.SMPPSession;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.sms.domain.SmsMessage;
import org.mifosplatform.infrastructure.sms.domain.SmsMessageRepository;
import org.mifosplatform.infrastructure.sms.domain.SmsMessageStatusType;
import org.mifosplatform.infrastructure.sms.gateways.SmsGatewayDeliveryReport;
import org.mifosplatform.infrastructure.sms.gateways.SmsGatewayMessage;
import org.mifosplatform.infrastructure.sms.gateways.infobip.InfobipSmsGatewayImpl;
import org.mifosplatform.infrastructure.sms.gateways.infobip.InfobipSmsGatewayUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/** 
 * Scheduled Job services to send SMS messages and get delivery reports from the SMS gateway 
 **/
public class SmsMessageScheduledJobServiceImpl implements SmsMessageScheduledJobService {
	
	private final SmsMessageRepository repository;
	private Boolean setupMessageDeliveryListener = true;
	private Boolean developmentMode = false;
	private SMPPSession session = InfobipSmsGatewayUtils.connectAndBindSession(InfobipSmsGatewayUtils.transceiverBindType(), developmentMode);
    private static final Logger logger = LoggerFactory.getLogger(SmsMessageScheduledJobServiceImpl.class);
    private final InfobipSmsGatewayImpl infobipSmsGatewayImpl = new InfobipSmsGatewayImpl();
	
	@Autowired
	/** 
	 * SmsMessageScheduledJobServiceImpl constructor
	 **/
	public SmsMessageScheduledJobServiceImpl(SmsMessageRepository repository) {
		super();
		this.repository = repository;
	}
	
	/** 
	 * Process delivery reports, update the status of the SMS messages
	 * 
	 * @return void
	 **/
	private void processDeliveryReports() {
		Iterator<SmsGatewayDeliveryReport> iterator = infobipSmsGatewayImpl.smsGatewayDeliveryReports.iterator();
		
		while(iterator.hasNext()) {
			// get the next iteration
			SmsGatewayDeliveryReport smsGatewayDeliveryReport = iterator.next();
			
			// get the SmsMessage object from the DB
			SmsMessage smsMessage = repository.findByExternalId(smsGatewayDeliveryReport.externalId());
			
			if(smsMessage != null) {
				// update the status of the SMS message
				smsMessage.setStatus(smsGatewayDeliveryReport.status());
				
				logger.info("SMS message with external ID '" + smsMessage.getExternalId() + "' successfully updated. Status set to: " + smsMessage.getStatusType().toString());
			}
			
			// the report has been processed, so remove from the list of delivery reports
			iterator.remove();
		}
	}

	@Override
	@Transactional
	@CronTarget(jobName = JobName.SEND_MESSAGES_TO_SMS_GATEWAY)
	public void sendMessages() {
		
		Pageable pageable = new PageRequest(0, getMaximumNumberOfMessagesToBeSent());
        List<SmsMessage> pendingMessages = repository.findPending(pageable);
        
        // check if the listener is already running and there's an active session
        if(setupMessageDeliveryListener) {
        	// setup the SMS message delivery listener
        	infobipSmsGatewayImpl.setupMessageDeliveryListener(session);
        }
        
        // only proceed if there are pending messages
        if(pendingMessages.size() > 0) {
        	
        	for(SmsMessage pendingMessage : pendingMessages) {
        		
        		SmsGatewayMessage smsGatewayMessage = new SmsGatewayMessage(pendingMessage.getId(), 
	        			pendingMessage.getExternalId(), pendingMessage.getMobileNo(), pendingMessage.getMessage());
	        	
        		// send message to SMS message gateway
	        	smsGatewayMessage = infobipSmsGatewayImpl.sendMessage(smsGatewayMessage, session);
	        	
	        	// check if the returned SmsGatewayMessage object has an external ID
	        	if(!StringUtils.isEmpty(smsGatewayMessage.externalId())) {
	        		
	        		// update the external ID of the SMS message in the DB
	        		pendingMessage.setExternalId(smsGatewayMessage.externalId());
	            	
	            	// update the status of the SMS message in the DB
	            	pendingMessage.setStatus(SmsMessageStatusType.SENT);
	            	
	            	logger.info(pendingMessage.getStatusType().toString());
	        	}
	        }
        }
        
        if(infobipSmsGatewayImpl.smsGatewayDeliveryReports.size() > 0) {
        	processDeliveryReports();
        }
        
        try { 
        	// the message delivery listener is already running, set "setupMessageDeliveryListener" 
        	// to false so we don't have to set it up again
        	setupMessageDeliveryListener = false;
        	
        	// put the thread to sleep for a minute
        	Thread.sleep(60000);
        	
        	logger.info(Long.toString(session.getLastActivityTimestamp()));
        	
    	} catch (InterruptedException e) {
    		logger.error("Sleeping thread interrupted : " + e.getMessage());
    		e.printStackTrace();
    	}
	}
	
	/** 
	 * Get the maximum number of messages to be sent to the SMS gateway
	 * 
	 * TODO this should be configurable, add to c_configuration
	 **/
	private int getMaximumNumberOfMessagesToBeSent() {
		return 100;
	}
}
