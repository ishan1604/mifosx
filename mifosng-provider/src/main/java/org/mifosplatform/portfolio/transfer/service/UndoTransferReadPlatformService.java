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
