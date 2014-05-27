/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 **/
package org.mifosplatform.infrastructure.sms.gateways;

/** 
 * Immutable data object representing a sms gateway message 
 **/
public class SmsGatewayMessage {
    
    /** 
     * the internal message identifier (mifos sms message table id) 
     **/
    private Long id;
    
    /** 
     * the SMS gateway message identifier
     **/
    private String externalId;
    
    /** 
     * mobile phone number of sms message recipient, must be in international format without the leading
     * "0" or "+", example: 31612345678
     **/
    private String mobileNumber;
    
    /** 
     * the sms message text to be sent out
     **/
    private String message;
    
    /** 
     * SmsGatewayMessage constructor
     * 
     * @param id the internal mifos message identifier
     * @param externalId the sms gateway message identifier
     * @param mobileNumber the mobile number of sms message recipient
     * @param message the sms message text
     **/
    public SmsGatewayMessage(Long id, String externalId, String mobileNumber, String message) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.message = message;
        this.externalId = externalId;
    }

    /**
     * @return the id
     **/
    public Long id() {
        return id;
    }
    
    /** 
     * @return the mobileNumber 
     **/
    public String mobileNumber() {
        return mobileNumber;
    }
    
    /** 
     * @return the message 
     **/
    public String message() {
        return message;
    }
    
    /** 
     * @return the externalId 
     **/
    public String externalId() {
        return externalId;
    }
    
    @Override
    /** 
     * @return String representation of the SmsGatewayMessage class
     **/
    public String toString() {
        return "SmsGatewayDeliveryReport [id=" + id + ", externalId=" + externalId + ", mobileNumber=" + mobileNumber + ", message=" + message + "]";
    }
}
