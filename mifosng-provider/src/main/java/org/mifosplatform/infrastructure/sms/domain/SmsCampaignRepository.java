package org.mifosplatform.infrastructure.sms.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmsCampaignRepository extends JpaRepository<SmsCampaign, Long>, JpaSpecificationExecutor<SmsCampaign> {
}
