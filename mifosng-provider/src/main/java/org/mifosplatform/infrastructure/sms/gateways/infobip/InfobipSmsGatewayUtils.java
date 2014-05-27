/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 **/
package org.mifosplatform.infrastructure.sms.gateways.infobip;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.RelativeTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.mifosplatform.infrastructure.sms.gateways.SmsGatewayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Utility functions, constants used in manipulating the connection, sending of SMS messages to the SMS gateway 
 **/
public class InfobipSmsGatewayUtils {
    
    /** 
     * A simulated SMS gateway is used in development mode
     * development mode is set to false by default 
     **/
    private static Boolean developmentMode = false;
    
    /** 
     * logger 
     **/
    private static final Logger logger = LoggerFactory.getLogger(InfobipSmsGatewayUtils.class);
    
    /** 
     * Country calling code (phone number prefix code) of tenant 
     * 
     * TODO this should be configurable, add to c_configuration
     **/
    public static final String countryCallingCode() {
    	return "31";
    }
    
    /** 
     * @return identifier of the system requesting a bind to the SMSC. 
     * 
     * TODO this should be configurable, add to c_configuration
     **/
    public static final String systemId() {
        return developmentMode ? "test" : "";
    }
    
    /** 
     * @return SMS gateway host name 
     * 
     * TODO this should be configurable, add to c_configuration
     **/
    public static final String host() {
    	return developmentMode ? "localhost" : "smpp3.infobip.com";
    }
    
    /** 
     * @return SMPP connection port number 
     * 
     * TODO this should be configurable, add to c_configuration
     **/
    public static final int port() {
    	return developmentMode ? 8056 : 8888;
    }
    
    /** 
     * @return SMPP connection password
     * 
     * TODO this should be configurable, add to c_configuration
     **/
    public static final String password() {
    	return developmentMode ? "test" : "";
    }
    
    /** 
     * @return connection bind type/mode for submitting "submit_sm" PDUs (Protocol Data Units) 
     **/
    public static final BindType transmitterBindType() {
    	return BindType.BIND_TX;
    }
    
    /** 
     * @return connection bind type/mode for submitting "submit_sm" and receiving "deliver_sm" PDUs (Protocol Data Units) 
     **/
    public static final BindType transceiverBindType() {
    	return BindType.BIND_TRX;
    }
    
    /** 
     * @return connection bind type/mode for receiving "deliver_sm" PDUs (Protocol Data Units) 
     **/
    public static final BindType receiverBindType() {
    	return BindType.BIND_RX;
    }
    
    /** 
     * @return type of system requesting the bind 
     **/
    public static final String systemType() {
        return developmentMode ? "cp" : null;
    }
    
    /** 
     * @return Type of Number for use in routing Delivery Receipts 
     **/
    public static final TypeOfNumber addrTon() {
        return TypeOfNumber.UNKNOWN;
    }
    
    /** 
     * @return Numbering Plan Identity for use in routing Delivery Receipts.  
     **/
    public static final NumberingPlanIndicator addrNpi() {
        return NumberingPlanIndicator.UNKNOWN;
    }
    
    /** 
     * @return Address range for use in routing short messages and Delivery Receipts to an ESME. 
     **/
    public static final String addressRange() {
        return null;
    }
    
    /** 
     * @return SMS Application service associated with the message 
     **/
    public static final String serviceType() {
    	return "CMT";
    }
    
    /** 
     * @return type of number (TON) to be used in the SME (Short Message Entity) originator address parameters
     **/
    public static final TypeOfNumber sourceAddrTon() {
        return TypeOfNumber.ALPHANUMERIC;
    }
    
    /** 
     * @return NPI (numeric plan indicator) to be used in the SME (Short Message Entity) originator address parameters
     **/
    public static final NumberingPlanIndicator sourceAddrNpi() {
        return NumberingPlanIndicator.UNKNOWN;
    }
    
    /** 
     * @return address of SME (Short Message Entity) which originated this message 
     * 
     * TODO should be configurable per tenant, so add a new line to the c_configuration table
     **/
    public static final String sourceAddress() {
        return "Musoni BV";
    }
    
    /** 
     * @return type of number (TON) to be used in the SME (Short Message Entity) destination address parameters 
     **/
    public static final TypeOfNumber destAddrTon() {
        return TypeOfNumber.INTERNATIONAL;
    }
    
    /** 
     * @return numeric plan indicator (NPI) to be used in the SME (Short Message Entity) destination address parameters 
     **/
    public static final NumberingPlanIndicator destAddrNpi() {
        return NumberingPlanIndicator.UNKNOWN;
    }
    
    /** 
     * @return a new instance of the ESMClass() class 
     **/
    public static final ESMClass esmClass() {
        return new ESMClass();
    }
    
    /** 
     * @return GSM Protocol ID 
     **/
    public static final byte protocolId() {
        return (byte)0;
    }
    
    /** 
     * @return priority level to be assigned to the short message 
     **/
    public static final byte priorityFlag() {
        return (byte)0;
    }
    
    /** 
     * @return date and time (relative to GMT) at which delivery of the message must be attempted 
     **/
    public static final String scheduledDeliveryTime() {
    	return null;
    }
    
