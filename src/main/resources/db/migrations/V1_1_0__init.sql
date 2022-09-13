create table if not exists dog
(
    id          varchar(36),
    name        varchar(50),
    dateOfBirth date,
    height      numeric,
    weight      numeric,
    code        varchar(255)
);