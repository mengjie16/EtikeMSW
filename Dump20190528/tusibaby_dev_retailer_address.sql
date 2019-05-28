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
-- Table structure for table `retailer_address`
--

DROP TABLE IF EXISTS `retailer_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `retailer_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `retailer_id` int(11) NOT NULL COMMENT '零售商ID',
  `name` varchar(18) DEFAULT NULL COMMENT '用户名称',
  `phone` varchar(20) DEFAULT NULL COMMENT '用户手机',
  `country` varchar(30) DEFAULT NULL COMMENT '国家',
  `country_id` tinyint(8) DEFAULT NULL COMMENT '国家ID',
  `province` varchar(32) DEFAULT NULL COMMENT '省',
  `province_id` varchar(32) DEFAULT NULL COMMENT '省ID',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `region` varchar(20) DEFAULT NULL COMMENT '区域',
  `address` varchar(128) DEFAULT NULL COMMENT '具体地址',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `default_address` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否为默认地址',
  `gender` varchar(8) DEFAULT NULL,
  `IDcard` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1016 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `retailer_address`
--

LOCK TABLES `retailer_address` WRITE;
/*!40000 ALTER TABLE `retailer_address` DISABLE KEYS */;
INSERT INTO `retailer_address` VALUES (1002,2,'mm','13127787656','中国',1,'北京','110000','北京市','东城区','aha','2017-07-17 17:45:56','2017-07-17 17:45:56',0,NULL,NULL),(1003,2,'xiaowu','13456652103','中国',1,'北京','110000','北京市','西城区','aa','2017-07-17 17:47:20','2017-07-17 17:47:20',1,NULL,NULL),(1008,1,'张珊珊','15822222222','中国',1,'北京','110000','北京市','东城区','重中之重','2017-07-18 10:34:43','2017-07-18 10:34:43',0,NULL,NULL),(1009,1,'咋整','15222222222','中国',1,'北京','110000','北京市','东城区','啧啧啧','2017-07-18 10:37:20','2017-07-18 10:37:20',1,NULL,NULL),(1010,3,'aaa','13122243534','中国',1,'北京','110000','北京市','东城区','ssss','2017-07-21 19:35:59','2017-07-21 19:35:59',1,NULL,NULL),(1011,3,'小小','13224343332','中国',1,'北京','110000','北京市','东城区','西土城路10号','2017-07-22 10:26:00','2017-07-22 10:26:00',0,NULL,NULL),(1013,5,'二二','15006188150','中国',1,'北京','110000','北京市','东城区','10字符以内写出','2017-10-22 15:42:15','2017-10-22 15:42:15',1,NULL,NULL),(1014,6,'二二','15006188150','中国',1,'北京','110000','北京市','东城区','一二二三大理石','2017-12-09 17:15:43','2017-12-09 17:15:43',1,NULL,NULL),(1015,18,'顶面','15006188150','中国',1,'北京','110000','北京市','西城区','夺夺地寺五一地','2018-01-04 21:46:15','2018-01-04 21:46:15',1,'male','231121198911140520');
/*!40000 ALTER TABLE `retailer_address` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-28 21:23:32
