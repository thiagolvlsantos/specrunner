insert into ORD.customer (name) values ('Thiago');
update ORD.customer set name='Luiz' where name='Thiago';

insert into ORD.customer (name) values ('Santos');
update ORD.customer set name='Luiz' where name='Santos';

insert into ORD.logging (idRegister,	entity, attribute, previous, current, operator,	atimestamp) 
values (3,'Customer','Name','Thiago','Luiz','thiago.santos',CURRENT_TIMESTAMP);

insert into ORD.logging (idRegister,	entity, attribute, previous, current, operator,	atimestamp) 
values (4,'Customer','Name','Santos','Luiz','thiago.santos',CURRENT_TIMESTAMP);
