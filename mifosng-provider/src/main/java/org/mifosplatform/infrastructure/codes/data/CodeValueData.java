/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.codes.data;

import java.io.Serializable;

/**
 * Immutable data object represent code-value data in system.
 */
public class CodeValueData implements Serializable {

    private final Long id;

    @SuppressWarnings("unused")
    private final String name;

    @SuppressWarnings("unused")
    private final Integer position;
    
    private final boolean isMandatory;

    @SuppressWarnings("unused")
    private final String description;
    
    private final boolean deleted;

    public static CodeValueData instance(final Long id, final String name, final Integer position, 
            final boolean deleted) {
        String description = null;
        boolean isMandatory = false;
        return new CodeValueData(id, name, position, isMandatory, description, deleted);
    }

    public static CodeValueData instance(final Long id, final String name, final String description, 
            final boolean deleted) {
        Integer position = null;
        boolean isMandatory = false;
        return new CodeValueData(id, name, position, isMandatory, description, deleted);
    }

    public static CodeValueData instance(final Long id, final String name, final boolean deleted) {
        String description = null;
        Integer position = null;
        boolean isMandatory = false;
        return new CodeValueData(id, name, position, isMandatory, description, deleted);
    }

    public static CodeValueData instance(final Long id, final String name, final Integer position, 
            final boolean isMandatory, final String description, final boolean deleted) {
        return new CodeValueData(id, name, position, isMandatory, description, deleted);
    }

    private CodeValueData(final Long id, final String name, final Integer position, final boolean isMandatory, 
            final String description, final boolean deleted) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.isMandatory = isMandatory;
        this.description = description;
        this.deleted = deleted;
    }

    public Long getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }

    /**
     * @return the isMandatory
     */
    public boolean isMandatory() {
        return isMandatory;
    }
    
    /**
     * @return value of the deleted property
     */
    public boolean isDeleted() {
        return this.deleted;
    }
    
    /**
     * @return true if the "deleted" property is false
     */
    public boolean isActive() {
        return !this.deleted;
    }
}