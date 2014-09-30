drop schema ORD cascade;
create schema ord authorization dba; 

CREATE TABLE "ORD"."UNCHANGED"
(
   ID bigint PRIMARY KEY NOT NULL,
   OK varchar(255)
)

CREATE TABLE "ORD"."BEFORE"
(
   ID bigint PRIMARY KEY NOT NULL,
   PREVIOUS varchar(255) DEFAULT 'Remove me'
)

CREATE TABLE "ORD"."ADDRESS"
(
   ID bigint PRIMARY KEY NOT NULL,
   CUSTOMER_ID integer NOT NULL,
   DATE timestamp,
   ADDRESS varchar(255)  DEFAULT 'Not informed' not null 
)
;
CREATE TABLE "ORD"."CUSTOMERS"
(
   ID bigint ,
   NAME varchar(255),
   DESCRIPTION varchar(255),
   BIRTHDAY date DEFAULT CURRENT_DATE,
   RATING numeric,
   NUMBER smallint,
   TEXT longvarchar
)
;
CREATE INDEX SYS_IDX_10102 ON "ORD"."ADDRESS"(CUSTOMER_ID)
;
CREATE UNIQUE INDEX SYS_IDX_SYS_PK_10096_10099 ON "ORD"."ADDRESS"(ID)
;
CREATE UNIQUE INDEX SYS_IDX_SYS_PK_10092_10093 ON "ORD"."CUSTOMERS"(ID)
;
