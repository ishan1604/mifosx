/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.reportmailingjob.data.ReportMailingJobEnumerations;
import org.mifosplatform.infrastructure.reportmailingjob.domain.ReportMailingJobEmailAttachmentFileFormat;
import org.springframework.stereotype.Service;

@Service
public class ReportMailingJobDropdownReadPlatformServiceImpl implements ReportMailingJobDropdownReadPlatformService {

    @Override
    public List<EnumOptionData> retrieveEmailAttachmentFileFormatOptions() {
        final List<EnumOptionData> emailAttachementFileFormatOptions = new ArrayList<>();
        
        for (ReportMailingJobEmailAttachmentFileFormat reportMailingJobEmailAttachmentFileFormat : ReportMailingJobEmailAttachmentFileFormat.values()) {
            if (ReportMailingJobEmailAttachmentFileFormat.INVALID.equals(reportMailingJobEmailAttachmentFileFormat)) {
                continue;
            }
            
            emailAttachementFileFormatOptions.add(ReportMailingJobEnumerations.emailAttachementFileFormat(reportMailingJobEmailAttachmentFileFormat));
        }
        
        return emailAttachementFileFormatOptions;
    }
}
