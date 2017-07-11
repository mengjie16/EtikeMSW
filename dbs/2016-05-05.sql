alter table `supplier` 
   change `desc` `descption` mediumtext character set utf8mb4 collate utf8mb4_general_ci NULL  comment '描述Html'


 
   CREATE TABLE `retailer` (
  `id` BIGINT(32) NOT NULL AUTO_INCREMENT COMMENT '系统主键',
  `tb_user_id` BIGINT(64) NOT NULL COMMENT '淘宝userid',
  `nick` VARCHAR(32) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(256) DEFAULT NULL COMMENT '头像url',
  `session_key` VARCHAR(128) DEFAULT NULL COMMENT '验证标识',
  `shop_name` VARCHAR(30) DEFAULT NULL COMMENT '店铺名称',
  `shop_cid` BIGINT(32) DEFAULT NULL COMMENT '类目ID',
  `shop_url` VARCHAR(256) DEFAULT NULL COMMENT '店铺地址',
  `level` TINYINT(10) DEFAULT NULL COMMENT '等级',
  `type` ENUM('B','C') DEFAULT NULL COMMENT '店铺类型',
  `shop_bind_time` DATETIME DEFAULT NULL COMMENT '店铺绑定时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4