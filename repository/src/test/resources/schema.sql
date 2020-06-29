create table certificate
(
    id                serial    not null
        constraint certificate_pk
            primary key,
    name              varchar   not null,
    description       varchar   not null,
    price             numeric   not null,
    creation_date     timestamp not null,
    modification_date timestamp,
    duration          smallint  not null
);

create unique index certificate_id_uindex
    on certificate (id);

create table tag
(
    id   serial  not null
        constraint tag_pk
            primary key,
    name varchar not null
);

create unique index tag_name_uindex
    on tag (name);

create table certificate_tag
(
    certificate_id integer not null
        constraint certificate_tag_certificate_id_fk
            references certificate,
    tag_id         integer not null
        constraint certificate_tag_tag_id_fk
            references tag,
    constraint certificate_tag_pk
        primary key (certificate_id, tag_id)
);
