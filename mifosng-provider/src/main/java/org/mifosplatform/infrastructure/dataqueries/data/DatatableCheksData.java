package org.mifosplatform.infrastructure.dataqueries.data;

import java.io.Serializable;

/**
 * Created by Cieyou on 2/10/2015.
 */
public class DatatableCheksData implements Serializable {

    private final long id;
    private final String dataTableName;

    public DatatableCheksData(final long id, final String dataTableName){
        this.id = id;
        this.dataTableName = dataTableName;

    }

}
