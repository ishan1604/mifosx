package org.mifosplatform.infrastructure.survey.data;

import java.util.List;

/**
 * Created by Cieyou on 3/11/14.
 */
public class PpiPovertyLineData {

    final List<LikeliHoodPovertyLineData> likeliHoodPovertyLineData;
    final String ppi;

    PpiPovertyLineData(final List<LikeliHoodPovertyLineData> likeliHoodPovertyLineData,
                       final String ppi){

        this.likeliHoodPovertyLineData = likeliHoodPovertyLineData;
        this.ppi = ppi;

    }


}
