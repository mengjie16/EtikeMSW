
alter table `user` 
   add column `is_auth` tinyint(2) DEFAULT '0' NULL COMMENT '是否认证' after `user_id`
