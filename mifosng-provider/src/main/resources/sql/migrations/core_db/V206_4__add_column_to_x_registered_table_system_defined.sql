Alter table x_registered_table_metadata
ADD Column system_defined tinyint(1) default 0;

Alter table x_registered_table 
ADD Column system_defined tinyint(1) default 0;

