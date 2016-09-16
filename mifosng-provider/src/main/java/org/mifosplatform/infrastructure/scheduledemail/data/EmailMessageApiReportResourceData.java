/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.data;

import com.google.gson.Gson;

import java.util.List;

/** 
 * Immutable data object representing the API request body sent in the POST request to
 * the "/report" resource 
 **/
public class EmailMessageApiReportResourceData {
	private List<Long> externalIds;
	private String mifosTenantIdentifier;
	
	/** 
	 * EmailMessageApiReportResourceData constructor
	 **/
	private EmailMessageApiReportResourceData(List<Long> externalIds, String mifosTenantIdentifier) {
		this.externalIds = externalIds;
		this.mifosTenantIdentifier = mifosTenantIdentifier;
	}
	
	/** 
	 * EmailMessageApiReportResourceData constructor
	 **/
	protected EmailMessageApiReportResourceData() {}
	
	/** 
	 * @return new instance of the EmailMessageApiReportResourceData class
	 **/
	public static final EmailMessageApiReportResourceData instance(List<Long> externalIds, String mifosTenantIdentifier) {
		return new EmailMessageApiReportResourceData(externalIds, mifosTenantIdentifier);
	}

	/**
	 * @return the externalIds
	 */
	public List<Long> getExternalIds() {
		return externalIds;
	}

	/**
	 * @return the mifosTenantIdentifier
	 */
	public String getMifosTenantIdentifier() {
		return mifosTenantIdentifier;
	}
	
	/** 
	 * @return JSON representation of the object 
	 **/
	public String toJsonString() {
		Gson gson = new Gson();
		
		return gson.toJson(this);
	}
}
