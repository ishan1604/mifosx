/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 **/
package org.mifosplatform.infrastructure.sms.scheduler;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.sms.data.SmsConfigurationData;
import org.mifosplatform.infrastructure.sms.data.SmsData;
import org.mifosplatform.infrastructure.sms.data.SmsMessageApiQueueResourceData;
import org.mifosplatform.infrastructure.sms.data.SmsMessageApiReportResourceData;
import org.mifosplatform.infrastructure.sms.data.SmsMessageApiResponseData;
import org.mifosplatform.infrastructure.sms.data.SmsMessageDeliveryReportData;
import org.mifosplatform.infrastructure.sms.domain.SmsConfiguration;
import org.mifosplatform.infrastructure.sms.domain.SmsConfigurationRepository;
import org.mifosplatform.infrastructure.sms.domain.SmsMessage;
import org.mifosplatform.infrastructure.sms.domain.SmsMessageRepository;
import org.mifosplatform.infrastructure.sms.domain.SmsMessageStatusType;
import org.mifosplatform.infrastructure.sms.service.SmsConfigurationReadPlatformService;
import org.mifosplatform.infrastructure.sms.service.SmsReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Scheduled job services that send SMS messages and get delivery reports for the sent SMS messages
 **/
@Service
public class SmsMessageScheduledJobServiceImpl implements SmsMessageScheduledJobService {
	
	private final SmsMessageRepository smsMessageRepository;
	private final SmsConfigurationRepository smsConfigurationRepository;
	private final SmsReadPlatformService smsReadPlatformService;
	private final SmsConfigurationReadPlatformService configurationReadPlatformService;
	private static final Logger logger = LoggerFactory.getLogger(SmsMessageScheduledJobServiceImpl.class);
	RestTemplate restTemplate = new RestTemplate();
    private final String apiAuthUsername;
    private final String apiAuthPassword;
    private final String apiBaseUrl;
    HttpEntity<String> requestEntity;
    private final Map<String, String> configuration = new HashMap<String, String>();
    private final String sourceAddress;
    private final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
    private Integer smsCredits;
    private Integer smsSqlLimit = 300;
    
    /** 
	 * SmsMessageScheduledJobServiceImpl constructor
	 **/
	@Autowired
	public SmsMessageScheduledJobServiceImpl(SmsMessageRepository smsMessageRepository, 
			SmsConfigurationReadPlatformService readPlatformService, 
			SmsReadPlatformService smsReadPlatformService,
			SmsConfigurationRepository smsConfigurationRepository) {
		this.smsMessageRepository = smsMessageRepository;
		this.smsConfigurationRepository = smsConfigurationRepository;
		this.configurationReadPlatformService = readPlatformService;
		this.smsReadPlatformService = smsReadPlatformService;
		setConfiguration();
		apiAuthUsername = configuration.get("API_AUTH_USERNAME");
		apiAuthPassword = configuration.get("API_AUTH_PASSWORD");
		apiBaseUrl = configuration.get("API_BASE_URL");
		sourceAddress = configuration.get("SMS_SOURCE_ADDRESS");
		smsCredits = Integer.parseInt(configuration.get("SMS_CREDITS"));
	}
	
	/** 
     * get all configuration from the db and put them in the "configuration" map 
     * 
     * @return void
     **/
    private void setConfiguration() {
    	Collection<SmsConfigurationData> configurationDataCollection = configurationReadPlatformService.retrieveAll();
    	
    	Iterator<SmsConfigurationData> iterator = configurationDataCollection.iterator();
    	
    	while(iterator.hasNext()) {
    		SmsConfigurationData configurationData = iterator.next();
    		
    		configuration.put(configurationData.getName(), configurationData.getValue());
    	}
    }
	
	/** 
	 * get a new HttpEntity with the provided body
	 **/
	private HttpEntity<String> getHttpEntity(String body) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        String authorization = apiAuthUsername + ":" + apiAuthPassword;

        byte[] encodedAuthorisation = Base64.encode(authorization.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
		
		return new HttpEntity<String>(body, headers);
	}

