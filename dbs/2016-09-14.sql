alter table `trade`
	ADD COLUMN `total_amount` int(11) DEFAULT 0 COMMENT '供货价' AFTER `caption`,
	ADD COLUMN `discount` int(11) DEFAULT 0 COMMENT '折扣价格' AFTER `total_amount`
	
   
   
   