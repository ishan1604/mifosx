/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.scheduler;

import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.scheduledemail.data.*;
import org.mifosplatform.infrastructure.scheduledemail.domain.*;
import org.mifosplatform.infrastructure.scheduledemail.service.EmailConfigurationReadPlatformService;
import org.mifosplatform.infrastructure.scheduledemail.service.EmailReadPlatformService;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.reportmailingjob.helper.IPv4Helper;
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

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Scheduled job services that send Email messages and get delivery reports for the sent Email messages
 **/
@Service
public class EmailMessageScheduledJobServiceImpl implements EmailMessageScheduledJobService {
	
	private final EmailMessageRepository emailMessageRepository;
	private final EmailConfigurationRepository emailConfigurationRepository;
	private final EmailReadPlatformService emailReadPlatformService;
	private final EmailConfigurationReadPlatformService configurationReadPlatformService;
	private static final Logger logger = LoggerFactory.getLogger(EmailMessageScheduledJobServiceImpl.class);
	private final RestTemplate restTemplate = new RestTemplate();
    
    /** 
	 * EmailMessageScheduledJobServiceImpl constructor
	 **/
	@Autowired
	public EmailMessageScheduledJobServiceImpl(EmailMessageRepository emailMessageRepository,
											   EmailConfigurationReadPlatformService readPlatformService,
											   EmailReadPlatformService emailReadPlatformService,
											   EmailConfigurationRepository emailConfigurationRepository) {
		this.emailMessageRepository = emailMessageRepository;
		this.emailConfigurationRepository = emailConfigurationRepository;
		this.configurationReadPlatformService = readPlatformService;
		this.emailReadPlatformService = emailReadPlatformService;
	}
	
	/** 
     * get the tenant's Email configuration
     * 
     * @return {@link TenantEmailConfiguration} object
     **/
    private TenantEmailConfiguration getTenantEmailConfiguration() {
    	Collection<EmailConfigurationData> configurationDataCollection = configurationReadPlatformService.retrieveAll();
    	
    	return TenantEmailConfiguration.instance(configurationDataCollection);
    }
	
	/** 
	 * get a new HttpEntity with the provided body
	 **/
	private HttpEntity<String> getHttpEntity(String body, String apiAuthUsername, String apiAuthPassword) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        String authorization = apiAuthUsername + ":" + apiAuthPassword;

