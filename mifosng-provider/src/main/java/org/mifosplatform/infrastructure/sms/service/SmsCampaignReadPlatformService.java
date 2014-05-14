package org.mifosplatform.infrastructure.sms.service;

import org.mifosplatform.infrastructure.sms.data.PreviewCampaignMessage;
import org.mifosplatform.infrastructure.sms.data.SmsBusinessRulesData;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignData;

import java.util.Collection;

public interface SmsCampaignReadPlatformService {

    Collection<SmsBusinessRulesData> retrieveAll();

    SmsBusinessRulesData retrieveOneTemplate(Long resourceId);

    SmsCampaignData retrieveOne(Long resourceId);

    Collection<SmsCampaignData> retrieveAllCampaign();

    Collection<SmsCampaignData> retrieveAllScheduleActiveCampaign();

}
