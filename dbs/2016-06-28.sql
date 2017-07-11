alter table `item` 
   add column `dist_price_use` tinyint(2) DEFAULT '1' NOT NULL COMMENT '代销价格是否应用' after `dist_price`, 
   add column `price_range_use` tinyint(2) DEFAULT '1' NOT NULL COMMENT '批发价格区间是否应用' after `price_ranges`,
   change `dist_price` `dist_price` int(11) NULL  comment '分销价', 
   change `price_ranges` `price_ranges` json NULL  comment '商品批发价格区间',
   ADD COLUMN `skus` JSON NULL DEFAULT NULL comment '商品规格属性' AFTER `price_range_use`;