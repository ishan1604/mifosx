Alter table m_loan_transaction Add column is_account_transfer tinyint(1) default 0;

update m_loan_transaction  mlt
inner join m_account_transfer_transaction mt on (mt.to_loan_transaction_id = mlt.id
or mt.from_loan_transaction_id = mlt.id ) and mlt.is_reversed !=1
set mlt.is_account_transfer = 1;