    /** 
     * @return expiration time of this message specified as an absolute date and time of expiry 
     **/
    public static final String validityPeriod() {
        return null;
    }
    
    /** 
     * @return new instance of the RegisteredDelivery() class which will indicating if the message is a registered short
     *  message and thus if a Delivery Receipt is required upon the message attaining a final state
     **/
    public static final RegisteredDelivery registeredDelivery() {
        return new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE);
    }
    
    /** 
     * @return indication if submitted message should replace an existing message between the specified source and destination 
     **/
    public static final byte replaceIfPresentFlag() {
        return (byte)0;
    }
    
    /** 
     * @return GSM Data-Coding-Scheme 
     **/
    public static final DataCoding dataCoding() {
        // ignore any IDE warnings
        return new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false);
    }
    
    /** 
     * @return default short message to send, by providing an index into the table of Predefined Messages set up by the SMSC administrator.
     **/
    public static final byte smDefaultMsgId() {
        return (byte)0;
    }
    
    /** 
     * create new SMPPSession object, connect and bind SMPP session 
     * 
     * @return SMPPSession object
     **/
    public static final SMPPSession connectAndBindSession(BindType smppBindType, Boolean developmentMode) {
    	SMPPSession session = new SMPPSession();
    	InfobipSmsGatewayUtils.developmentMode = developmentMode;
    	
    	try {
            session.connectAndBind(InfobipSmsGatewayUtils.host(), InfobipSmsGatewayUtils.port(), 
                    new BindParameter(smppBindType, InfobipSmsGatewayUtils.systemId(), 
                            InfobipSmsGatewayUtils.password(), InfobipSmsGatewayUtils.systemType(), 
                            InfobipSmsGatewayUtils.addrTon(), InfobipSmsGatewayUtils.addrNpi(), 
                            InfobipSmsGatewayUtils.addressRange()));
        } catch (IOException e) {
            logger.error("Failed to connect and bind to host");
            e.printStackTrace();
        }
    	
    	return session;
    }
    
    /** 
     * Unbind and close open SMPP session
     * 
     * @param session SMPPSession object
     * @return void
     **/
    public static final void unbindAndCloseSession(SMPPSession session) {
    	session.unbindAndClose();
    }
    
    /** 
     * Send the SMS message to the SMS gateway
     * 
     * @param smsGatewayMessage SmsGatewayMessage object
     * @param session SMPPSession object
     * 
     * @return SmsGatewayMessage object
     **/
    public static SmsGatewayMessage submitShortMessage(SmsGatewayMessage smsGatewayMessage, SMPPSession session) {
        String messageId = "";
        String message = smsGatewayMessage.message();
        String mobileNumber = formatDestinationPhoneNumber(smsGatewayMessage.mobileNumber());
        
        try {
        	logger.info("Scheduled Delivery Time: " + scheduledDeliveryTime());
            messageId = session.submitShortMessage(InfobipSmsGatewayUtils.serviceType(), InfobipSmsGatewayUtils.sourceAddrTon(), 
                    InfobipSmsGatewayUtils.sourceAddrNpi(), InfobipSmsGatewayUtils.sourceAddress(), InfobipSmsGatewayUtils.destAddrTon(), 
                    InfobipSmsGatewayUtils.destAddrNpi(), mobileNumber, InfobipSmsGatewayUtils.esmClass(), InfobipSmsGatewayUtils.protocolId(), 
                    InfobipSmsGatewayUtils.priorityFlag(), InfobipSmsGatewayUtils.scheduledDeliveryTime(), InfobipSmsGatewayUtils.validityPeriod(), 
                    InfobipSmsGatewayUtils.registeredDelivery(), InfobipSmsGatewayUtils.replaceIfPresentFlag(), InfobipSmsGatewayUtils.dataCoding(), 
                    InfobipSmsGatewayUtils.smDefaultMsgId(), message.getBytes());
            
            logger.info("Message sent to " + mobileNumber +  ", SMS gateway message ID is " + messageId);
        } catch (PDUException e) {
            // Invalid PDU parameter
            logger.error("Invalid PDU parameter");
            e.printStackTrace();
        } catch (ResponseTimeoutException e) {
            // Response timeout
            logger.error("Response timeout");
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            // Invalid response
            logger.error("Receive invalid response");
            e.printStackTrace();
        } catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            logger.error("Receive negative response");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("IO error occur");
            e.printStackTrace();
        }
        
        return new SmsGatewayMessage(smsGatewayMessage.id(), messageId, mobileNumber, message);
    }
    
    /** 
     * Format destination phone number so it is in international format without the leading
     * "0" or "+", example: 31612345678 
     * 
     * @param phoneNumber the phone number to be formated
     **/
    public static String formatDestinationPhoneNumber(String phoneNumber) {
    	String formatedPhoneNumber = "";
    	
    	try {
    		Long phoneNumberToLong = Long.parseLong(phoneNumber);
    		Long countryCallingCodeToLong = Long.parseLong(countryCallingCode());
    		formatedPhoneNumber = Long.toString(countryCallingCodeToLong) + Long.toString(phoneNumberToLong);
    	}
    	
    	catch(Exception e) {
    		logger.error("Invalid phone number or country calling code, must contain only numbers");
    		e.printStackTrace();
    	}
    	
    	return formatedPhoneNumber;
    }
}
