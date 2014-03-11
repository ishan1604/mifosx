package org.mifosplatform.infrastructure.survey.data;

/**
 * Created by Cieyou on 3/11/14.
 */
public class PovertyLineData {

    final Long resourceId;
    final Long scoreFrom;
    final Long scoreTo;
    final Long povertyLine;

    public PovertyLineData(final Long resourceId,
            final Long scoreFrom,
            final Long scoreTo,
            final Long povertyLine){

        this.resourceId = resourceId;
        this.scoreTo = scoreTo;
        this.scoreFrom = scoreFrom;
        this.povertyLine = povertyLine;
    }
}
