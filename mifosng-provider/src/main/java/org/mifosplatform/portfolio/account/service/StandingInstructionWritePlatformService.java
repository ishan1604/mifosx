/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.account.service;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.jobs.exception.JobExecutionException;

public interface StandingInstructionWritePlatformService {

    CommandProcessingResult create(JsonCommand command);

    CommandProcessingResult update(Long id, JsonCommand command);

    void executeStandingInstructions() throws JobExecutionException;

    CommandProcessingResult delete(Long id);

    String executeStandingInstructions(LocalDate transactionDate);

    CommandProcessingResult executeStandingInstructions(JsonCommand jsonCommand);
}
