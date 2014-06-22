package org.mifosplatform.infrastructure.sms.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.sms.data.SmsConfigurationData;

public interface SmsConfigurationReadPlatformService {
	
	Collection<SmsConfigurationData> retrieveAll();
	
	SmsConfigurationData retrieveOne(String name);
}
