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
-- Table structure for table `item_cate`
--

DROP TABLE IF EXISTS `item_cate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_cate` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '宝贝类目ID(主键)',
  `name` varchar(64) NOT NULL COMMENT '宝贝类目名称',
  `parent_cid` bigint(32) NOT NULL COMMENT '父级ID',
  `is_parent` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否父级类目',
  `ordinal` int(11) NOT NULL DEFAULT '0' COMMENT '同级兄弟中自己的展示顺序',
  `level` int(11) NOT NULL DEFAULT '0' COMMENT '类目级别',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_cate`
--

LOCK TABLES `item_cate` WRITE;
/*!40000 ALTER TABLE `item_cate` DISABLE KEYS */;
INSERT INTO `item_cate` VALUES (1,'奶粉',-9999,0,6,1,'2017-07-22 16:36:20'),(2,'奶瓶',-9999,0,5,1,'2017-07-22 16:36:38'),(3,'纸尿裤',-9999,0,4,1,'2017-07-22 16:36:49'),(4,'牙胶',-9999,0,3,1,'2017-07-22 20:37:22'),(5,'推车',-9999,0,1,1,'2017-10-22 16:57:21');
/*!40000 ALTER TABLE `item_cate` ENABLE KEYS */;
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
