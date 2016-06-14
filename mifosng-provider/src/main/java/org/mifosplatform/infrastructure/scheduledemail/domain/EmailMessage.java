/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.domain;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.scheduledemail.EmailApiConstants;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.group.domain.Group;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "email_messages_outbound")
public class EmailMessage extends AbstractPersistable<Long> {
	
	@Column(name = "external_id", nullable = true)
	private Long externalId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;

    @Column(name = "status_enum", nullable = false)
    private Integer statusType;
    
    @Column(name = "source_address", nullable = true, length = 50)
    private String sourceAddress;

    @Column(name = "email_address", nullable = false, length = 50)
    private String emailAddress;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "campaign_name", nullable = true)
    private String campaignName;

    @Column(name = "submittedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;

    public static EmailMessage pendingEmail(final Long externalId, final Group group, final Client client, final Staff staff, final String message,
                                        final String sourceAddress, final String emailAddress, final String campaignName) {
        return new EmailMessage(externalId, group, client, staff, EmailMessageStatusType.PENDING, message, sourceAddress, emailAddress,campaignName);
    }
    
    public static EmailMessage instance(final Long externalId, final Group group, final Client client, final Staff staff, final EmailMessageStatusType statusType,
                                      final String message, final String sourceAddress, final String emailAddress, final String campaignName) {
    	
    	return new EmailMessage(externalId, group, client, staff, statusType, message, sourceAddress, emailAddress, campaignName);
    }

    protected EmailMessage() {
        //
    }

    private EmailMessage(final Long externalId, final Group group, final Client client, final Staff staff, final EmailMessageStatusType statusType,
            final String message, final String sourceAddress, final String emailAddress, final String campaignName) {
        this.externalId = externalId;
    	this.group = group;
        this.client = client;
        this.staff = staff;
        this.statusType = statusType.getValue();
        this.emailAddress = emailAddress;
        this.sourceAddress = sourceAddress;
        this.message = message;
        this.campaignName = campaignName;
        this.submittedOnDate = LocalDate.now().toDate();
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        if (command.isChangeInStringParameterNamed(EmailApiConstants.messageParamName, this.message)) {
            final String newValue = command.stringValueOfParameterNamed(EmailApiConstants.messageParamName);
            actualChanges.put(EmailApiConstants.messageParamName, newValue);
            this.message = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }
    
    public Long getExternalId() {
    	return externalId;
    }
    
    public Group getGroup() {
    	return group;
    }
    
    public Client getClient() {
    	return client;
    }
    
    public Staff getStaff() {
    	return staff;
    }
    
    public Integer getStatusType() {
    	return statusType;
    }
    
    public String getSourceAddress() {
    	return sourceAddress;
    }
    
    public String getemailAddress() {
    	return emailAddress;
    }
    
    public String getMessage() {
    	return message;
    }
    
    public void setExternalId(final Long externalId) {
    	this.externalId = externalId;
    }
    
    public void setStatusType(final Integer statusType) {
    	this.statusType = statusType;
    }
    
    public void setSourceAddress(final String sourceAddress) {
    	this.sourceAddress = sourceAddress;
    }

    public String getCampaignName() {
        return this.campaignName;
    }

    public Date getSubmittedOnDate() {
        return this.submittedOnDate;
    }
}