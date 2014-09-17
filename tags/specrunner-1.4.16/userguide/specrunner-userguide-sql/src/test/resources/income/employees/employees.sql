create table employers (
    id BIGINT not null, 
    name VARCHAR(255), 
    primary key (id)
)

create table employment_periods (
    id BIGINT not null,
    hourly_rate NUMERIC(12, 2),
    currency VARCHAR(12), 
    employee_id BIGINT not null, 
    employer_id BIGINT not null, 
    end_date TIMESTAMP, 
    start_date TIMESTAMP, 
    primary key (id)
)

create table employees (
    id BIGINT not null, 
    firstName VARCHAR(255), 
    initial CHAR(1), 
    lastName VARCHAR(255), 
    taxfileNumber VARCHAR(255), 
    primary key (id)
)

alter table employment_periods 
    add constraint employment_periodsFK0 foreign key (employer_id) references employers
alter table employment_periods 
    add constraint employment_periodsFK1 foreign key (employee_id) references employees
create sequence employee_id_seq
create sequence employment_id_seq
create sequence employer_id_seq