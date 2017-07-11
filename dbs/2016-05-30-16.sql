 CREATE TABLE `brand` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `name` varchar(64) NOT NULL COMMENT '品牌名称',
  `secondary_name` varchar(64) DEFAULT NULL COMMENT '品牌从名称',
  `pic_url` varchar(256) DEFAULT NULL COMMENT '品牌logo',
  `company` varchar(64) NOT NULL COMMENT '品牌最根源的公司',
  `holder` varchar(64) NOT NULL COMMENT '和平台对接',
  `note` varchar(256) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4