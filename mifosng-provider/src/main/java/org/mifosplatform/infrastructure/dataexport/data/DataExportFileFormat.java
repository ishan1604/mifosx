package org.mifosplatform.infrastructure.dataexport.data;


public enum DataExportFileFormat {
    CSV("csv"),
    XLS("xls"),
    XML("xml");

    private String format;

    DataExportFileFormat(String format) {
        this.format = format;
    }

    public String getFormat(){
        return this.format;
    }
}
