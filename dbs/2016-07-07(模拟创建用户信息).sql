// 插入供应商数据
INSERT INTO `supplier`
            (
             `name`,
             `company`,
             `industry`,
             `product`,
             `email`,
             `phone`,
             `country`,
             `country_id`,
             `province`,
             `province_id`,
             `city`,
             `region`,
             `address`,
             `domain`,
             `level`,
             `type`,
             `pic_url`,
             `summary`,
             `descption`,
             `contact_name`,
             `contact_phone`,
             `contact_email`,
             `contact_qq`,
             `contact_wechat`,
             `last_login_time`,
             `create_time`,
             `update_time`)
VALUES (
        'name',	    // 名字不能为空
        'company',  // 公司不能为空
        'industry', // 行业不能为空
        'product',
        'email',
        'phone', // 手机不能为空
        'country',
        'country_id',
        'province',
        'province_id',
        'city',
        'region',
        'address',
        'domain',
        'level',
        'type',
        'pic_url',
        'summary',
        'descption',
        'contact_name',
        'contact_phone',
        'contact_email',
        'contact_qq',
        'contact_wechat',
        'last_login_time',
        'create_time',
        'update_time');
 
// 查询供应商信息
 SELECT
  `id`,
  `name`,
  `phone`
FROM `supplier`
WHERE phone = ''
LIMIT 0, 1000;
 
 // 插入零售商
 INSERT INTO `retailer`
            (
             `tb_user_id`,
             `nick`,
             `avatar`,
             `session_key`,
             `shop_name`,
             `shop_cid`,
             `shop_url`,
             `level`,
             `type`,
             `shop_bind_time`,
             `last_login_time`,
             `create_time`,
             `update_time`)
VALUES (
        'tb_user_id', // 淘宝用户id不能为空
        'nick',
        'avatar',
        'session_key',
        'shop_name',
        'shop_cid',
        'shop_url',
        'level',
        'type',
        'shop_bind_time',
        'last_login_time',
        'create_time',
        'update_time');

// 查询零售商信息 
SELECT
  `id`,
  `tb_user_id`
FROM `tusibaby_dev`.`retailer`
WHERE tb_user_id = ''
LIMIT 0, 1000;

// 插入用户信息
INSERT INTO `user`
            (`id`,
             `name`,
             `email`,
             `avatar`,
             `phone`,
             `password`,
             `salt`,
             `role`,
             `is_delete`,
             `user_id`,
             `create_time`,
             `last_login_time`)
VALUES ('id',
        'name',
        'email',
        'avatar',
        'phone',
        'password',
        'salt',
        'role',
        'is_delete',
        'user_id',
        NOW(),
        'last_login_time');