insert into ORD.customer (name) values ('Thiago');
insert into ORD.customer (name) values ('Santos');

insert into ORD.logging (idRegister, entity, attribute, previous, current, operator, atimestamp) 
values (3,'Customer','Status','Enabled','Disabled','thiago.santos',CURRENT_TIMESTAMP);

insert into ORD.logging (idRegister, entity, attribute, previous, current, operator, atimestamp) 
values (4,'Customer','Status','Enabled','Disabled','thiago.santos',CURRENT_TIMESTAMP);
