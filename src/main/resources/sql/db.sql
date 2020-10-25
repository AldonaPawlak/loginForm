
CREATE TABLE users
(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(150) NOT NULL
);

insert into users (login, password) values
('Aldona', 'ojoj');
insert into users (login, password) values
('Celina', 'zgody');
insert into users (login, password) values
('Hrabina', 'Mostowiak');