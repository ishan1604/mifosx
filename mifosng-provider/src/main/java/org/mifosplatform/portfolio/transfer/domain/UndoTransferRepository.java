/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.transfer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UndoTransferRepository  extends JpaRepository<UndoTransfer, Long>, JpaSpecificationExecutor<UndoTransfer> {
    public static final String FIND_CLIENT_UNDO_TRANSFER = "from UndoTransfer ut where ut.client.id = :clientId and ut.id=" +
            "(select max(t.id) from UndoTransfer t where t.client.id = :clientId and t.transferUndone = 0)";

    public static final String FIND_GROUP_TO_UNDO_TRANSFER = "from UndoTransfer ut where ut.group.id = :groupId and " +
            "ut.id=(select max(t.id) from UndoTransfer t where t.group.id = :groupId and t.transferUndone =0)";

    @Query(FIND_CLIENT_UNDO_TRANSFER)
    UndoTransfer findClientUndoTransfer(@Param("clientId") Long clientId);

    @Query(FIND_GROUP_TO_UNDO_TRANSFER)
    UndoTransfer findGroupUndoTransfer(@Param("groupId") Long groupId);

    UndoTransfer findByClientId(Long clientId);

    UndoTransfer findByGroupId(Long groupId);
}
