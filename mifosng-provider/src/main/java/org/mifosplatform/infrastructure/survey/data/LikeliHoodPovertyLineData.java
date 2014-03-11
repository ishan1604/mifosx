package org.mifosplatform.infrastructure.survey.data;

import java.util.List;

/**
 * Created by Cieyou on 3/11/14.
 */
public class LikeliHoodPovertyLineData {

     List<PovertyLineData> povertyLineData;
    final String likeliHoodName;
    final String likeliHoodCode;

    LikeliHoodPovertyLineData(final List<PovertyLineData> povertyLineData,
                              final String likeliHoodName,
                              final String likeliHoodCode){
        this.povertyLineData = povertyLineData;
        this.likeliHoodName = likeliHoodName;
        this.likeliHoodCode = likeliHoodCode;
    }

    public void addPovertyLine(PovertyLineData povertyLineData)
    {
        this.povertyLineData.add(povertyLineData);
    }
}
