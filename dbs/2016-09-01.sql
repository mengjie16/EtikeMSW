CREATE TABLE `trade` (
  `id` bigint(32) unsigned NOT NULL COMMENT '交易id',
  `retailer_id` int(11) NOT NULL COMMENT '零售商id',
  `caption` varchar(64) DEFAULT NULL COMMENT '交易标题',
  `cargo_fee` int(11) DEFAULT '0' COMMENT '货款',
  `discount_fee` int(11) DEFAULT '0' COMMENT '优惠价格',
  `payment` int(11) NOT NULL COMMENT '实际支付金额',
  `total_fee` int(11) NOT NULL COMMENT '交易总金额',
  `shipping_fee` int(11) NOT NULL COMMENT '物流费用',
  `expect_consign_time` datetime DEFAULT NULL COMMENT '预计发货时间',
  `consign_time` datetime DEFAULT NULL COMMENT '发货时间',
  `status` enum('TRADE_AUDITING','TRADE_FAIL_AUDITING','TRADE_SETTLEMENT_BEEN','TRADE_CLOSE','TRADE_USER_CANCELLED') NOT NULL DEFAULT 'TRADE_AUDITING' COMMENT '交易状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
  `note` varchar(128) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;