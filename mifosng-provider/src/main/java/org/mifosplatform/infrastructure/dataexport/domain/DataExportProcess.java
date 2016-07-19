/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.domain;

import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "m_data_export_process")
public class DataExportProcess extends AbstractPersistable<Long> {

    @Column(name = "data_export_id",nullable = false)
    private DataExport dataExport;

    @Column(name = "process_status", nullable = false)
    private Integer status;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "started_by_user_id", nullable = false)
    private AppUser startedByUser;

    @Column(name = "started_date", nullable = false)
    private Date startDate;

    @Column(name = "ended_date")
    private Date endDate;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "file_download_count",nullable = false)
    private Integer fileDownloadCount;
}
