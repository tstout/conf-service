create SCHEMA IF NOT EXISTS CONF;


create sequence IF NOT EXISTS CONF.ACCOUNTS_SEQ start with 1 increment by 1;
create sequence IF NOT EXISTS CONF.TAGS_SEQ start with 1 increment by 1;

create table CONF.ACCOUNTS (
    account_id int primary key not null
    ,name varchar(256) not null
    ,description varchar(1000)
    ,user_id varchar(256)
    ,pass varchar(256)
);

create table CONF.TAGS (
    tag_id int primary key not null
    ,name varchar(256)
    ,description varchar(4096)
);

create table CONF.COUNT_TAGS(
    tag_id int
    ,account_id int
    ,FOREIGN KEY (account_id) REFERENCES ACCOUNTS(account_id)
    ON DELETE CASCADE
    ,FOREIGN KEY (tag_id) REFERENCES TAGS(tag_id)
    ON DELETE CASCADE
);