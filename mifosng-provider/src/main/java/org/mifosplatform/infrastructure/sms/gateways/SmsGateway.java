/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 **/
package org.mifosplatform.infrastructure.sms.gateways;

import java.util.List;

import org.jsmpp.session.SMPPSession;

/** 
 * The SMS gateway sits between Mifos and a mobile network's SMSC.
 * It connects with the mobile network SMSCs in order to send/receive messages and provide delivery reports
 **/
public interface SmsGateway {
    
    /** 
     * send batch of SMS messages to SMS gateway
     * 
     * @param smsGatewayMessages List of SmsGatewayMessage objects
     * @param session SMPPSession object
     * 
     * @return List of SmsGatewayMessage objects
     **/
    public List<SmsGatewayMessage> sendMessages(List<SmsGatewayMessage> smsGatewayMessages, SMPPSession session);
    
    /** 
     * Send SMS message to SMS gateway 
     * 
     * @param smsGatewayMessage SmsGatewayMessage object
     * @param session SMPPSession object
     * 
     * @return SmsGatewayMessage object
     **/
    public SmsGatewayMessage sendMessage(SmsGatewayMessage smsGatewayMessage, SMPPSession session);
}
