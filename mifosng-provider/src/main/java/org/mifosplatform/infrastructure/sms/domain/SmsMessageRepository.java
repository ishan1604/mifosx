/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SmsMessageRepository extends JpaRepository<SmsMessage, Long>, JpaSpecificationExecutor<SmsMessage> {
    // no extra behaviour
	
	@Query("select count(msg) from SmsMessage msg where msg.statusType = 100")
	long countPending();
	
	@Query("from SmsMessage msg where msg.statusType = 100 order by msg.id")
    List<SmsMessage> findPending(Pageable pageable);
	
	@Query("from SmsMessage msg where msg.externalId = :externalId")
	SmsMessage findByExternalId(@Param("externalId") String externalId);
}