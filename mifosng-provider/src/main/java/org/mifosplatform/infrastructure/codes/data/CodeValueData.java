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

    public static CodeValueData instance(final Long id, final String name, final Integer position, 
            final boolean isMandatory) {
        return new CodeValueData(id, name, position, isMandatory);
    }

    public static CodeValueData instance(final Long id, final String name) {
        return new CodeValueData(id, name, null, false);
    }

    private CodeValueData(final Long id, final String name, final Integer position, 
            final boolean isMandatory) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.isMandatory = isMandatory;
    }

    public Long getId() {
        return this.id;
    }

    /**
     * @return the isMandatory
     */
    public boolean isMandatory() {
        return isMandatory;
    }
}