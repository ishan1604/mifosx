package org.mifosplatform.infrastructure.dataexport.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface DataExportProcessRepository extends JpaRepository<DataExportProcess, Long>,
        JpaSpecificationExecutor<DataExportProcess> {
}
