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
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '自增ID主键',
  `title` varchar(128) NOT NULL COMMENT '商品标题',
  `item_id` bigint(32) NOT NULL COMMENT '商品ID',
  `retailer_id` int(11) NOT NULL COMMENT '零售商ID',
  `brand_id` bigint(64) DEFAULT NULL COMMENT '品牌ID',
  `pic_url` varchar(256) NOT NULL COMMENT '商品主图URL',
  `retail_price` decimal(10,2) DEFAULT NULL,
  `cart_count` int(11) NOT NULL COMMENT '购物车数量',
  `sku_color` varchar(256) NOT NULL COMMENT '商品颜色',
  `sku_quantity` bigint(32) NOT NULL COMMENT '商品库存',
  `cart_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (19,'Premium Protection系列特级棉柔纸尿裤',487,4,7,'//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg',200.00,1,'',0,200.00),(21,'儿童牙胶 Rassel mit Beissring mit beweglichen Elementen',488,2,8,'//cdn.tusibaby.com/o_1bll52av413pk1u0u1c4jqg647ch.jpg',29.00,11,'mit beweglichen Elementen',-1,319.00),(22,'喜宝BIO有机配方奶粉',485,2,5,'//cdn.tusibaby.com/o_1bll3v8b01fkq15rpau51nm7av1s.jpg',80.00,11,'????? Pre? (600 g), 1 ?',0,880.00),(23,'喜宝BIO有机配方奶粉',485,2,5,'//cdn.tusibaby.com/o_1bll3v8b01fkq15rpau51nm7av1s.jpg',80.00,2,'BIO??????? 1? (600 g), 1 ?',0,160.00),(26,'努克First Choice + PP婴儿奶瓶',486,4,6,'//cdn.tusibaby.com/o_1bll49m9mvi518e6klj1i6n1230h.jpg',40.00,1,'150ml, Silikon-Trinksauger, 0-6 Monate M, beige',99,40.00),(35,'测试用测试用',490,5,1,'//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg',890.00,4,'蓝色',8,3560.00),(41,'测试专用测试专用',492,6,1,'//cdn.tusibaby.com/o_1c0t3mj9i7ll1vu1sprn0f16s717.jpg',1000.00,6,'绿',29,6000.00),(44,'一二三四五六',493,6,1,'//cdn.tusibaby.com/o_1c0t7929jl9c183j1k6683u1ue4h.jpg',1206.00,1,'地',11,1206.00),(47,'123456',493,18,1,'//cdn.tusibaby.com/o_1c0t7929jl9c183j1k6683u1ue4h.jpg1',1206.89,1,'123',11,1206.89),(48,'测试专用测试专用',492,18,1,'//cdn.tusibaby.com/o_1c0t3lpda198g1b36ao81bt61nbsh.jpg',1000.00,1,'绿',29,1000.00);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
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
