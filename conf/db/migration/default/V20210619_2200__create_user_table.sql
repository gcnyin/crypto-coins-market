create table "user"
(
    "id"            serial primary key  not null,
    "username"      varchar(64) unique  not null,
    "password"      varchar(64)         not null,
    "total_amount"  integer    default 0 not null,
    "frozen_amount" integer    default 0 not null,
    "created_date"  timestamp default current_timestamp
);

comment on table "user" is '用户';
comment on column "user"."id" is '用户ID';
comment on column "user"."username" is '用户名';
comment on column "user"."password" is '用户密码';
comment on column "user"."total_amount" is '账户全部金额';
comment on column "user"."frozen_amount" is '账户冻结金额';
comment on column "user"."created_date" is '创建时间';
