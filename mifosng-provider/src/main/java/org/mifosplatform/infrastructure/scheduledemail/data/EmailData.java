/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.data;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.dataqueries.data.ReportData;

import java.util.List;

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
	private final String emailSubject;
    private final String emailMessage;
	private final EnumOptionData emailAttachmentFileFormat;
	private final ReportData stretchyReport;
	private final String stretchyReportParamMap;
	private final List<EnumOptionData> emailAttachmentFileFormatOptions;
	private final List<EnumOptionData> stretchyReportParamDateOptions;
    private final String campaignName;
	private final LocalDate sentDate;


	public static EmailData instance(final Long id, Long externalId, final Long groupId, final Long clientId, final Long staffId, final EnumOptionData status,
								   final String sourceAddress, final String emailAddress, final String emailSubject,
									 final String message, final EnumOptionData emailAttachmentFileFormat, final ReportData stretchyReport,
									 final String stretchyReportParamMap, final List<EnumOptionData> emailAttachmentFileFormatOptions,
									 final List<EnumOptionData> stretchyReportParamDateOptions, final String campaignName, final LocalDate sentDate) {
        return new EmailData(id, externalId, groupId, clientId, staffId, status, sourceAddress, emailAddress, emailSubject, message,
				emailAttachmentFileFormat,stretchyReport,stretchyReportParamMap,emailAttachmentFileFormatOptions,
				stretchyReportParamDateOptions,campaignName,sentDate);
    }

    private EmailData(final Long id, Long externalId, final Long groupId, final Long clientId, final Long staffId, final EnumOptionData status,
            final String sourceAddress, final String emailAddress, final String emailSubject, final String message,
					  final EnumOptionData emailAttachmentFileFormat, final ReportData stretchyReport, final String stretchyReportParamMap,
					  final List<EnumOptionData> emailAttachmentFileFormatOptions, final List<EnumOptionData> stretchyReportParamDateOptions,
					  final String campaignName,final LocalDate sentDate) {
        this.id = id;
        this.externalId = externalId;
        this.groupId = groupId;
        this.clientId = clientId;
        this.staffId = staffId;
        this.status = status;
        this.sourceAddress = sourceAddress;
        this.emailAddress = emailAddress;
		this.emailSubject = emailSubject;
        this.emailMessage = message;
		this.emailAttachmentFileFormat = emailAttachmentFileFormat;
		this.stretchyReport = stretchyReport;
		this.stretchyReportParamMap = stretchyReportParamMap;
		this.emailAttachmentFileFormatOptions = emailAttachmentFileFormatOptions;
		this.stretchyReportParamDateOptions = stretchyReportParamDateOptions;
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
		return emailMessage;
	}

	public String getCampaignName() {return this.campaignName;}

	public LocalDate getSentDate() { return this.sentDate; }

	public String getEmailSubject() {
		return emailSubject;
	}

	public EnumOptionData getEmailAttachmentFileFormat() {
		return emailAttachmentFileFormat;
	}

	public ReportData getStretchyReport() {
		return stretchyReport;
	}

	public String getStretchyReportParamMap() {
		return stretchyReportParamMap;
	}

	public List<EnumOptionData> getEmailAttachmentFileFormatOptions() {
		return emailAttachmentFileFormatOptions;
	}

	public List<EnumOptionData> getStretchyReportParamDateOptions() {
		return stretchyReportParamDateOptions;
	}
}