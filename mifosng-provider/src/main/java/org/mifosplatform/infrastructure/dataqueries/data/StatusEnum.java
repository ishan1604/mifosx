/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

import java.util.ArrayList;
import java.util.List;

public enum StatusEnum {

    CREATE("create",100),
    APPROVE("approve",200),
    ACTIVATE("activate",300),
    WITHDRAWN("withdraw",400),
    REJECTED("reject",500),
    CLOSE("close",600),
    WRITE_OFF("write off",601),
    RESCHEDULE("reschedule",602),
    OVERPAY("overpay",700);

    private String name;

    public Integer getCode() {
        return code;
    }

    private Integer code;

    private StatusEnum(String name, Integer code){

        this.name = name;
        this.code = code;

    }

    public static List<DatatableCheckStatusData> getStatusList(){

        List<DatatableCheckStatusData> data = new ArrayList<DatatableCheckStatusData>();

        for(StatusEnum status : StatusEnum.values()){
            data.add(new DatatableCheckStatusData(status.name,status.code));
        }

        return data;

    }

}
