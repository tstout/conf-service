create table CONF.NAME(
    name varchar(255)
    ,account_id int
    ,FOREIGN KEY (account_id) REFERENCES ACCOUNT(account_id)
    ON DELETE CASCADE
);