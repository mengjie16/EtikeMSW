create table `album`( 
   `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '主键', 
   `name` varchar(64) NOT NULL COMMENT '专辑名称', 
   `album_items` json NULL COMMENT '专辑商品列表', 
   `create_time` datetime NOT NULL COMMENT '创建时间', 
   PRIMARY KEY (`id`)
 )	