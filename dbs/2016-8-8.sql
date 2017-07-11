
alter table `retailer` 
   add column `channel` varchar(64) NOT NULL COMMENT '渠道' after `tb_user_id`

alter table `supplier` 
   modify column `industry`  VARCHAR(50) NULL;


alter table `supplier` 
	modify column `email`  VARCHAR(32) NULL,
   modify column `contact_name`  VARCHAR(32) NULL;


