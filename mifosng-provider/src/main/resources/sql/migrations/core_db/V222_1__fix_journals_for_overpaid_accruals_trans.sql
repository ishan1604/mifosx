create table temp_overpaid_transactions (
    loan_id int,
      journal_id int,
        transaction_id int,
          payment_details_id int,
            journal_amount decimal(29,6),
              paid_amount decimal (29,6),
                overpaid_amount decimal (29,6),
                  overpaid_gl int,
                    created_datetime datetime,
                      transaction_date date,
                        office_id int,
                          currency_code nvarchar(3)
                        );

                        insert into temp_overpaid_transactions (loan_id, journal_id, transaction_id, payment_details_id, journal_amount,paid_amount, overpaid_amount, overpaid_gl, created_datetime, transaction_date, office_id, currency_code)
                        (
                          select mlt.loan_id, je.id as journal_id, mlt.id as transaction_id, mlt.`payment_detail_id`, je.amount, mlt.amount, mlt.overpayment_portion_derived, apm.gl_account_id, je.`created_date`, mlt.`transaction_date`, je.office_id, je.`currency_code`
                          from m_loan_transaction as mlt
                          left join m_loan as ml on mlt.loan_id = ml.id
                          left join m_product_loan as mpl on ml.product_id = mpl.id
                          left join acc_product_mapping as apm on mpl.id = apm.`product_id` and apm.`product_type` = 1 and apm.financial_account_type = 11 and payment_type is null
                          left join acc_gl_journal_entry as je on mlt.id = je.loan_transaction_id and type_enum = 2
                          where overpayment_portion_derived is not null and overpayment_portion_derived > 0 and is_reversed = 0 and mpl.accounting_type IN (3,4)
                          and mlt.overpayment_portion_derived <> mlt.amount and je.amount <> mlt.amount
                        );


                        UPDATE acc_gl_journal_entry as je
                        JOIN temp_overpaid_transactions as op on op.journal_id = je.id
                        SET je.amount = op.paid_amount
                        where amount != op.paid_amount;

                        INSERT INTO `acc_gl_journal_entry` ( `account_id`, `office_id`, `reversal_id`, `currency_code`, `transaction_id`, `loan_transaction_id`, `savings_transaction_id`, `reversed`, `ref_num`, `manual_entry`, `entry_date`, `type_enum`, `amount`, `description`, `entity_type_enum`, `entity_id`, `createdby_id`, `lastmodifiedby_id`, `created_date`, `lastmodified_date`, `is_running_balance_calculated`, `office_running_balance`, `organization_running_balance`, `payment_details_id`)
                        (
                            select overpaid_gl, office_id, NULL, currency_code, concat('L',transaction_id), transaction_id, NULL, 0, NULL, 0, transaction_date, 1, overpaid_amount, NULL, 1, loan_id, 1, 1, created_datetime, created_datetime, 0, 0, 0, payment_details_id
                              FROM temp_overpaid_transactions
                            );

                            select * from temp_overpaid_transactions;


                            drop table temp_overpaid_transactions;