	/**
	 * Send batches of SMS messages to the SMS gateway (or intermediate gateway)
	 **/
	@Override
	@Transactional
	@CronTarget(jobName = JobName.SEND_MESSAGES_TO_SMS_GATEWAY)
	public void sendMessages() {
		
		if(smsCredits > 0) {
			
			try{
				smsSqlLimit = (smsSqlLimit > smsCredits) ? smsCredits : smsSqlLimit;
				Collection<SmsData> pendingMessages = this.smsReadPlatformService.retrieveAllPending(smsSqlLimit);
				
				if(pendingMessages.size() > 0) {
					
					Iterator<SmsData> iterator1 = pendingMessages.iterator();
					
					// ====================== start point json string ======================================
					StringBuilder httpEntity = new StringBuilder("[");
					
					while(iterator1.hasNext()) {
						SmsData smsData = iterator1.next();
						SmsMessageApiQueueResourceData apiQueueResourceData = 
								SmsMessageApiQueueResourceData.instance(smsData.getId(), tenant.getTenantIdentifier(), 
										null, sourceAddress, smsData.getMobileNo(), smsData.getMessage());
						
						httpEntity.append(apiQueueResourceData.toJsonString());
						
						// add comma separation if iterator has more elements
						if(iterator1.hasNext()) {
							httpEntity.append(", ");
						}
					}
					
					httpEntity.append("]");
					// ====================== end point json string ====================================
					
					ResponseEntity<SmsMessageApiResponseData> entity = restTemplate.postForEntity(apiBaseUrl + "/queue",
						    getHttpEntity(httpEntity.toString()), 
						    SmsMessageApiResponseData.class);
					
					List<SmsMessageDeliveryReportData> smsMessageDeliveryReportDataList = entity.getBody().getData();
					Iterator<SmsMessageDeliveryReportData> iterator2 = smsMessageDeliveryReportDataList.iterator();
					
					while(iterator2.hasNext()) {
						SmsMessageDeliveryReportData smsMessageDeliveryReportData = iterator2.next();
						
						if(!smsMessageDeliveryReportData.getHasError()) {
							SmsMessage smsMessage = this.smsMessageRepository.findOne(smsMessageDeliveryReportData.getId());
							
							// initially set the status type enum to 100
							Integer statusType = SmsMessageStatusType.PENDING.getValue();
							
							switch(smsMessageDeliveryReportData.getDeliveryStatus()) {
								case 100:
								case 200:
									statusType = SmsMessageStatusType.SENT.getValue();
									break;
									
								case 300:
									statusType = SmsMessageStatusType.DELIVERED.getValue();
									break;
									
								case 400:
									statusType = SmsMessageStatusType.FAILED.getValue();
									break;
									
								default:
									statusType = SmsMessageStatusType.INVALID.getValue();
									break;
							}
							
							// update the externalId of the SMS message
							smsMessage.setExternalId(smsMessageDeliveryReportData.getExternalId());
							
							// update the SMS message sender
							smsMessage.setSourceAddress(sourceAddress);
							
							// update the status Type enum
							smsMessage.setStatusType(statusType);
							
							// deduct one credit from the tenant's SMS credits
							smsCredits--;
						}
					}
					
					SmsConfiguration smsConfiguration = this.smsConfigurationRepository.findByName("SMS_CREDITS");
					smsConfiguration.setValue(smsCredits.toString());
				}
			}
			
			catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * get SMS message delivery reports from the SMS gateway (or intermediate gateway)
	 **/
	@Override
	@Transactional
	@CronTarget(jobName = JobName.GET_DELIVERY_REPORTS_FROM_SMS_GATEWAY)
	public void getDeliveryReports() {
		
		try{
			List<Long> smsMessageExternalIds = this.smsReadPlatformService.retrieveExternalIdsOfAllSent(smsSqlLimit);
			SmsMessageApiReportResourceData smsMessageApiReportResourceData = 
					SmsMessageApiReportResourceData.instance(smsMessageExternalIds, tenant.getTenantIdentifier());
			
			ResponseEntity<SmsMessageApiResponseData> entity = restTemplate.postForEntity(apiBaseUrl + "/report",
				    getHttpEntity(smsMessageApiReportResourceData.toJsonString()), 
				    SmsMessageApiResponseData.class);
			
			List<SmsMessageDeliveryReportData> smsMessageDeliveryReportDataList = entity.getBody().getData();
			Iterator<SmsMessageDeliveryReportData> iterator1 = smsMessageDeliveryReportDataList.iterator();
			
			while(iterator1.hasNext()) {
				SmsMessageDeliveryReportData smsMessageDeliveryReportData = iterator1.next();
				Integer deliveryStatus = smsMessageDeliveryReportData.getDeliveryStatus();
				
				if(!smsMessageDeliveryReportData.getHasError() && (deliveryStatus != 100 && deliveryStatus != 200)) {
					SmsMessage smsMessage = this.smsMessageRepository.findOne(smsMessageDeliveryReportData.getId());
					Integer statusType = smsMessage.getStatusType();
					
					switch(deliveryStatus) {
						case 0:
							statusType = SmsMessageStatusType.INVALID.getValue();
							break;
						case 300:
							statusType = SmsMessageStatusType.DELIVERED.getValue();
							break;
							
						case 400:
							statusType = SmsMessageStatusType.FAILED.getValue();
							break;
							
						default:
							statusType = smsMessage.getStatusType();
							break;
					}
					
					// update the status Type enum
					smsMessage.setStatusType(statusType);
				}
			}
		}
		
		catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
