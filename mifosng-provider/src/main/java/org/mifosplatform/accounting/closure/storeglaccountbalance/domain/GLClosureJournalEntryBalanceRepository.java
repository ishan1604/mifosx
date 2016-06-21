/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.storeglaccountbalance.domain;

import java.util.Collection;

import org.mifosplatform.accounting.closure.domain.GLClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GLClosureJournalEntryBalanceRepository extends JpaRepository<GLClosureJournalEntryBalance, Long>, 
        JpaSpecificationExecutor<GLClosureJournalEntryBalance> { 
    /**
     * Retrieve all {@link GLClosureJournalEntryBalance} entities that have not been deleted with 
     * similar {@link GLClosure} property to the one passed
     * 
     * @param glClosure {@link GLClosure} object
     * @return collection of {@link GLClosureJournalEntryBalance} entities
     */
    Collection<GLClosureJournalEntryBalance> findByGlClosureAndDeletedIsFalse(GLClosure glClosure);
}
