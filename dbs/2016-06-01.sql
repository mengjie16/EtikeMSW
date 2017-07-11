alter table `item` drop column `unit_price`,
   add column `dist_price` int(11) NULL COMMENT '分销价' after `status`