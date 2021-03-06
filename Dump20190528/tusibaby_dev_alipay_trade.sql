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
-- Table structure for table `alipay_trade`
--

DROP TABLE IF EXISTS `alipay_trade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alipay_trade`
--

LOCK TABLES `alipay_trade` WRITE;
/*!40000 ALTER TABLE `alipay_trade` DISABLE KEYS */;
INSERT INTO `alipay_trade` VALUES (1707182878309376,2,'支付：0.1元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,10,'2017-07-18 16:35:08',NULL,NULL),(1707187549788160,3,'支付：2.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,200,'2017-07-18 22:05:19',NULL,NULL),(1707189407361024,3,'支付：0.1元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,10,'2017-07-18 22:04:22',NULL,NULL),(1707232480850944,2,'支付：0.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,0,'2017-07-23 12:09:25',NULL,NULL),(1707232855458816,2,'支付：1344.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,134400,'2017-07-23 18:50:16',NULL,NULL),(1707236297980928,2,'支付：101.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,10100,'2017-07-23 15:08:57',NULL,NULL),(1707236797033472,2,'支付：101.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,10100,'2017-07-23 14:28:26',NULL,NULL),(1707239106871296,2,'支付：2014.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,201400,'2017-07-23 17:11:09',NULL,NULL),(1710212765793280,4,'支付：520.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,52000,'2017-10-21 16:03:55',NULL,NULL),(1710221104256000,5,'支付：596.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,59600,'2017-10-22 15:44:50',NULL,NULL),(1710221342073856,5,'支付：1304.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,130400,'2017-10-22 21:55:49',NULL,NULL),(1710223209066496,5,'支付：1304.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,130400,'2017-10-22 21:59:31',NULL,NULL),(1710223733096448,5,'支付：904.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,90400,'2017-10-22 17:49:20',NULL,NULL),(1710224229518336,5,'支付：596.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,59600,'2017-10-22 15:53:59',NULL,NULL),(1710226567860224,5,'支付：904.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,90400,'2017-10-22 17:46:20',NULL,NULL),(1710227201582080,5,'支付：596.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,59600,'2017-10-22 16:52:44',NULL,NULL),(1712099236683776,6,'支付：3005.0元',NULL,'WAIT_BUYER_PAY',NULL,NULL,NULL,NULL,300500,'2017-12-09 17:16:59',NULL,NULL);
/*!40000 ALTER TABLE `alipay_trade` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-28 21:23:27
