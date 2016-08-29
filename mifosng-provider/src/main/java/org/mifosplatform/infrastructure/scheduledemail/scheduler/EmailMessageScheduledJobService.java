/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.scheduler;

/** 
 * Scheduled Job service interface for Email message
 **/
public interface EmailMessageScheduledJobService {
	
	/** 
	 * sends a batch of Email messages to the Email gateway
	 **/
	void sendMessages();
	
	/** 
	 * get delivery report from the Email gateway
	 **/
	void getDeliveryReports();
}