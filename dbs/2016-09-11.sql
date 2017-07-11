alter table `item` 
   ADD COLUMN `supply_price` int(11) DEFAULT 0 COMMENT '供货价' AFTER `retail_price`,
   ADD COLUMN `net_weight`  DECIMAL(10,2) null COMMENT '净重量' AFTER `supply_price`,
   ADD COLUMN `gross_weight` DECIMAL(10,2) null COMMENT '毛重量'  AFTER `net_weight`,
   ADD COLUMN `quality` int(11) DEFAULT '1000' COMMENT '品质系数' AFTER `gross_weight`
   