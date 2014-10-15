Alter table `x_registered_table`
drop primary key;

Alter table `x_registered_table`
add column id int NOT NULL AUTO_INCREMENT primary key first;

create table `x_registered_table_metadata`(
`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
`registered_table_id` int NOT NULL,
`table_name` varchar(150),
`field_name` varchar(100),
`label_name` varchar(100),
`ordering`  int,
PRIMARY KEY (`id`),
FOREIGN KEY(registered_table_id) REFERENCES x_registered_table(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;