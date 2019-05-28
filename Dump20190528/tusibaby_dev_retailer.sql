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
-- Table structure for table `retailer`
--

DROP TABLE IF EXISTS `retailer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `retailer` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '系统主键',
  `channel` varchar(64) NOT NULL COMMENT '渠道',
  `name` varchar(30) DEFAULT NULL COMMENT '名称',
  `shop_url` varchar(256) DEFAULT NULL COMMENT '店铺地址',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `note` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `retailer`
--

LOCK TABLES `retailer` WRITE;
/*!40000 ALTER TABLE `retailer` DISABLE KEYS */;
INSERT INTO `retailer` VALUES (1,'1',NULL,NULL,'2017-07-16 17:20:18','2017-07-16 17:20:18',NULL),(2,'1',NULL,NULL,'2017-07-17 17:45:06','2017-07-17 17:45:06',NULL),(3,'1',NULL,NULL,'2017-07-22 21:10:15','2017-07-22 21:10:15',NULL),(4,'1',NULL,NULL,'2017-10-21 21:30:19','2017-10-21 21:30:19',NULL),(5,'1',NULL,NULL,'2017-12-09 11:43:39','2017-12-09 11:43:39',NULL),(6,'1',NULL,NULL,'2017-12-09 13:55:18','2017-12-09 13:55:18',NULL),(7,'1',NULL,NULL,'2017-12-09 14:49:45','2017-12-09 14:49:45',NULL),(8,'1',NULL,NULL,'2017-12-09 15:02:58','2017-12-09 15:02:58',NULL),(9,'1',NULL,NULL,'2017-12-09 15:06:53','2017-12-09 15:06:53',NULL),(10,'1',NULL,NULL,'2017-12-09 15:39:19','2017-12-09 15:39:19',NULL),(11,'1',NULL,NULL,'2017-12-09 15:40:16','2017-12-09 15:40:16',NULL),(12,'1',NULL,NULL,'2017-12-09 15:45:32','2017-12-09 15:45:32',NULL),(13,'1',NULL,NULL,'2017-12-09 15:46:43','2017-12-09 15:46:43',NULL),(14,'1',NULL,NULL,'2017-12-09 16:14:00','2017-12-09 16:14:00',NULL),(15,'1',NULL,NULL,'2017-12-09 19:15:06','2017-12-09 19:15:06',NULL),(16,'1','admin_123456',NULL,'2017-12-30 18:34:06','2017-12-30 18:34:06',NULL),(17,'1','123456',NULL,'2017-12-30 18:52:11','2017-12-30 18:52:11',NULL),(18,'1','admin123456',NULL,'2018-01-07 14:56:44','2018-01-07 14:56:44',NULL);
/*!40000 ALTER TABLE `retailer` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-28 21:23:28
