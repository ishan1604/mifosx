/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.data;

import java.util.List;

/** 
 * Immutable data object representing an outbound Email message API response data
 **/
public class EmailMessageApiResponseData {
	private Integer httpStatusCode;
	private List<EmailMessageDeliveryReportData> data;
	
	/** 
	 * EmailMessageApiResponseData constructor
	 * 
	 * @return void 
	 **/
	private EmailMessageApiResponseData(Integer httpStatusCode, List<EmailMessageDeliveryReportData> data) {
		this.httpStatusCode = httpStatusCode;
		this.data = data;
	}
	
	/** 
	 * Default EmailMessageApiResponseData constructor
	 * 
	 * @return void
	 **/
	protected EmailMessageApiResponseData() {}
	
	/** 
	 * @return an instance of the EmailMessageApiResponseData class
	 **/
	public static EmailMessageApiResponseData getInstance(Integer httpStatusCode, List<EmailMessageDeliveryReportData> data) {
		return new EmailMessageApiResponseData(httpStatusCode, data);
	}

	/**
	 * @return the httpStatusCode
	 */
	public Integer getHttpStatusCode() {
		return httpStatusCode;
	}

	/**
	 * @return the data
	 */
	public List<EmailMessageDeliveryReportData> getData() {
		return data;
	}
}
