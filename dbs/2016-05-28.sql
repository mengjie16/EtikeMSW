create table `item_cate`( 
   `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '宝贝类目ID(主键)', 
   `name` varchar(64) NOT NULL COMMENT '宝贝类目名称', 
   `parent_cid` bigint(32) NOT NULL COMMENT '父级ID', 
   `is_parent` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否父级类目', 
   `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '同级兄弟中自己的展示顺序', 
   `level` int(11) NOT NULL DEFAULT '0' COMMENT '类目级别', 
   PRIMARY KEY (`id`)
 )