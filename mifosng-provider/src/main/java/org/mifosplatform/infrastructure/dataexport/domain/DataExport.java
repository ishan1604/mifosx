/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.domain;

import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;
import org.mifosplatform.organisation.office.domain.Office;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "m_data_export")
public class DataExport extends AbstractPersistable<Long> {

    @Column(name="base_entity", nullable = false)
    private DataExportBaseEntityEnum baseEntity;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "entity_status", nullable = false)
    private Integer status;

    @Column(name = "submitted_on_date", nullable = false)
    private Date submittedOnDate;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "office_id")
    private Office office;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "mobile_no")
    private String mobileNo;
}
