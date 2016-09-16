/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.data;

/** 
 * Immutable data object representing an outbound Email message delivery report data
 **/
public class EmailMessageDeliveryReportData {
	private Long id;
	private Long externalId;
	private String addedOnDate;
	private String deliveredOnDate;
	private Integer deliveryStatus;
	private Boolean hasError;
	private String errorMessage;
	
	/** 
	 * EmailMessageDeliveryReportData constructor
	 * 
	 * @return void 
	 **/
	private EmailMessageDeliveryReportData(Long id, Long externalId, String addedOnDate, String deliveredOnDate,
			Integer deliveryStatus, Boolean hasError, String errorMessage) {
		this.id = id;
		this.externalId = externalId;
		this.addedOnDate = addedOnDate;
		this.deliveredOnDate = deliveredOnDate;
		this.deliveryStatus = deliveryStatus;
		this.hasError = hasError;
		this.errorMessage = errorMessage;
	}
	
	/** 
	 * Default EmailMessageDeliveryReportData constructor
	 * 
	 * @return void
	 **/
	protected EmailMessageDeliveryReportData() {}
	
	/** 
	 * @return an instance of the EmailMessageDeliveryReportData class
	 **/
	public static EmailMessageDeliveryReportData getInstance(Long id, Long externalId, String addedOnDate, String deliveredOnDate,
														   Integer deliveryStatus, Boolean hasError, String errorMessage) {
		
		return new EmailMessageDeliveryReportData(id, externalId, addedOnDate, deliveredOnDate, deliveryStatus, hasError, errorMessage);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the externalId
	 */
	public Long getExternalId() {
		return externalId;
	}

	/**
	 * @return the addedOnDate
	 */
	public String getAddedOnDate() {
		return addedOnDate;
	}

	/**
	 * @return the deliveredOnDate
	 */
	public String getDeliveredOnDate() {
		return deliveredOnDate;
	}

	/**
	 * @return the deliveryStatus
	 */
	public Integer getDeliveryStatus() {
		return deliveryStatus;
	}

	/**
	 * @return the hasError
	 */
	public Boolean getHasError() {
		return hasError;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
