insert into CTI.CIT_CITY values (2,now(),now(),'Segunda',1)
insert into CTI.CIT_CITY values (3,${time},{ts '${dt.toString(pattern)}'},'${"Segunda "+" feira"}',${Math.pow(2,10)/1024})
insert into CTI.ADD_ADDRESS values (1,'Rua da primeira cidade',1)
insert into CTI.ADD_ADDRESS values (2,'Rua da segunda cidade',2)
