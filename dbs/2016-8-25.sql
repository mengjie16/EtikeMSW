CREATE TABLE `alipay_trade` (
  `id` bigint(18) unsigned NOT NULL COMMENT '交易id',
  `user_id` bigint(32) DEFAULT NULL COMMENT '内部用户id标识',
  `subject` varchar(128) DEFAULT '' COMMENT '商品名称',
  `trade_no` varchar(64) DEFAULT NULL COMMENT '支付宝交易编号',
  `trade_status` enum('WAIT_BUYER_PAY','TRADE_CLOSED','TRADE_SUCCESS','TRADE_PENDING','TRADE_FINISHED') NOT NULL DEFAULT 'WAIT_BUYER_PAY' COMMENT '交易状态',
  `seller_email` varchar(32) DEFAULT NULL COMMENT '卖家支付宝账号',
  `buyer_email` varchar(32) DEFAULT NULL COMMENT '买家支付宝账户',
  `buyer_id` varchar(32) DEFAULT NULL COMMENT '买家支付宝账户号',
  `seller_id` varchar(32) DEFAULT NULL COMMENT '卖家支付宝账户号',
  `total_fee` int(11) NOT NULL COMMENT '交易金额(单位：元)',
  `gmt_create` datetime NOT NULL COMMENT '交易创建时间',
  `gmt_payment` datetime DEFAULT NULL COMMENT '交易付款时间',
  `gmt_close` datetime DEFAULT NULL COMMENT '交易关闭时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;