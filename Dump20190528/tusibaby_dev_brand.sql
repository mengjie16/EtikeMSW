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
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `brand` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `name` varchar(64) NOT NULL COMMENT '品牌名称',
  `secondary_name` varchar(64) DEFAULT NULL COMMENT '品牌从名称',
  `pic_url` varchar(256) DEFAULT NULL COMMENT '品牌logo',
  `company` varchar(64) NOT NULL COMMENT '品牌最根源的公司',
  `holder` varchar(64) NOT NULL COMMENT '和平台对接',
  `note` varchar(256) DEFAULT NULL COMMENT '备注',
  `descption` varchar(256) NOT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
INSERT INTO `brand` VALUES (1,'FisherPrice','费雪','//cdn.tusibaby.com/o_1b1jigcebu4r16741duu1hua6ju.jpg','中国','中国','阿斯钢','again'),(2,'保健','啊嘎嘎','//cdn.tusibaby.com/o_1b1jigcebu4r16741duu1hua6ju.jpg','中国','中国','个','阿斯钢'),(3,'玩具','啊嘎嘎','//cdn.tusibaby.com/o_1aurd69n41ed71umoc3u1sm118ie1g.png','中国','中国','三个 ','阿萨'),(4,'爱他美','儿童三段','//cdn.tusibaby.com/o_1blkn3uhiqjk1gbcra216hc5j39.jpg','','',NULL,'好喝好喝好喝'),(5,'HiPP','喜宝','//cdn.tusibaby.com/o_1bll2u2s816ie1hq81gc3soqqr59.png','','',NULL,''),(6,'NUK','努克','//cdn.tusibaby.com/o_1bll4ak6q14k8183eh3noei1ku8e.png','','',NULL,''),(7,'Pampers','帮宝适','//cdn.tusibaby.com/o_1bll4m8dn147b1c1316gg16nl1iu7j.png','','',NULL,''),(8,'Fashy','','//cdn.tusibaby.com/o_1bll4m8dn147b1c1316gg16nl1iu7j.png','','',NULL,'');
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
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
