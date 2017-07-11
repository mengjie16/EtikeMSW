alter table `item` 
   ADD COLUMN `freight_temp` bigint(32) DEFAULT '0' COMMENT '运费模板id' AFTER `price_range_use`
   