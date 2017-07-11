CREATE TABLE `article`( 
   `id` BIGINT(64) NOT NULL AUTO_INCREMENT COMMENT '主键', 
   `author_id` BIGINT(64) COMMENT '作者ID', 
   `author_name` VARCHAR(64) COMMENT '作者名字', 
   `title` VARCHAR(32) NOT NULL COMMENT '标题', 
   `type_name` VARCHAR(12) NOT NULL COMMENT '类型名称', 
   `type_id` VARCHAR(32) NOT NULL COMMENT '类型ID', 
   `content` MEDIUMTEXT NOT NULL COMMENT '文章内容', 
   `is_public` TINYINT(2) NOT NULL DEFAULT '1' COMMENT '是否对外可见', 
   `is_inlist` TINYINT(2) NOT NULL DEFAULT '1' COMMENT '是否显示文章列表', 
 `is_delete` TINYINT(2) NOT NULL DEFAULT '0' COMMENT '是否标记删除',  
   `create_time` DATETIME NOT NULL COMMENT '创建时间', 
   PRIMARY KEY (`id`)
 )ENGINE='Default' COMMENT='' ROW_FORMAT=DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci  