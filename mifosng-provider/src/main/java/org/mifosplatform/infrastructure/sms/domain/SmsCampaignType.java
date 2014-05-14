package org.mifosplatform.infrastructure.sms.domain;

public enum SmsCampaignType {
    DIRECT(1,"smsCampaignStatusType.direct"),
    SCHEDULE(2,"smsCampaignStatusType.schedule");

    private Integer value;
    private String code;

    private SmsCampaignType(Integer value, String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static SmsCampaignType fromInt(final Integer typeValue) {
        SmsCampaignType type = null;
        switch (typeValue) {
            case 1:
                type = DIRECT;
                break;
            case 2:
                type = SCHEDULE;
                break;
        }
        return type;
    }

    public boolean isDirect(){
        return this.value.equals(SmsCampaignType.DIRECT.getValue());
    }

    public boolean isSchedule(){
        return this.value.equals(SmsCampaignType.SCHEDULE.getValue());
    }
}
