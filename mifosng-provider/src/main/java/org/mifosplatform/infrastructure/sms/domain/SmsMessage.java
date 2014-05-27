/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.sms.SmsApiConstants;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.group.domain.Group;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "sms_messages_outbound")
public class SmsMessage extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;
    
    @Column(name = "external_id", nullable = true)
    private String externalId;

    @Column(name = "status_enum", nullable = false)
    private Integer statusType;

    @Column(name = "mobile_no", nullable = false, length = 50)
    private String mobileNo;

    @Column(name = "message", nullable = false)
    private String message;

    public static SmsMessage pendingSms(final Group group, final Client client, final Staff staff, final String externalId, final String message,
            final String mobileNo) {
        return new SmsMessage(group, client, staff, SmsMessageStatusType.PENDING, externalId, message, mobileNo);
    }

    protected SmsMessage() {
        //
    }

    private SmsMessage(final Group group, final Client client, final Staff staff, final SmsMessageStatusType statusType,
            final String externalId, final String message, final String mobileNo) {
        this.group = group;
        this.client = client;
        this.staff = staff;
        this.statusType = statusType.getValue();
        this.externalId = externalId;
        this.mobileNo = mobileNo;
        this.message = message;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

        if (command.isChangeInStringParameterNamed(SmsApiConstants.messageParamName, this.message)) {
            final String newValue = command.stringValueOfParameterNamed(SmsApiConstants.messageParamName);
            actualChanges.put(SmsApiConstants.messageParamName, newValue);
            this.message = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }
    
    /** 
     * @return the SMS gateway message identifier
     **/
    public String getExternalId() {
    	return this.externalId;
    }
    
    /** 
     * Set the value of external ID 
     **/
    public void setExternalId(String externalId) {
    	this.externalId = externalId;
    }
    
    /** 
     * @return the status type of the SMS message
     **/
    public Integer getStatusType() {
        return this.statusType;
    }

    /** 
     * Set the value of this.statusType
     **/
    public void setStatus(SmsMessageStatusType status) {
        this.statusType = status.getValue();
    }

    /** 
     * @return Group object if SMS message recipient is a group
     **/
    public Group getGroup() {
        return this.group;
    }

    /** 
     * @return Client object if SMS message recipient is a client
     **/
    public Client getClient() {
        return this.client;
    }

    /** 
     * @return Staff object if SMS message recipient is a staff
     **/
    public Staff getStaff() {
        return this.staff;
    }

    /** 
     * @return SMS message recipient's mobile number
     **/
    public String getMobileNo() {
        return this.mobileNo;
    }

    /** 
     * @return the SMS message text 
     **/
    public String getMessage() {
        return this.message;
    }
}