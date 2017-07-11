ALTER TABLE `item` 
ADD COLUMN `properties` JSON NULL COMMENT '商品的各项SKU属性，风格，承重类似' AFTER `status`;
