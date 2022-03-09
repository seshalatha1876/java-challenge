
CREATE TABLE if not exists employee
(
   ID integer GENERATED ALWAYS AS IDENTITY not null,
   EMPLOYEE_NAME varchar(255),
   EMPLOYEE_SALARY integer,
   DEPARTMENT varchar(255),
   primary key(ID)
);