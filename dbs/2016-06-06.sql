alter table `item_cate` 
   add column `create_time` datetime NOT NULL after `level`

alter table  `item_cate` 
   change `sort_order` `ordinal` int(11) default '0' NOT NULL comment '同级兄弟中自己的展示顺序'