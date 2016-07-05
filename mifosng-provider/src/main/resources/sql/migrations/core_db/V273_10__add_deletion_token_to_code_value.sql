alter table m_code_value
add deletion_token varchar(100);

update m_code_value
set deletion_token = sha2(concat(id, '_', code_id), 224)
where is_deleted = 1;

alter table m_code_value
drop index code_value;

update m_code_value
set code_value = replace( code_value, concat( '_deleted_', id ) , '' )
where is_deleted = 1;

alter table m_code_value
add constraint unique_code_value unique (code_value, code_id, deletion_token); 