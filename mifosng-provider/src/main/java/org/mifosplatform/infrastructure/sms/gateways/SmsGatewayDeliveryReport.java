/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 **/
package org.mifosplatform.infrastructure.sms.gateways;

import java.util.Date;

import org.mifosplatform.infrastructure.sms.domain.SmsMessageStatusType;

/** 
 * Immutable data object representing a sms gateway message delivery report 
 **/
public class SmsGatewayDeliveryReport {
    
    /** 
     * the sms gateway message identifier  
     **/
    private String externalId;
    
    /** 
     * Date/time when message was submitted from mifos to the sms gateway system. (format: yyyy/mm/dd hh:mm:ss) 
     **/
    private Date sentDate;
    
    /** 
     * Date/time when SMSC notified the sms gateway system of the delivery report (format: yyyy/mm/dd hh:mm:ss) 
     **/
    private Date doneDate;
    
    /** 
     * the status of the message delivery 
     **/
    private SmsMessageStatusType status;
    
    /** 
     * SmsGatewayDeliveryReport constructor
     * 
     **/
    public SmsGatewayDeliveryReport(String externalId, Date sentDate, Date doneDate, SmsMessageStatusType status) {
        this.externalId = externalId;
        this.sentDate = sentDate;
        this.doneDate = doneDate;
        this.status = status;
    }
    
    /**
     * @return the external id
     **/
    public String externalId() {
        return externalId;
    }
    
    /**
     * @return the sent date
     **/
    public Date sentDate() {
        return sentDate;
    }
    
    /**
     * @return the sent date
     **/
    public Date doneDate() {
        return doneDate;
    }
    
    /**
     * @return the status
     **/
    public SmsMessageStatusType status() {
        return status;
    }
    
    @Override
    /** 
     * @return String representation of the SmsGatewayDeliveryReport class
     **/
    public String toString() {
        return "SmsGatewayDeliveryReport [externalId=" + externalId + ", sentDate=" + sentDate + ", doneDate=" + doneDate + ", status=" + status + "]";
    }
}
