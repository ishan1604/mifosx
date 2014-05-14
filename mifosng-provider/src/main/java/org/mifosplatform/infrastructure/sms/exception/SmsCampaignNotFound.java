package org.mifosplatform.infrastructure.sms.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SmsCampaignNotFound extends AbstractPlatformResourceNotFoundException{

    public SmsCampaignNotFound(final Long resourceId) {
        super("error.msg.sms.campaign.identifier.not.found", "SMS_CAMPAIGN with identifier `" + resourceId + "` does not exist", resourceId);
    }
}
