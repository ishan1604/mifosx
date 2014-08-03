/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.transfer.data;

import org.joda.time.LocalDate;

public class UndoTransferClientData {

    private final Long id;
    private final Long clientId;
    private final String clientName;
    private final boolean isTransferUndone;
    private final Long transferFromOfficeId;
    private final String transferFromOfficeName;
    private final Long transferFromGroupId;
    private final String transferFromGroupName;
    private final Long transferFromStaffId;
    private final String transferFromStaffName;
    private final LocalDate submittedOnDate;
    private final String approvedUser;
    private final String transferToGroupName;
    private final String transferToOfficeName;

    private UndoTransferClientData(Long id, Long clientId, String clientName, boolean transferUndone, Long transferFromOfficeId, String transferFromOfficeName,
            Long transferFromGroupId, String transferFromGroupName, Long transferFromStaffId, String transferFromStaffName,
            LocalDate submittedOnDate,String approvedUser,String transferToGroupName, String transferToOfficeName) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        isTransferUndone = transferUndone;
        this.transferFromOfficeId = transferFromOfficeId;
        this.transferFromOfficeName = transferFromOfficeName;
        this.transferFromGroupId = transferFromGroupId;
        this.transferFromGroupName = transferFromGroupName;
        this.transferFromStaffId = transferFromStaffId;
        this.transferFromStaffName = transferFromStaffName;
        this.submittedOnDate  = submittedOnDate;
        this.approvedUser = approvedUser;
        this.transferToGroupName = transferToGroupName;
        this.transferToOfficeName = transferToOfficeName;
    }

    public static UndoTransferClientData instance(final Long id,final Long clientId, final String clientName,
            boolean transferUndone,final Long transferFromOfficeId,final String transferFromOfficeName,
            final Long transferFromGroupId,final String transferFromGroupName,final Long transferFromStaffId,
            final String transferFromStaffName,final LocalDate submittedOnDate,final String approvedUser,
            final String transferToGroupName, final String transferToOfficeName){
        return new UndoTransferClientData(id,clientId,clientName,transferUndone,transferFromOfficeId,transferFromOfficeName,transferFromGroupId,
                transferFromGroupName,transferFromStaffId,transferFromStaffName,submittedOnDate,approvedUser,transferToGroupName,transferToOfficeName);
    }
}
