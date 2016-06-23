alter table m_payment_type
add deletion_token varchar(100) not null default 'NA';

update m_payment_type
set deletion_token = sha2(concat(id, '_', `value`), 224)
where is_deleted = 1;

update m_payment_type
set `value` = replace(`value`, concat( '_deleted_', id ) , '' )
where is_deleted = 1;

alter table m_payment_type
add constraint unique_payment_type unique (`value`, deletion_token); 