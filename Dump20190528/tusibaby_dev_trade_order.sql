-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: tusibaby_dev
-- ------------------------------------------------------
-- Server version	5.7.18-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `trade_order`
--

DROP TABLE IF EXISTS `trade_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trade_order` (
  `id` bigint(32) unsigned NOT NULL COMMENT '订单id',
  `trade_id` bigint(32) DEFAULT '0' COMMENT '所属交易id',
  `out_order_no` varchar(64) DEFAULT NULL COMMENT '外部订单id',
  `caption` varchar(64) DEFAULT NULL COMMENT '订单标题',
  `retailer_id` int(11) NOT NULL COMMENT '零售商id',
  `num` int(11) DEFAULT '0' COMMENT '商品总件数',
  `cargo_fee` decimal(10,2) DEFAULT NULL,
  `shipping_fee` decimal(10,2) DEFAULT NULL,
  `total_fee` decimal(10,2) DEFAULT NULL,
  `express` varchar(64) DEFAULT NULL COMMENT '快递公司',
  `exp_no` varchar(128) DEFAULT NULL COMMENT '快递单号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `consign_time` datetime DEFAULT NULL COMMENT '发货时间',
  `note` varchar(128) DEFAULT NULL COMMENT '备注',
  `buyer_info` json DEFAULT NULL COMMENT '买家信息',
  `product_info` json DEFAULT NULL COMMENT '商品信息',
  `status` enum('ORDER_CONSIGN_WAIT','ORDER_CONSIGN_BEEN','ORDER_SUCCESS','ORDER_REFUNDING','ORDER_REFUNDING_FINISH','TRADE_UNPAIED') NOT NULL DEFAULT 'ORDER_CONSIGN_WAIT' COMMENT '订单状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trade_order`
--

LOCK TABLES `trade_order` WRITE;
/*!40000 ALTER TABLE `trade_order` DISABLE KEYS */;
INSERT INTO `trade_order` VALUES (102113626281472,1710219240666112,'18',NULL,4,2,58.00,12.00,70.00,NULL,NULL,'2017-10-21 13:38:15',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"我有一只小毛驴\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"\", \"title\": \"儿童牙胶 Rassel mit Beissring mit beweglichen Elementen\", \"itemId\": 488, \"picUrl\": \"//cdn.tusibaby.com/o_1bll52av413pk1u0u1c4jqg647ch.jpg\", \"brandName\": \"Fashy\", \"itemPrice\": 29, \"retailPrice\": 29}','TRADE_UNPAIED'),(102217669496320,171022321807360,'29',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-10-22 17:46:11',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','TRADE_UNPAIED'),(104217432235008,201801042146245056,'45',NULL,18,1,1206.89,12.00,1218.89,NULL,NULL,'2018-01-04 21:46:25',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"顶面\", \"IDcard\": \"231121198911140520\", \"gender\": \"male\", \"region\": \"西城区\", \"address\": \"夺夺地寺五一地\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"å°\", \"title\": \"一二三四五六\", \"itemId\": 493, \"picUrl\": \"//cdn.tusibaby.com/o_1c0t7929jl9c183j1k6683u1ue4h.jpg\", \"brandName\": \"FisherPrice\", \"itemPrice\": 1206.89, \"productNum\": 0, \"retailPrice\": 1206.89}','ORDER_CONSIGN_WAIT'),(104217685703680,201801042146245056,'46',NULL,18,1,200.00,12.00,212.00,NULL,NULL,'2018-01-04 21:46:25',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"顶面\", \"IDcard\": \"231121198911140520\", \"gender\": \"male\", \"region\": \"西城区\", \"address\": \"夺夺地寺五一地\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, æ°çå¿ç»æµè£, å°ºå¯¸ 1, 4 è¢x 22 ç\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"productNum\": 0, \"retailPrice\": 200}','ORDER_CONSIGN_WAIT'),(108201784263680,2018010820235899584,'47',NULL,18,1,1206.89,12.00,1218.89,NULL,NULL,'2018-01-08 20:23:58',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"顶面\", \"IDcard\": \"231121198911140520\", \"gender\": \"male\", \"region\": \"西城区\", \"address\": \"夺夺地寺五一地\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"123\", \"title\": \"123456\", \"itemId\": 493, \"picUrl\": \"//cdn.tusibaby.com/o_1c0t7929jl9c183j1k6683u1ue4h.jpg1\", \"brandName\": \"FisherPrice\", \"itemPrice\": 1206.89, \"productNum\": 0, \"retailPrice\": 1206.89}','ORDER_CONSIGN_WAIT'),(108201803953152,2018010820235899584,'48',NULL,18,1,1000.00,12.00,1012.00,NULL,NULL,'2018-01-08 20:23:58',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"顶面\", \"IDcard\": \"231121198911140520\", \"gender\": \"male\", \"region\": \"西城区\", \"address\": \"夺夺地寺五一地\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"绿\", \"title\": \"测试专用测试专用\", \"itemId\": 492, \"picUrl\": \"//cdn.tusibaby.com/o_1c0t3lpda198g1b36ao81bt61nbsh.jpg\", \"brandName\": \"FisherPrice\", \"itemPrice\": 1000, \"productNum\": 0, \"retailPrice\": 1000}','ORDER_CONSIGN_WAIT'),(723164183787520,1707234059047936,'21',NULL,2,4,116.00,12.00,128.00,NULL,NULL,'2017-07-23 16:06:24',NULL,NULL,NULL,'{\"city\": \"???\", \"name\": \"xiaowu\", \"region\": \"???\", \"address\": \"aa\", \"contact\": \"13456652103\", \"province\": \"??\", \"provinceId\": 110000}','{\"sku\": \"mit beweglichen Elementen0\", \"itemId\": 488, \"itemPrice\": 29}','TRADE_UNPAIED'),(723164258961408,1707234059047936,'22',NULL,2,11,880.00,12.00,892.00,NULL,NULL,'2017-07-23 16:06:24',NULL,NULL,NULL,'{\"city\": \"???\", \"name\": \"xiaowu\", \"region\": \"???\", \"address\": \"aa\", \"contact\": \"13456652103\", \"province\": \"??\", \"provinceId\": 110000}','{\"sku\": \"????? Pre? (600 g), 1 ?0\", \"itemId\": 485, \"itemPrice\": 80}','TRADE_UNPAIED'),(723164290156544,1707234059047936,'23',NULL,2,2,160.00,12.00,172.00,NULL,NULL,'2017-07-23 16:06:24',NULL,NULL,NULL,'{\"city\": \"???\", \"name\": \"xiaowu\", \"region\": \"???\", \"address\": \"aa\", \"contact\": \"13456652103\", \"province\": \"??\", \"provinceId\": 110000}','{\"sku\": \"BIO??????? 1? (600 g), 1 ?0\", \"itemId\": 485, \"itemPrice\": 80}','TRADE_UNPAIED'),(723176487798784,1707235657601024,'24',NULL,2,14,2000.00,12.00,2012.00,NULL,NULL,'2017-07-23 17:10:59',NULL,NULL,NULL,'{\"city\": \"???\", \"name\": \"xiaowu\", \"region\": \"???\", \"address\": \"aa\", \"contact\": \"13456652103\", \"province\": \"??\", \"provinceId\": 110000}','{\"color\": \"New Baby, ??????, ?? 1, 4 ?x 22 ?\", \"title\": \"Premium Protection?????????\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','TRADE_UNPAIED'),(723182244651008,1707232118187008,'21',NULL,2,10,290.00,12.00,302.00,NULL,NULL,'2017-07-23 18:50:06',NULL,NULL,NULL,'{\"city\": \"???\", \"name\": \"xiaowu\", \"region\": \"???\", \"address\": \"aa\", \"contact\": \"13456652103\", \"province\": \"??\", \"provinceId\": 110000}','{\"color\": \"mit beweglichen Elementen\", \"title\": \"???? Rassel mit Beissring mit beweglichen Elementen\", \"itemId\": 488, \"picUrl\": \"//cdn.tusibaby.com/o_1bll52av413pk1u0u1c4jqg647ch.jpg\", \"brandName\": \"Fashy\", \"itemPrice\": 29, \"retailPrice\": 29}','TRADE_UNPAIED'),(723182319476736,1707232118187008,'22',NULL,2,11,880.00,12.00,892.00,NULL,NULL,'2017-07-23 18:50:07',NULL,NULL,NULL,'{\"city\": \"???\", \"name\": \"xiaowu\", \"region\": \"???\", \"address\": \"aa\", \"contact\": \"13456652103\", \"province\": \"??\", \"provinceId\": 110000}','{\"color\": \"????? Pre? (600 g), 1 ?\", \"title\": \"??BIO??????\", \"itemId\": 485, \"picUrl\": \"//cdn.tusibaby.com/o_1bll3v8b01fkq15rpau51nm7av1s.jpg\", \"brandName\": \"HiPP\", \"itemPrice\": 80, \"retailPrice\": 80}','TRADE_UNPAIED'),(723182350012416,1707232118187008,'23',NULL,2,2,160.00,12.00,172.00,NULL,NULL,'2017-07-23 18:50:07',NULL,NULL,NULL,'{\"city\": \"???\", \"name\": \"xiaowu\", \"region\": \"???\", \"address\": \"aa\", \"contact\": \"13456652103\", \"province\": \"??\", \"provinceId\": 110000}','{\"color\": \"BIO??????? 1? (600 g), 1 ?\", \"title\": \"??BIO??????\", \"itemId\": 485, \"picUrl\": \"//cdn.tusibaby.com/o_1bll3v8b01fkq15rpau51nm7av1s.jpg\", \"brandName\": \"HiPP\", \"itemPrice\": 80, \"retailPrice\": 80}','TRADE_UNPAIED'),(1021131054841856,1710219240666112,'19',NULL,4,1,200.00,12.00,212.00,NULL,NULL,'2017-10-21 13:38:15',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"我有一只小毛驴\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','TRADE_UNPAIED'),(1021131062947840,1710219240666112,'24',NULL,4,2,160.00,12.00,172.00,NULL,NULL,'2017-10-21 13:38:15',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"我有一只小毛驴\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"出生段奶粉 Pre段 (600 g), 1 盒\", \"title\": \"喜宝BIO有机配方奶粉\", \"itemId\": 485, \"picUrl\": \"//cdn.tusibaby.com/o_1bll3v8b01fkq15rpau51nm7av1s.jpg\", \"brandName\": \"HiPP\", \"itemPrice\": 80, \"retailPrice\": 80}','TRADE_UNPAIED'),(1021131071361024,1710219240666112,'25',NULL,4,1,88.00,12.00,100.00,NULL,NULL,'2017-10-21 13:38:15',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"我有一只小毛驴\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"红色\", \"title\": \"母婴商品33\", \"itemId\": 100, \"picUrl\": \"//cdn.tusibaby.com/o_1blkq1n925gs3g215ejp791sdrh.jpg\", \"brandName\": \"母婴\", \"itemPrice\": 88, \"retailPrice\": 88}','TRADE_UNPAIED'),(1022162866029568,1710222400699392,'27',NULL,5,2,582.00,12.00,594.00,NULL,NULL,'2017-10-22 16:52:38',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"mit beweglichen Elementen 1\", \"title\": \"儿童牙胶 Rassel mit Beissring mit beweglichen Elementen1\", \"itemId\": 488, \"picUrl\": \"//cdn.tusibaby.com/o_1bll52av413pk1u0u1c4jqg647ch.jpg\", \"brandName\": \"Fashy\", \"itemPrice\": 291, \"retailPrice\": 291}','TRADE_UNPAIED'),(1022171977564160,1710221667312640,'29',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-10-22 17:48:49',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','TRADE_UNPAIED'),(1022182051416064,1710221869737984,'29',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-10-22 18:03:33',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','ORDER_CONSIGN_WAIT'),(1022182086076416,1710221869737984,'30',NULL,5,2,200.00,12.00,212.00,NULL,NULL,'2017-10-22 18:03:33',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','ORDER_CONSIGN_WAIT'),(1022185936717824,1710225635153920,'29',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-10-22 18:04:12',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','ORDER_CONSIGN_WAIT'),(1022185945929728,1710225635153920,'30',NULL,5,2,200.00,12.00,212.00,NULL,NULL,'2017-10-22 18:04:12',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','ORDER_CONSIGN_WAIT'),(1022211506646016,1710221218643968,'35',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-10-22 21:55:13',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','TRADE_UNPAIED'),(1022211515436032,1710221218643968,'36',NULL,5,2,400.00,12.00,412.00,NULL,NULL,'2017-10-22 21:55:13',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','TRADE_UNPAIED'),(1022212797912064,1710222781851648,'35',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-10-22 21:54:50',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','ORDER_CONSIGN_WAIT'),(1022213210499072,1710223143513088,'35',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-10-22 21:59:24',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','TRADE_UNPAIED'),(1022213246740480,1710223143513088,'36',NULL,5,2,400.00,12.00,412.00,NULL,NULL,'2017-10-22 21:59:24',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','TRADE_UNPAIED'),(1209131561423872,1712091089257472,'35',NULL,5,2,1780.00,12.00,1792.00,NULL,NULL,'2017-12-09 13:38:22',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','ORDER_CONSIGN_WAIT'),(1209131565122560,1712091089257472,'36',NULL,5,2,400.00,12.00,412.00,NULL,NULL,'2017-12-09 13:38:22',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','ORDER_CONSIGN_WAIT'),(1209132915733504,1712092618269696,'35',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-12-09 13:04:31',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','ORDER_CONSIGN_WAIT'),(1209132926010368,1712092618269696,'36',NULL,5,2,400.00,12.00,412.00,NULL,NULL,'2017-12-09 13:04:31',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','ORDER_CONSIGN_WAIT'),(1209137950887936,1712097275400192,'35',NULL,5,1,890.00,12.00,902.00,NULL,NULL,'2017-12-09 13:03:08',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','ORDER_CONSIGN_WAIT'),(1209137973608448,1712097275400192,'36',NULL,5,2,400.00,12.00,412.00,NULL,NULL,'2017-12-09 13:03:08',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','ORDER_CONSIGN_WAIT'),(1209139381693440,1712099138989056,'35',NULL,5,2,1780.00,12.00,1792.00,NULL,NULL,'2017-12-09 13:50:52',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','ORDER_CONSIGN_WAIT'),(1209139390729216,1712099138989056,'36',NULL,5,2,400.00,12.00,412.00,NULL,NULL,'2017-12-09 13:50:52',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"title\": \"Premium Protection系列特级棉柔纸尿裤\", \"itemId\": 487, \"picUrl\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"brandName\": \"Pampers\", \"itemPrice\": 200, \"retailPrice\": 200}','ORDER_CONSIGN_WAIT'),(1209147435184128,1712097163389952,'35',NULL,5,4,3560.00,12.00,3572.00,NULL,NULL,'2017-12-09 14:31:52',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"10字符以内写出\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"蓝色\", \"title\": \"测试用测试用\", \"itemId\": 490, \"picUrl\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"brandName\": \"费雪\", \"itemPrice\": 890, \"retailPrice\": 890}','ORDER_CONSIGN_WAIT'),(1209173788881920,1712092950717440,'41',NULL,6,3,3000.00,12.00,3012.00,NULL,NULL,'2017-12-09 17:15:58',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"一二二三大理石\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"绿\", \"title\": \"测试专用测试专用\", \"itemId\": 492, \"picUrl\": \"//cdn.tusibaby.com/o_1c0t3mj9i7ll1vu1sprn0f16s717.jpg\", \"brandName\": \"FisherPrice\", \"itemPrice\": 1000, \"retailPrice\": 1000}','TRADE_UNPAIED'),(1209174455587840,1712092950717440,'42',NULL,6,1,5.00,12.00,17.00,NULL,NULL,'2017-12-09 17:15:58',NULL,NULL,NULL,'{\"city\": \"北京市\", \"name\": \"二二\", \"region\": \"东城区\", \"address\": \"一二二三大理石\", \"contact\": \"15006188150\", \"province\": \"北京\", \"provinceId\": 110000}','{\"color\": \"89\", \"title\": \"德国产品的Prüfung\", \"itemId\": 491, \"picUrl\": \"//cdn.tusibaby.com/o_1bu220h8cl0h1ca0o0mafc1gclh.jpg\", \"brandName\": \"NUK\", \"itemPrice\": 5, \"retailPrice\": 5}','TRADE_UNPAIED');
/*!40000 ALTER TABLE `trade_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-28 21:23:30
