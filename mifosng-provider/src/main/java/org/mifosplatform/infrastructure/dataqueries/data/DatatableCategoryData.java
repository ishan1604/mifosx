package org.mifosplatform.infrastructure.dataqueries.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cieyou on 2/19/2015.
 */
public class DatatableCategoryData {

    private final Long id;
    private final String category;
    private final List<String> datatables;

    private final static Logger logger = LoggerFactory.getLogger(DatatableData.class);

    public DatatableCategoryData(final Long id, final String name, final List<String> tables){

        this.id = id;
        this.category = name;
        this.datatables = tables;
    }

    public static DatatableCategoryData datatableCategoryData(final Long id, final String name){

        return new DatatableCategoryData(id,name,new ArrayList<String>());
    }

    public void addTable(String table){
        datatables.add(table);
    }

    public Long getId(){return this.id;}
}
