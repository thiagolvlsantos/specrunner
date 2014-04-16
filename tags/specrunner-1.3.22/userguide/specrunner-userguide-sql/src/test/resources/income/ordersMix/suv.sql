insert into ORD.customers (name) values ('Thiago')

insert into ORD.orders (id,customer_id,date) values (NEXT VALUE FOR ORD.orders_id_seq, 3, CURRENT_DATE)

insert into ORD.line_items (line_number,order_id,product_id,quantity) values (1,3,0,500)

insert into ORD.line_items (line_number,order_id,product_id,quantity) values (2,3,1,40)

insert into ORD.line_items (line_number,order_id,product_id,quantity) values (3,3,2,10)

delete from ORD.line_items where line_number=3 and order_id=3

insert into ORD.line_items (line_number,order_id,product_id,quantity) values (3,3,3,10)

delete from ORD.customers where name = 'Ringo Star'