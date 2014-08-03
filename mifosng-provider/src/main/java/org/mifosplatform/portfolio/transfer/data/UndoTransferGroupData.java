/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.transfer.data;

import org.joda.time.LocalDate;

public class UndoTransferGroupData {

    private final Long id;
    private final Long groupId;
    private final String groupName;
    private final boolean isTransferUndone;
    private final Long transferFromOfficeId;
    private final String transferFromOfficeName;
    private final Long transferFromGroupId;
    private final String transferFromGroupName;
    private final Long transferFromStaffId;   
    private final String transferFromStaffName;
    private final LocalDate submittedOnDate;
    private final String approvedUser;
    private final String transferToOfficeName;

    private UndoTransferGroupData(Long id, Long groupId, String groupName, boolean transferUndone, Long transferFromOfficeId,
            String transferFromOfficeName, Long transferFromGroupId, String transferFromGroupName, Long transferFromStaffId, String transferFromStaffName,
            LocalDate submittedOnDate, String approvedUser,String transferToOfficeName) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        isTransferUndone = transferUndone;
        this.transferFromOfficeId = transferFromOfficeId;
        this.transferFromOfficeName = transferFromOfficeName;
        this.transferFromGroupId = transferFromGroupId;
        this.transferFromGroupName = transferFromGroupName;
        this.transferFromStaffId = transferFromStaffId;
        this.transferFromStaffName = transferFromStaffName;
        this.submittedOnDate = submittedOnDate;
        this.approvedUser = approvedUser;
        this.transferToOfficeName = transferToOfficeName;

    }
    
    public static UndoTransferGroupData instance(final Long id,final Long groupId, final String groupName,
            boolean transferUndone,final Long transferFromOfficeId,final String transferFromOfficeName,
            final Long transferFromGroupId,final String transferFromGroupName,final Long transferFromStaffId,
            final String transferFromStaffName,final LocalDate submittedOnDate,final String approvedUser, final String transferToOfficeName){
        return new UndoTransferGroupData(id,groupId,groupName,transferUndone,transferFromOfficeId,transferFromOfficeName,transferFromGroupId,
                transferFromGroupName,transferFromStaffId,transferFromStaffName,submittedOnDate,approvedUser,transferToOfficeName);
    }
}
