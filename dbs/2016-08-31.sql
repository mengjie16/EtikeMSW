CREATE TABLE `trade_order` (
  `id` bigint(32) unsigned NOT NULL COMMENT '订单id',
  `trade_id` bigint(32) DEFAULT '0' COMMENT '所属交易id',
  `out_order_no` varchar(64) DEFAULT NULL COMMENT '外部订单id',
  `caption` varchar(64) DEFAULT NULL COMMENT '订单标题',
  `retailer_id` int(11) NOT NULL COMMENT '零售商id',
  `num` int(11) DEFAULT '0' COMMENT '商品总件数',
  `cargo_fee` int(11) DEFAULT '0' COMMENT '商品总金额',
  `shipping_fee` int(11) DEFAULT NULL COMMENT '物流费用',
  `total_fee` int(11) DEFAULT NULL COMMENT '订单总金额',
  `express` varchar(64) DEFAULT NULL COMMENT '快递公司',
  `exp_no` varchar(128) DEFAULT NULL COMMENT '快递单号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `consign_time` datetime DEFAULT NULL COMMENT '发货时间',
  `note` varchar(128) DEFAULT NULL COMMENT '备注',
  `buyer_info` json DEFAULT NULL COMMENT '买家信息',
  `product_info` json DEFAULT NULL COMMENT '商品信息',
  `status` enum('ORDER_CONSIGN_WAIT','ORDER_CONSIGN_BEEN','ORDER_SUCCESS','ORDER_REFUNDING','ORDER_REFUNDING_FINISH') NOT NULL DEFAULT 'ORDER_CONSIGN_WAIT' COMMENT '订单状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;