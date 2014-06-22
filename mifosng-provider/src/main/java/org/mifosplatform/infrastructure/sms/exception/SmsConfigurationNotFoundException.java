package org.mifosplatform.infrastructure.sms.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class SmsConfigurationNotFoundException extends AbstractPlatformResourceNotFoundException {

	public SmsConfigurationNotFoundException(final String name) {
		super("error.msg.sms.configuration.name.not.found", "SMS configuration with name " + name + " does not exist", name);
	}
}
