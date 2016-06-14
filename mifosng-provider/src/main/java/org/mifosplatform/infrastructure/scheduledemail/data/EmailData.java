/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.data;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

/**
 * Immutable data object representing a SMS message.
 */
public class EmailData {

    private final Long id;
    private final Long externalId;
    private final Long groupId;
    private final Long clientId;
    private final Long staffId;
    private final EnumOptionData status;
    private final String sourceAddress;
    private final String emailAddress;
    private final String message;
    private final String campaignName;
	private final LocalDate sentDate;


	public static EmailData instance(final Long id, Long externalId, final Long groupId, final Long clientId, final Long staffId, final EnumOptionData status,
								   final String sourceAddress, final String emailAddress, final String message, final String campaignName, final LocalDate sentDate) {
        return new EmailData(id, externalId, groupId, clientId, staffId, status, sourceAddress, emailAddress, message, campaignName,sentDate);
    }

    private EmailData(final Long id, Long externalId, final Long groupId, final Long clientId, final Long staffId, final EnumOptionData status,
            final String sourceAddress, final String emailAddress, final String message, final String campaignName,final LocalDate sentDate) {
        this.id = id;
        this.externalId = externalId;
        this.groupId = groupId;
        this.clientId = clientId;
        this.staffId = staffId;
        this.status = status;
        this.sourceAddress = sourceAddress;
        this.emailAddress = emailAddress;
        this.message = message;
        this.campaignName = campaignName;
		this.sentDate = sentDate;
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
	 * @return the groupId
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * @return the clientId
	 */
	public Long getClientId() {
		return clientId;
	}

	/**
	 * @return the staffId
	 */
	public Long getStaffId() {
		return staffId;
	}

	/**
	 * @return the status
	 */
	public EnumOptionData getStatus() {
		return status;
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
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	public String getCampaignName() {return this.campaignName;}

	public LocalDate getSentDate() { return this.sentDate; }
}