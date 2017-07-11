alter table `item` 
   add column `initial_quantity` int(11) DEFAULT '0' NULL COMMENT '初始销量' after `retail_price`