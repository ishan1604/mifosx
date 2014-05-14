package org.mifosplatform.infrastructure.sms.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SmsBusinessRuleNotFound extends AbstractPlatformResourceNotFoundException {

    public SmsBusinessRuleNotFound(final Long resourceId) {
        super("error.msg.sms.business.rule.not.found", "SMS business rule with identifier `" + resourceId + "` does not exist", resourceId);
    }
}
