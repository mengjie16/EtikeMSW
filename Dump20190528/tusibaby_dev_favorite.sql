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
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `retailer_id` int(11) NOT NULL COMMENT '零售商ID',
  `item_id` bigint(32) DEFAULT NULL COMMENT '商品ID',
  `retail_price` int(11) NOT NULL COMMENT '零售价格',
  `title` varchar(128) NOT NULL COMMENT '商品标题',
  `pic_url` varchar(256) NOT NULL COMMENT '商品主图URL',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (8,2,100,1,'母婴商品','//cdn.tusibaby.com/o_1b1jigcebu4r16741duu1hua6ju.jpg'),(9,2,481,219,'TRANSFORMER XT PRO 变形金刚','//cdn.tusibaby.com/o_1b1jigcebu4r16741duu1hua6ju.jpg'),(10,2,482,150,'测试','//cdn.tusibaby.com/o_1b1jigcebu4r16741duu1hua6ju.jpg'),(11,4,488,29,'儿童牙胶 Rassel mit Beissring mit beweglichen Elementen','//cdn.tusibaby.com/o_1bll52av413pk1u0u1c4jqg647ch.jpg'),(12,4,485,80,'喜宝BIO有机配方奶粉','//cdn.tusibaby.com/o_1bll3v8b01fkq15rpau51nm7av1s.jpg'),(13,5,487,200,'Premium Protection系列特级棉柔纸尿裤','//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg');
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-28 21:23:31
