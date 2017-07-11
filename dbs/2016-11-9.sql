ALTER TABLE `user`   
  CHANGE `is_delete` `is_delete` TINYINT(2) DEFAULT 0  NOT NULL   COMMENT '是否删除',
  ADD COLUMN `is_main_account` TINYINT DEFAULT 0  NOT NULL   COMMENT '是否是主账号' AFTER `user_id`,
  ADD COLUMN `qq` VARCHAR(15) NULL   COMMENT 'QQ' AFTER `is_main_account`,
  ADD COLUMN `weixin` VARCHAR(30) NULL   COMMENT '微信' AFTER `qq`;

ALTER TABLE `retailer`   
  DROP COLUMN `tb_user_id`, 
  DROP COLUMN `nick`, 
  DROP COLUMN `avatar`, 
  DROP COLUMN `session_key`, 
  DROP COLUMN `shop_cid`, 
  DROP COLUMN `level`, 
  DROP COLUMN `type`, 
  DROP COLUMN `shop_bind_time`, 
  DROP COLUMN `last_login_time`, 
  CHANGE `shop_name` `name` VARCHAR(30)  NULL   COMMENT '名称',
  ADD COLUMN `note` VARCHAR(500) NULL   COMMENT '备注' AFTER `update_time`;



ALTER TABLE `supplier`   
  DROP COLUMN `company`, 
  DROP COLUMN `industry`, 
  DROP COLUMN `email`, 
  DROP COLUMN `phone`, 
  DROP COLUMN `level`, 
  DROP COLUMN `pic_url`, 
  DROP COLUMN `summary`, 
  DROP COLUMN `descption`, 
  DROP COLUMN `contact_name`, 
  DROP COLUMN `contact_phone`, 
  DROP COLUMN `contact_email`, 
  DROP COLUMN `contact_qq`, 
  DROP COLUMN `contact_wechat`, 
  DROP COLUMN `last_login_time`, 
  ADD COLUMN `note` VARCHAR(500) NULL   COMMENT '备注' AFTER `update_time`;
