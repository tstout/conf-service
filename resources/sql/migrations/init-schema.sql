create SCHEMA IF NOT EXISTS CONF;


create sequence IF NOT EXISTS CONF.ACCOUNT_SEQ start with 1 increment by 1;
create sequence IF NOT EXISTS CONF.TAG_SEQ start with 1 increment by 1;

create table CONF.ACCOUNT (
    account_id int primary key not null
    ,name varchar(256) not null
    ,description varchar(1000)
    ,user_id varchar(256)
    ,pass varchar(256)
);

create table CONF.TAG (
    tag_id int primary key not null
    ,name varchar(256)
    ,description varchar(4096)
);

create table CONF.ACCOUNT_TAG(
    tag_id int
    ,account_id int
    ,FOREIGN KEY (account_id) REFERENCES ACCOUNT(account_id)
    ON DELETE CASCADE
    ,FOREIGN KEY (tag_id) REFERENCES TAG(tag_id)
    ON DELETE CASCADE
);