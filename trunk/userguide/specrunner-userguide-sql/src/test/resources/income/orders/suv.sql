insert into ORD.customers (name) values ('Thiago')
insert into ORD.orders (customer_id,date) values (2, CURRENT_DATE)
insert into ORD.line_items (line_number,order_id,product_id,quantity) values (1,2,0,500)
insert into ORD.line_items (line_number,order_id,product_id,quantity) values (2,2,1,40)
insert into ORD.line_items (line_number,order_id,product_id,quantity) values (3,2,2,10)
delete from ORD.line_items where line_number=3 and order_id=2
insert into ORD.line_items (line_number,order_id,product_id,quantity) values (3,2,3,10)
