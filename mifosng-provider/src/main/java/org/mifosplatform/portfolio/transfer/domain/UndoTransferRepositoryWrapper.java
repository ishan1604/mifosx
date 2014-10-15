/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.transfer.domain;

import org.mifosplatform.portfolio.transfer.exception.UndoTransferNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UndoTransferRepositoryWrapper {

    private final UndoTransferRepository undoTransferRepository;

    @Autowired
    public UndoTransferRepositoryWrapper(final UndoTransferRepository undoTransferRepository) {
        this.undoTransferRepository = undoTransferRepository;
    }


    public void save(final UndoTransfer undoTransfer){
        this.undoTransferRepository.save(undoTransfer);
    }
    public void saveAndFlush(final UndoTransfer undoTransfer) {
        this.undoTransferRepository.saveAndFlush(undoTransfer);
    }

    public boolean doesClientExistInUndoTransfer(final Long clientId){
        boolean clientExist  = false;
        final UndoTransfer undoTransfer = this.undoTransferRepository.findClientUndoTransfer(clientId);
        if(undoTransfer !=null){
            clientExist = true;
        }
        return clientExist;
    }


    public boolean doesGroupExistInUndoTransfer(final Long groupId){
        boolean groupExist = false;
        final UndoTransfer undoTransfer = this.undoTransferRepository.findGroupUndoTransfer(groupId);
        if(undoTransfer !=null){
            groupExist = true;
        }
        return groupExist;
    }


    public UndoTransfer findOneWithNotFoundDetection(final Long id){
        final UndoTransfer entity = this.undoTransferRepository.findOne(id);
        if (entity == null) { throw new UndoTransferNotFoundException(id); }
        return entity;
    }
}