        byte[] encodedAuthorisation = Base64.encode(authorization.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));
		
		return new HttpEntity<String>(body, headers);
	}
	
	/** 
	 * prevents the SSL security certificate check 
	 **/
	private void trustAllSSLCertificates() {
		TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
 
                public void checkClientTrusted(
                    X509Certificate[] certs, String authType) {
                }
 
                public void checkServerTrusted(
                    X509Certificate[] certs, String authType) {
                }
            }
        };
		
		try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            
            // Create all-trusting host name verifier
    		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
    			@Override
    			public boolean verify(String hostname, SSLSession session) {
    				return true;
    			}
    		};

    		// Install the all-trusting host verifier
    		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        } 
		
		catch (Exception e) { 
			// do nothing
		}
	}
	
	/** 
     * Format destination phone number so it is in international format without the leading
     * "0" or "+", example: 31612345678 
     * 
     * @param phoneNumber the phone number to be formated
     * @param countryCallingCode the country calling code
     * @return phone number in international format
     **/
    private String formatDestinationPhoneNumber(String phoneNumber, String countryCallingCode) {
    	String formatedPhoneNumber = "";
    	
    	try {
    		Long phoneNumberToLong = Long.parseLong(phoneNumber);
    		Long countryCallingCodeToLong = Long.parseLong(countryCallingCode);
    		formatedPhoneNumber = Long.toString(countryCallingCodeToLong) + Long.toString(phoneNumberToLong);
    	}
    	
    	catch(Exception e) {
    		logger.error("Invalid phone number or country calling code, must contain only numbers", e);
    	}
    	
    	return formatedPhoneNumber;
    }

	/**
	 * Send batches of Email messages to the Email gateway (or intermediate gateway)
	 **/
	@Override
	@Transactional
	@CronTarget(jobName = JobName.SEND_MESSAGES_TO_EMAIL_GATEWAY)
	public void sendMessages() {
	    if (IPv4Helper.applicationIsNotRunningOnLocalMachine()) {
	        final TenantEmailConfiguration tenantEmailConfiguration = this.getTenantEmailConfiguration();
	        final String apiAuthUsername = tenantEmailConfiguration.getApiAuthUsername();
	        final String apiAuthPassword = tenantEmailConfiguration.getApiAuthPassword();
	        final String apiBaseUrl = tenantEmailConfiguration.getApiBaseUrl();
	        final String sourceAddress = tenantEmailConfiguration.getSourceAddress();
	        final String countryCallingCode = tenantEmailConfiguration.getCountryCallingCode();
	        final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
	        final int httpEntityLimit = 500;
	        final int httpEntityLimitMinusOne = httpEntityLimit - 1;
	        
	        Integer emailCredits = tenantEmailConfiguration.getEmailCredits();
	        Integer emailSqlLimit = 5000;
	        
	        if(emailCredits > 0) {
	            try{
	                emailSqlLimit = (emailSqlLimit > emailCredits) ? emailCredits : emailSqlLimit;
	                final Collection<EmailData> pendingMessages = this.emailReadPlatformService.retrieveAllPending(emailSqlLimit);
	                
	                if(pendingMessages.size() > 0) {
	                    Iterator<EmailData> pendingMessageIterator = pendingMessages.iterator();
	                    int index = 0;
	                    
	                    // ====================== start point json string ======================================
	                    StringBuilder httpEntity = new StringBuilder("[");
	                    
	                    while(pendingMessageIterator.hasNext()) {
							EmailData emailData = pendingMessageIterator.next();
							EmailMessageApiQueueResourceData apiQueueResourceData =
									EmailMessageApiQueueResourceData.instance(emailData.getId(), tenant.getTenantIdentifier(),
	                                        null, sourceAddress, formatDestinationPhoneNumber(emailData.getEmailAddress(), countryCallingCode),
	                                        emailData.getMessage());
	                        
	                        httpEntity.append(apiQueueResourceData.toJsonString());
	                        
	                        index++;
	                        
	                        if (index == httpEntityLimitMinusOne) {
	                            httpEntity.append("]");
	                            
	                            emailCredits = this.sendMessages(httpEntity, apiAuthUsername, apiAuthPassword, apiBaseUrl, emailCredits,
	                                    sourceAddress);
	                            
	                            index = 0;
	                            httpEntity = new StringBuilder("[");
	                        }
	                        
	                        // add comma separation if iterator has more elements
	                        if(pendingMessageIterator.hasNext() && (index > 0)) {
	                            httpEntity.append(", ");
	                        }
	                    }
	                    
	                    httpEntity.append("]");
	                    // ====================== end point json string ====================================
	                    
	                    emailCredits = this.sendMessages(httpEntity, apiAuthUsername, apiAuthPassword, apiBaseUrl, emailCredits, sourceAddress);
	                    
	                    logger.info(pendingMessages.size() + " pending message(s) successfully sent to the intermediate gateway - mlite-scheduledemail");

						EmailConfiguration emailConfiguration = this.emailConfigurationRepository.findByName("EMAIL_CREDITS");
	                    emailConfiguration.setValue(emailCredits.toString());
	                    
	                    // save the EmailConfiguration entity
	                    this.emailConfigurationRepository.save(emailConfiguration);
	                }
	            }
	            
	            catch(Exception e) {
	                logger.error(e.getMessage(), e);
	            }
	        }
	    }
	}
	
	/**
	 * handles the sending of messages to the intermediate gateway and updating of the external ID, status and sources address
	 * of each message
	 * 
	 * @param httpEntity
	 * @param apiAuthUsername
	 * @param apiAuthPassword
	 * @param apiBaseUrl
	 * @param emailCredits
	 * @param sourceAddress
	 * @return the number of Email credits left
	 */
	private Integer sendMessages(final StringBuilder httpEntity, final String apiAuthUsername, 
	        final String apiAuthPassword, final String apiBaseUrl, Integer emailCredits, final String sourceAddress) {
	    // trust all SSL certificates
        trustAllSSLCertificates();
        
        // make request
        final ResponseEntity<EmailMessageApiResponseData> entity = restTemplate.postForEntity(apiBaseUrl + "/queue",
                getHttpEntity(httpEntity.toString(), apiAuthUsername, apiAuthPassword),
				EmailMessageApiResponseData.class);
        
        final List<EmailMessageDeliveryReportData> emailMessageDeliveryReportDataList = entity.getBody().getData();
        final Iterator<EmailMessageDeliveryReportData> deliveryReportIterator = emailMessageDeliveryReportDataList.iterator();
        
        while(deliveryReportIterator.hasNext()) {
			EmailMessageDeliveryReportData emailMessageDeliveryReportData = deliveryReportIterator.next();
            
            if(!emailMessageDeliveryReportData.getHasError()) {
				EmailMessage emailMessage = this.emailMessageRepository.findOne(emailMessageDeliveryReportData.getId());
                
                // initially set the status type enum to 100
                Integer statusType = EmailMessageStatusType.PENDING.getValue();
                
                switch(emailMessageDeliveryReportData.getDeliveryStatus()) {
                    case 100:
                    case 200:
                        statusType = EmailMessageStatusType.SENT.getValue();
                        break;
                        
                    case 300:
                        statusType = EmailMessageStatusType.DELIVERED.getValue();
                        break;
                        
                    case 400:
                        statusType = EmailMessageStatusType.FAILED.getValue();
                        break;
                        
                    default:
                        statusType = EmailMessageStatusType.INVALID.getValue();
                        break;
                }
                
                // update the externalId of the Email message
                emailMessage.setExternalId(emailMessageDeliveryReportData.getExternalId());
                
                // update the Email message sender
                emailMessage.setSourceAddress(sourceAddress);
                
                // update the status Type enum
                emailMessage.setStatusType(statusType);
                
                // save the EmailMessage entity
                this.emailMessageRepository.save(emailMessage);
                
                // deduct one credit from the tenant's Email credits
                emailCredits--;
            }
        }
        
        return emailCredits;
	}

	/**
	 * get Email message delivery reports from the Email gateway (or intermediate gateway)
	 **/
	@Override
	@Transactional
	@CronTarget(jobName = JobName.GET_DELIVERY_REPORTS_FROM_EMAIL_GATEWAY)
	public void getDeliveryReports() {
	    if (IPv4Helper.applicationIsNotRunningOnLocalMachine()) {
	        final TenantEmailConfiguration tenantEmailConfiguration = this.getTenantEmailConfiguration();
	        final String apiAuthUsername = tenantEmailConfiguration.getApiAuthUsername();
	        final String apiAuthPassword = tenantEmailConfiguration.getApiAuthPassword();
	        final String apiBaseUrl = tenantEmailConfiguration.getApiBaseUrl();
	        final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
	        
	        try{
	            List<Long> emailMessageExternalIds = this.emailReadPlatformService.retrieveExternalIdsOfAllSent(0);
				EmailMessageApiReportResourceData emailMessageApiReportResourceData =
						EmailMessageApiReportResourceData.instance(emailMessageExternalIds, tenant.getTenantIdentifier());
	            
	            // only proceed if there are scheduledemail message with status type enum 200
	            if(emailMessageExternalIds.size() > 0) {
	                // trust all SSL certificates
	                trustAllSSLCertificates();
	                
	                // make request
	                ResponseEntity<EmailMessageApiResponseData> entity = restTemplate.postForEntity(apiBaseUrl + "/report",
	                        getHttpEntity(emailMessageApiReportResourceData.toJsonString(), apiAuthUsername, apiAuthPassword),
							EmailMessageApiResponseData.class);
	                
	                List<EmailMessageDeliveryReportData> emailMessageDeliveryReportDataList = entity.getBody().getData();
	                Iterator<EmailMessageDeliveryReportData> iterator1 = emailMessageDeliveryReportDataList.iterator();
	                
	                while(iterator1.hasNext()) {
						EmailMessageDeliveryReportData emailMessageDeliveryReportData = iterator1.next();
	                    Integer deliveryStatus = emailMessageDeliveryReportData.getDeliveryStatus();
	                    
	                    if(!emailMessageDeliveryReportData.getHasError() && (deliveryStatus != 100 && deliveryStatus != 200)) {
							EmailMessage emailMessage = this.emailMessageRepository.findOne(emailMessageDeliveryReportData.getId());
	                        Integer statusType = emailMessage.getStatusType();
	                        boolean statusChanged = false;
	                        
	                        switch(deliveryStatus) {
	                            case 0:
	                                statusType = EmailMessageStatusType.INVALID.getValue();
	                                break;
	                            case 300:
	                                statusType = EmailMessageStatusType.DELIVERED.getValue();
	                                break;
	                                
	                            case 400:
	                                statusType = EmailMessageStatusType.FAILED.getValue();
	                                break;
	                                
	                            default:
	                                statusType = emailMessage.getStatusType();
	                                break;
	                        }
	                        
	                        statusChanged = !statusType.equals(emailMessage.getStatusType());
	                        
	                        // update the status Type enum
	                        emailMessage.setStatusType(statusType);
	                        
	                        // save the EmailMessage entity
	                        this.emailMessageRepository.save(emailMessage);
	                        
	                        if (statusChanged) {
	                            logger.info("Status of Email message id: " + emailMessage.getId() + " successfully changed to " + statusType);
	                        }
	                    }
	                }
	                
	                if(emailMessageDeliveryReportDataList.size() > 0) {
	                    logger.info(emailMessageDeliveryReportDataList.size() + " "
	                            + "delivery report(s) successfully received from the intermediate gateway - mlite-scheduledemail");
	                }
	            }
	        }
	        
	        catch(Exception e) {
	            logger.error(e.getMessage(), e);
	        }
	    }
	}
}
