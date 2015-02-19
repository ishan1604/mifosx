/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

import java.util.*;

public enum EntityTables {

    CLIENT("m_client",
            new Integer[]{
                    StatusEnum.ACTIVATE.getCode(),
                    StatusEnum.CLOSE.getCode()},"client_id"),
    LOAN("m_loan",new Integer[]{
            StatusEnum.APPROVE.getCode(),
            StatusEnum.ACTIVATE.getCode(),
            StatusEnum.WITHDRAWN.getCode(),
            StatusEnum.REJECTED.getCode(),
            StatusEnum.WRITE_OFF.getCode()
    },"loan_id"),
    GROUP("m_group",new Integer[]{
            StatusEnum.ACTIVATE.getCode(),
            StatusEnum.CLOSE.getCode(),
    },"group_id"),
    SAVING("m_savings_account",new Integer[]{
            StatusEnum.APPROVE.getCode(),
            StatusEnum.ACTIVATE.getCode(),
            StatusEnum.WITHDRAWN.getCode(),
            StatusEnum.REJECTED.getCode(),
            StatusEnum.CLOSE.getCode(),
            StatusEnum.WRITE_OFF.getCode()
    },"savings_account_id");


    private static final Map<String, EntityTables> lookup = new HashMap<String, EntityTables>();
    static {
        for (EntityTables d : EntityTables.values())
            lookup.put(d.getName(), d);
    }


    private String name;

    private Integer[] codes;

    private String foreignKeyColumnNameOnDatatable;

    private EntityTables(String name,Integer[] codes,String foreignKeyColumnNameOnDatatable){
        this.name = name;
        this.codes = codes;
        this.foreignKeyColumnNameOnDatatable = foreignKeyColumnNameOnDatatable;
    }

    public static List<String> getEntitiesList(){


        List<String> data = new ArrayList<String>();

        for(EntityTables entity : EntityTables.values()){
            data.add(entity.name);
        }

        return data;

    }

    public static Integer[]getStatus(String name){

        return lookup.get(name).getCodes();

    }

    public Integer[] getCodes() {
        return this.codes;
    }


    public String getName() {
        return name;
    }

    public String getForeignKeyColumnNameOnDatatable(){
        return this.foreignKeyColumnNameOnDatatable;
    }

    public static String getForeignKeyColumnNameOnDatatable(String name){
        return lookup.get(name).foreignKeyColumnNameOnDatatable;
    }






}
