/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.data;

import com.google.gson.Gson;

/** 
 * Immutable data object representing the API request body sent in the POST request to
 * the "/queue" resource 
 **/
public class EmailMessageApiQueueResourceData {
	private Long internalId;
	private String mifosTenantIdentifier;
	private String createdOnDate;
	private String sourceAddress;
	private String emailAddress;
	private String message;
	
	/** 
	 * EmailMessageApiQueueResourceData constructor
	 **/
	private EmailMessageApiQueueResourceData(Long internalId, String mifosTenantIdentifier, String createdOnDate,
			String sourceAddress, String emailAddress, String message) {
		this.internalId = internalId;
		this.mifosTenantIdentifier = mifosTenantIdentifier;
		this.createdOnDate = createdOnDate;
		this.sourceAddress = sourceAddress;
		this.emailAddress = emailAddress;
		this.message = message;
	}
	
	/** 
	 * EmailMessageApiQueueResourceData constructor
	 **/
	protected EmailMessageApiQueueResourceData() {}
	
	/** 
	 * @return a new instance of the EmailMessageApiQueueResourceData class
	 **/
	public static final EmailMessageApiQueueResourceData instance(Long internalId, String mifosTenantIdentifier, String createdOnDate,
																String sourceAddress, String emailAddress, String message) {
		
		return new EmailMessageApiQueueResourceData(internalId, mifosTenantIdentifier, createdOnDate, sourceAddress,
				emailAddress, message);
	}
	
	/**
	 * @return the internalId
	 */
	public Long getInternalId() {
		return internalId;
	}
	
	/**
	 * @return the mifosTenantIdentifier
	 */
	public String getMifosTenantIdentifier() {
		return mifosTenantIdentifier;
	}
	
	/**
	 * @return the createdOnDate
	 */
	public String getCreatedOnDate() {
		return createdOnDate;
	}

	/**
	 * @return the sourceAddress
	 */
	public String getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * @return the emailAddress
	 */
	public String getemailAddress() {
		return emailAddress;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/** 
	 * @return JSON representation of the object 
	 **/
	public String toJsonString() {
		Gson gson = new Gson();
		
		return gson.toJson(this);
	}
}
