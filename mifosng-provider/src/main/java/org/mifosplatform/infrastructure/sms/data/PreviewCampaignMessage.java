package org.mifosplatform.infrastructure.sms.data;

public class PreviewCampaignMessage {

    @SuppressWarnings("unused")
    private final String campaignMessage;

    private final Integer totalNumberOfMessages;

    public PreviewCampaignMessage(String campaignMessage, Integer totalNumberOfMessages) {
        this.campaignMessage = campaignMessage;
        this.totalNumberOfMessages = totalNumberOfMessages;
    }

    public String getCampaignMessage() {
        return campaignMessage;
    }

    public Integer getTotalNumberOfMessages() {
        return totalNumberOfMessages;
    }
}
