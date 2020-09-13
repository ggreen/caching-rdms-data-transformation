create schema APP;

create table APP.ACCOUNT (
    ACCOUNT_ID BIGINT not null,
    ACCOUNT_NM varchar(100) not null,
    ACCOUNT_TIMESTAMP timestamp not null,
    PRIMARY KEY (ACCOUNT_ID)
);