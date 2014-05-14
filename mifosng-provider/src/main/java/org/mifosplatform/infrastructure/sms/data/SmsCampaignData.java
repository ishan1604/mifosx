package org.mifosplatform.infrastructure.sms.data;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

import java.util.Map;

public class SmsCampaignData {

    @SuppressWarnings("unused")
    private Long id;
    @SuppressWarnings("unused")
    private final String campaignName;
    @SuppressWarnings("unused")
    private final Integer campaignType;
    @SuppressWarnings("unused")
    private final Long runReportId;

    @SuppressWarnings("unused")
    private final String paramValue;
    @SuppressWarnings("unused")
    private final EnumOptionData campaignStatus;
    @SuppressWarnings("unused")
    private final String message;
    @SuppressWarnings("unused")
    private final DateTime nextTriggerDate;

    private final LocalDate lastTriggerDate;

    private final SmsCampaignTimeLine smsCampaignTimeLine;

    private SmsCampaignData(final Long id,final String campaignName, final Integer campaignType, final Long runReportId,
                           final String paramValue,final EnumOptionData campaignStatus,
                           final String message,final DateTime nextTriggerDate,final LocalDate lastTriggerDate,final SmsCampaignTimeLine smsCampaignTimeLine) {
        this.id = id;
        this.campaignName = campaignName;
        this.campaignType = campaignType;
        this.runReportId = runReportId;
        this.paramValue = paramValue;
        this.campaignStatus =campaignStatus;
        this.message = message;
        if(nextTriggerDate !=null){
            this.nextTriggerDate = nextTriggerDate;
        }else{
            this.nextTriggerDate = null;
        }
        if(lastTriggerDate !=null){
            this.lastTriggerDate = lastTriggerDate;
        }else{
            this.lastTriggerDate = null;
        }
        this.smsCampaignTimeLine =smsCampaignTimeLine;
    }

    public static SmsCampaignData instance(final Long id,final String campaignName, final Integer campaignType, final Long runReportId,
                                           final String paramValue,final EnumOptionData campaignStatus,final String message,
                                           final DateTime nextTriggerDate, final LocalDate lastTriggerDate,final SmsCampaignTimeLine smsCampaignTimeLine){
        return new SmsCampaignData(id,campaignName,campaignType,runReportId,paramValue,
                campaignStatus,message,nextTriggerDate,lastTriggerDate,smsCampaignTimeLine);
    }


    public Long getId() {
        return id;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public Integer getCampaignType() {
        return campaignType;
    }

    public Long getRunReportId() {
        return runReportId;
    }

    public String getParamValue() {
        return paramValue;
    }

    public EnumOptionData getCampaignStatus() {
        return campaignStatus;
    }

    public String getMessage() {
        return message;
    }


    public DateTime getNextTriggerDate() {
        return nextTriggerDate;
    }

    public LocalDate getLastTriggerDate() {
        return lastTriggerDate;
    }

}
