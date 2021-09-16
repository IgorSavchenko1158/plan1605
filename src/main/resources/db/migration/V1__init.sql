create table user_statuses
(
    id          int  not null primary key,
    description text not null
);

insert into user_statuses (id, description)
values (0, 'ACTIVE');
insert into user_statuses (id, description)
values (1, 'SUSPENDED');

create table users
(
    id         bigserial primary key,
    email      text        not null,
    password   text        not null,
    status     int         not null references user_statuses (id),
    created_at timestamptz not null default now()
);

create unique index users_email_uindex on users (email);

create table authorities
(
    id    int primary key,
    value text not null
);

create unique index authorities_value_uindex on authorities (value);

create table user_authorities
(
    user_id      bigint not null,
    authority_id int    not null,
    primary key (user_id, authority_id),
    constraint user_authorities_users_fk foreign key (user_id)
        references users (id) on delete cascade,
    constraint user_authorities_authorities_fk foreign key (authority_id)
        references authorities (id) on delete cascade
);

insert into authorities (id, value)
values (0, 'ROLE_USER');
insert into authorities (id, value)
values (1, 'ROLE_ADMIN');

create table refresh_tokens
(
    value     uuid        not null primary key,
    user_id   bigint      not null,
    issued_at timestamptz not null,
    expire_at timestamptz not null,
    next      uuid,
    constraint refresh_tokens_user_fk foreign key (user_id)
        references users (id) on delete cascade,
    constraint refresh_tokens_next_fk foreign key (next)
        references refresh_tokens (value) on delete cascade
);

create table events
(
    id          bigserial   not null primary key,
    user_id     bigint   not null,
    name        text        not null,
    starts_at   timestamptz not null,
    description text,
    ends_at     timestamptz,
    notify      boolean,
    recurrence  int,

    constraint events_user_fk foreign key (user_id)
        references users (id) on delete cascade
);