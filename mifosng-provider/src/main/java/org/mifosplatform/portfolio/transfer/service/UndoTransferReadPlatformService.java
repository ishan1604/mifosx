/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.transfer.service;

import org.mifosplatform.portfolio.transfer.data.UndoTransferClientData;
import org.mifosplatform.portfolio.transfer.data.UndoTransferGroupData;

import java.util.Collection;

public interface UndoTransferReadPlatformService {

    UndoTransferClientData retrieveUndoTransferClientData(final Long clientId);

    UndoTransferGroupData retrieveUndoTransferGroupData(final Long groupId);

    Collection<UndoTransferClientData> retrieveAllTransferredClients();

    Collection<UndoTransferGroupData> retrieveAllTransferredGroups();
}
