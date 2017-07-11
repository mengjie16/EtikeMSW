alter table `item` 
   ADD COLUMN `refer_urls` JSON NULL DEFAULT NULL AFTER `properties`,
   ADD COLUMN `note` varchar(256) NULL DEFAULT NULL COMMENT '宝贝备注' AFTER `skus`;
   