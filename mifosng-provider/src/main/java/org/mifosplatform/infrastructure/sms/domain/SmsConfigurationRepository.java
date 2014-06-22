package org.mifosplatform.infrastructure.sms.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmsConfigurationRepository extends JpaRepository<SmsConfiguration, Long>, JpaSpecificationExecutor<SmsConfiguration> {
	SmsConfiguration findByName(String name);
}
