--drop table DUAL
CREATE TABLE DUAL ( SEQ INT ) 
insert into DUAL values (1)
create schema ORD authorization dba
create table ORD.customers (id BIGINT not null, name VARCHAR(255), primary key (id))
create table ORD.orders (id BIGINT not null, customer_id BIGINT, date TIMESTAMP, primary key (id))
create table ORD.line_items (line_number INTEGER not null, order_id BIGINT not null, product_id BIGINT, quantity INTEGER, primary key (order_id, line_number))
create table ORD.products (id BIGINT not null, serialNumber VARCHAR(255), primary key (id))
create sequence ORD.customers_id_seq 
create sequence ORD.orders_id_seq 
create sequence ORD.products_id_seq 
alter table ORD.orders add constraint ordersFK0 foreign key (customer_id) references customers
alter table ORD.line_items add constraint line_itemsFK0 foreign key (product_id) references products
alter table ORD.line_items add constraint line_itemsFK1 foreign key (order_id) references orders