-- Demo users

-- {"email": "a@a.a","password": "a"}
INSERT INTO employees
VALUES ('88f523c5-872b-47bf-a9ed-3b63bc1aa800', 'a', 'a@a.a',
        '$2a$10$Q42EfN7.pCVBTk0adWfdI.VuAHRGyecl/HCley4SaWFYYUeuvsGDy');
INSERT INTO employee_roles
VALUES ('88f523c5-872b-47bf-a9ed-3b63bc1aa800', 'APP_USER'),
       ('88f523c5-872b-47bf-a9ed-3b63bc1aa800', 'ADMIN');

-- {"email": "b@b.b","password": "b"}
INSERT INTO employees
VALUES ('264485ad-646c-4e35-94bf-7082f55371fe', 'Карина', 'b@b.b',
        '$2a$10$EUbGDlUU9a1uKO7OP2OwLuBJtEDzqoMknRn2LQicFa7DsaYTbGPmi');
INSERT INTO employee_roles
VALUES ('264485ad-646c-4e35-94bf-7082f55371fe', 'APP_USER');

-- {"email": "c@c.c","password": "c"}
INSERT INTO employees
VALUES ('2bce8c11-dedc-4c0b-b0b8-a9aca982e064', 'Александр', 'c@c.c',
        '$2a$10$huiGU8fnFZLqMl1U5Bd60O1Kqf43DU6DJXREO6ptH89YVS4JXcFYK');
INSERT INTO employee_roles
VALUES ('2bce8c11-dedc-4c0b-b0b8-a9aca982e064', 'APP_USER');
