create table `item`( 
   `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '自增ID主键', 
   `title` varchar(128) NOT NULL COMMENT '商品标题', 
   `product_id` bigint(32) NOT NULL COMMENT '商品ID', 
   `supplier_id` int(11) NOT NULL COMMENT '供应商ID', 
   `cate_id` bigint(32) NOT NULL COMMENT '商品类目ID', 
   `pic_url` varchar(256) NOT NULL COMMENT '商品主图URL', 
   `num` int(32) NOT NULL COMMENT '商品数量', 
   `origin` varchar(64) NOT NULL COMMENT '商品产地', 
   `out_no` varchar(64) NOT NULL COMMENT '商品货号', 
   `deliver_type` enum('ONLINE_ORDER','UN_LOGISTICS') NOT NULL COMMENT '发货方式', 
   `unit_price` int(11) NOT NULL COMMENT '单价', 
   `unit` varchar(32) COMMENT '计量单位', 
   `retail_price` int(11) NOT NULL COMMENT '零售价格', 
   `sold_quantity` int(11) COMMENT '总销量', 
   `detail` varchar(25000) COMMENT '详情描述', 
   `status` enum('AUDITING','FAIL_AUDITING','ONLINE','HIDE','DELETED') COMMENT '商品状态', 
   `create_time` datetime COMMENT '创建时间', 
   `update_time` datetime COMMENT '更新时间', 
   PRIMARY KEY (`id`)
 )

create table `region`( 
   `id` int(11) NOT NULL COMMENT '区域ID', 
   `name` varchar(64) NOT NULL COMMENT '区域名称', 
   `parent_id` int(11) NOT NULL COMMENT '上级区域ID', 
   `type` int(11) NOT NULL COMMENT '区域类型', 
   `zip` varchar(32) NOT NULL COMMENT '邮政编码'
 )  Engine='Default' comment='' row_format=Default charset=utf8mb4 collate=utf8mb4_general_ci  

create table `tusibaby_dev`.`TableName1`( 
   `id` bigint(32) NOT NULL COMMENT '零售商ID主键', 
   `tb_user_id` bigint(64) NOT NULL COMMENT '淘宝userid', 
   `nick` varchar(32) COMMENT '昵称', 
   `avatar` varchar(256) COMMENT '头像url', 
   `session_key` varchar(128) COMMENT '验证标识', 
   `shop_name` varchar(30) COMMENT '店铺名称', 
   `shop_cid` bigint(32) COMMENT '类目ID', 
   `shop_url` varchar(256) COMMENT '店铺地址', 
   `level` tinyint(10) COMMENT '等级', 
   `type` enum('B','C') COMMENT '店铺类型', 
   `shop_bind_time` datetime COMMENT '店铺绑定时间', 
   `last_login_time` datetime COMMENT '最后登录时间', 
   `create_time` datetime NOT NULL COMMENT '创建时间', 
   `update_time` datetime COMMENT '更新时间', 
   PRIMARY KEY (`id`)
 )  Engine='Default' comment='' row_format=Default charset=utf8mb4 collate=utf8mb4_general_ci  


create table `supplier`( 
   `id` int(11) NOT NULL COMMENT '供应商id', 
   `name` varchar(18) COMMENT '名称', 
   `company` varchar(64) COMMENT '公司名称', 
   `setup_time` datetime COMMENT '公司成立时间', 
   `industry` varchar(128) COMMENT '主营行业', 
   `product` varchar(128) COMMENT '主营产品', 
   `email` varchar(64) COMMENT '邮箱', 
   `phone` varchar(18) COMMENT '手机', 
   `country` varchar(30) COMMENT '国家', 
   `country_id` tinyint(8) COMMENT '国家ID', 
   `province` varchar(32) COMMENT '省', 
   `province_id` varchar(32) COMMENT '省ID', 
   `city` varchar(64) COMMENT '城市', 
   `address` varchar(128) COMMENT '具体地址', 
   `domain` varchar(256) COMMENT '主页', 
   `level` tinyint(10) COMMENT '等级', 
   `type` enum('FACTORY','BRAND','RESELLER') COMMENT '供应商类型', 
   `pic_url` varchar(256) COMMENT '头像url', 
   `summary` varchar(128) COMMENT '简单描述', 
   `desc` text COMMENT 'html描述', 
   `contact_name` varchar(32) COMMENT '联系人名字', 
   `contact_phone` varchar(18) COMMENT '联系人电话', 
   `contact_email` varchar(32) COMMENT '联系人邮箱', 
   `contact_qq` varchar(32) COMMENT '联系人QQ', 
   `contact_wechat` varchar(32) COMMENT '联系微信', 
   `last_login_time` datetime COMMENT '最后登录时间', 
   `create_time` datetime COMMENT '创建时间', 
   `update_time` datetime COMMENT '更新时间', 
   PRIMARY KEY (`id`)
 )  Engine='Default' comment='' row_format=Default charset=utf8mb4 collate=utf8mb4_general_ci  

create table `user`( 
   `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '主键ID', 
   `name` varchar(30) NOT NULL COMMENT '用户名称', 
   `email` varchar(30) COMMENT '用户邮箱', 
   `avatar` varchar(256) COMMENT '用户头像url', 
   `phone` varchar(20) COMMENT '用户手机', 
   `password` varchar(64) COMMENT '用户密码', 
   `salt` varchar(10) COMMENT '随机字符', 
   `role` varchar(128) COMMENT '权限', 
   `user_id` bigint(62) COMMENT '角色ID', 
   `create_time` datetime NOT NULL COMMENT '创建时间', 
   `last_login_time` datetime COMMENT '最后登录时间', 
   PRIMARY KEY (`id`)
 )  Engine='Default' comment='' row_format=Default charset=utf8mb4 collate=utf8mb4_general_ci  