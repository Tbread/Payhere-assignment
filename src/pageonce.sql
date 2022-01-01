create table pageonce
(
    id          bigint auto_increment
        primary key,
    created_at  datetime(6)  null,
    modified_at datetime(6)  null,
    deleted     bit          not null,
    expenditure bigint       not null,
    memo        varchar(255) null,
    user_id     bigint       not null
);

create table user
(
    id          bigint auto_increment
        primary key,
    created_at  datetime(6)  null,
    modified_at datetime(6)  null,
    email       varchar(255) not null,
    password    varchar(255) not null
);

