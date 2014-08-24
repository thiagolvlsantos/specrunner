insert into ORD.customers (name,date,description) values ('Vieira',CURRENT_TIME,'');

delete from ORD.customers where name = 'Thiago';

update ORD.customers set name = 'Vieira', description = '' where name = 'Luiz';