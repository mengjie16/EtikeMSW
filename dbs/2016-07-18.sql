create table `supplier_sendlocation_temp`( 
   `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '自增主键', 
   `supplier_id` int(11) NOT NULL COMMENT '供应商ID', 
   `country` varchar(30) NOT NULL COMMENT '国家', 
   `country_id` tinyint(8) NOT NULL COMMENT '国家ID', 
   `province` varchar(32) NOT NULL COMMENT '省份', 
   `province_id` varchar(32) NOT NULL COMMENT '省份ID', 
   `city` varchar(64) NOT NULL COMMENT '城市', 
   `region` varchar(32) NOT NULL COMMENT '区域' ,
   `address` varchar(128) NOT NULL COMMENT '地址', 
   `create_time` datetime NOT NULL COMMENT '创建时间', 
   PRIMARY KEY (`id`)
 )