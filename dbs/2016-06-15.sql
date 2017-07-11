alter table `user` 
   add column `is_delete` tinyint(2) DEFAULT '0' NULL COMMENT '是否删除' after `role`