CREATE TABLE `wechat_user` (
  `id` bigint(32) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `avatar` varchar(256) NOT NULL DEFAULT '' COMMENT '头像',
  `nick` varchar(64) NOT NULL DEFAULT '' COMMENT '昵称',
  `open_id` varchar(32) NOT NULL DEFAULT '' COMMENT 'OPEN_ID',
  `unionid` varchar(32) DEFAULT NULL COMMENT 'unionid',
  `auth_time` datetime DEFAULT NULL COMMENT '首次授权时间',
  `last_auth_time` datetime DEFAULT NULL COMMENT '最后一次授权时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8000 DEFAULT CHARSET=utf8mb4;
