/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.domain;
/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "x_registered_table")
public class RegisteredTable extends AbstractPersistable<Long> {

    @Column(name="registered_table_name", nullable = false,unique=true)
    private String registeredTableName;

    @Column(name = "application_table_name", nullable = false)
    private String applicationTableName;

    @Column(name = "category")
    private Integer category;

    @Column(name = "display_name")
    private String displayName;

    @Column(name="system_defined")
    private boolean systemDefined;

    public RegisteredTable() {
    }

    public Integer getCategory() {
        return this.category;
    }

    public String getApplicationTableName() {
        return this.applicationTableName;
    }

    public String getRegisteredTableName() {
        return this.registeredTableName;
    }

    public void updateCategory(final Integer category){
        this.category = category;
    }

    public boolean isSystemDefined() {return this.systemDefined;}

    public void updateDisplayName(final String displayName){

        if(!this.displayName.equals(displayName)){
            this.displayName = displayName;
        }
    }
    public String getDisplayName(){
        return this.displayName;
    }
}
