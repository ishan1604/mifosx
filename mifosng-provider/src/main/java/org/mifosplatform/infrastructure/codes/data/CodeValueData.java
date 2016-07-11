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

    private final String name;

    @SuppressWarnings("unused")
    private final Integer position;

    @SuppressWarnings("unused")
    private final String description;
    private final boolean active;
    private final boolean isMandatory;

    public static CodeValueData instance(final Long id, final String name, final Integer position, 
            final boolean active) {
        String description = null;
        boolean isMandatory = false;
        
        return new CodeValueData(id, name, position, description, active, isMandatory);
    }

    public static CodeValueData instance(final Long id, final String name, final String description, 
            final boolean active) {
        Integer position = null;
        boolean isMandatory = false;
        
        return new CodeValueData(id, name, position, description, active, isMandatory);
    }

    public static CodeValueData instance(final Long id, final String name, final boolean active) {
        String description = null;
        Integer position = null;
        boolean isMandatory = false;
        
        return new CodeValueData(id, name, position, description, active, isMandatory);
    }

    public static CodeValueData instance(final Long id, final String name, final Integer position, 
            final String description, final boolean active, final boolean isMandatory) {
        return new CodeValueData(id, name, position, description, active, isMandatory);
    }

    private CodeValueData(final Long id, final String name, final Integer position, 
            final String description, final boolean active, final boolean isMandatory) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.isMandatory = isMandatory;
        this.description = description;
        this.active = active;
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
     * @return the active
     */
    public boolean isActive() {
        return active;
    }
}