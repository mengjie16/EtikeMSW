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
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT COMMENT '自增ID主键',
  `title` varchar(128) NOT NULL COMMENT '商品标题',
  `product_id` bigint(32) NOT NULL COMMENT '商品ID',
  `supplier_id` int(11) NOT NULL COMMENT '供应商ID',
  `brand_id` bigint(64) DEFAULT NULL COMMENT '品牌ID',
  `cate_id` bigint(32) NOT NULL COMMENT '商品类目ID',
  `pic_url` varchar(256) NOT NULL COMMENT '商品主图URL',
  `num` int(32) NOT NULL COMMENT '商品数量',
  `origin` varchar(64) NOT NULL COMMENT '商品产地',
  `out_no` varchar(64) NOT NULL COMMENT '商品货号',
  `unit` varchar(32) DEFAULT NULL COMMENT '计量单位',
  `retail_price` decimal(10,2) DEFAULT NULL,
  `supply_price` decimal(10,2) DEFAULT NULL,
  `net_weight` decimal(10,2) DEFAULT NULL COMMENT '净重量',
  `gross_weight` decimal(10,2) DEFAULT NULL COMMENT '毛重量',
  `quality` int(11) DEFAULT '1000' COMMENT '品质系数',
  `initial_quantity` int(11) DEFAULT '0' COMMENT '初始销量',
  `sold_quantity` int(11) DEFAULT NULL COMMENT '总销量',
  `mobile_detail` mediumtext COMMENT '手机详情',
  `detail` mediumtext COMMENT '详情描述',
  `status` enum('AUDITING','FAIL_AUDITING','ONLINE','HIDE','DELETED') DEFAULT NULL COMMENT '商品状态',
  `dist_price` decimal(10,2) DEFAULT NULL,
  `dist_price_use` tinyint(2) NOT NULL DEFAULT '1' COMMENT '代销价格是否应用',
  `properties` json DEFAULT NULL COMMENT '商品的各项SKU属性，风格，承重类似',
  `refer_urls` json DEFAULT NULL,
  `price_ranges` json DEFAULT NULL COMMENT '商品批发价格区间',
  `price_range_use` tinyint(2) NOT NULL DEFAULT '1' COMMENT '批发价格区间是否应用',
  `freight_temp` bigint(32) DEFAULT '0' COMMENT '运费模板id',
  `skus` json DEFAULT NULL COMMENT '商品规格属性',
  `note` varchar(256) DEFAULT NULL COMMENT '宝贝备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=494 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (100,'母婴商品33',0,1,1,1,'//cdn.tusibaby.com/o_1blkq1n925gs3g215ejp791sdrh.jpg',4,'中国','20170723001','件',8860.00,0.00,1.30,2.00,1000,0,NULL,NULL,'<p>								</p><p>123</p><p>\r\n							</p>','ONLINE',NULL,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1blkq1n925gs3g215ejp791sdrh.jpg\", \"color\": \"红色\", \"quantity\": 12}]',NULL,NULL,NULL),(480,'321321323',1001,2003,2,2,'//cdn.tusibaby.com/o_1b1jigcebu4r16741duu1hua6ju.jpg',10,'中国','20161115002','辆',89900.00,30000.00,3.50,4.80,8000,0,0,NULL,NULL,'DELETED',58900.00,1,NULL,NULL,NULL,1,0,NULL,NULL,NULL,NULL),(481,'TRANSFORMER XT PRO 变形金刚',1002,2003,3,3,'//cdn.tusibaby.com/o_1aurd69n41ed71umoc3u1sm118ie1g.png',10,'中国','20161115002','辆',21900.00,33000.00,2.50,4.80,8000,0,0,NULL,NULL,'ONLINE',58900.00,1,NULL,NULL,NULL,1,0,NULL,NULL,NULL,NULL),(482,'测试',1003,2003,3,4,'//cdn.tusibaby.com/o_1b1jigcebu4r16741duu1hua6ju.jpg',5,'中国','20161115002','辆',15000.00,4000.00,1.50,4.80,8000,0,0,NULL,NULL,'ONLINE',58900.00,1,NULL,NULL,NULL,1,0,NULL,NULL,NULL,NULL),(483,'安尔乐纸尿裤',0,0,1,3,'//cdn.tusibaby.com/o_1blkrrrej1ajk8ogrkr1evtr8kh.jpg',0,'中国','12333','包',0.00,0.00,12.00,12.00,0,0,0,NULL,'<p>								</p><p>12</p><p>\r\n							</p>','HIDE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1blkrrrej1ajk8ogrkr1evtr8kh.jpg\", \"color\": \"L\", \"quantity\": 13}]','12','2017-07-22 18:01:11','2017-07-22 18:01:11'),(484,'asssssssss',0,0,1,3,'//cdn.tusibaby.com/o_1bll2rf875vs7astisja2coih.jpg',0,'中国','1212','a',0.00,0.00,10.00,12.00,0,0,0,NULL,'<p>								</p><p>1212</p><p>\r\n							</p>','DELETED',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1bll2rf875vs7astisja2coih.jpg\", \"color\": \"ass\", \"quantity\": 12}]','122','2017-07-22 20:03:23','2017-07-22 20:03:23'),(485,'喜宝BIO有机配方奶粉 Hipp Öl',0,0,5,1,'//cdn.tusibaby.com/o_1bll3tibcq43n8m12le1q64biah.jpg',0,'德国','20170722001','盒',8000.00,0.00,0.60,1.00,0,0,0,NULL,'<p>								</p><h2 style=\"box-sizing: inherit; margin: 0.2rem 0px 0.5rem; padding: 0px; color: rgb(102, 102, 102); font-family: Arial, Helvetica, sans-serif; line-height: 1.4; text-rendering: optimizeLegibility; font-size: 1.25rem; white-space: normal; background-color: rgb(255, 255, 255);\">HiPP 喜宝有机出生段奶粉 Pre段 (600 g), 1 盒</h2><p><br/></p><p>Öl</p><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">50年来，HiPP喜宝把所有关怀和经验，致力于生产最好的婴幼儿配方奶粉。作为有机婴幼儿产品的先锋，多年来在婴幼儿食品生产过程中，坚持使用最优质的有机原料。并且坚信有机质量是婴幼儿健康成长不可或缺的重要保证。HiPP喜宝有机奶粉1段为宝宝提供过渡到辅食阶段以及健康成长所需的所有营养物质。</p><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">HiPP喜宝出生段奶粉完全符合婴儿0至6个月期间的营养需求。适用于混合喂养或纯配方奶粉喂养。 HiPP喜宝有机出生段奶粉Pre段选用最优质的天然原料生产。</p><ul style=\"box-sizing: inherit; margin-bottom: 1.66667rem; margin-left: 1.1rem; padding: 0px; list-style-position: initial; list-style-image: initial; font-family: Arial, Helvetica, sans-serif; font-size: 12px; line-height: 1.6; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\" class=\" list-paddingleft-2\"><li><p>HiPP喜宝有机奶粉Pre段易消化，适合初生婴儿的特殊营养需求。</p></li><li><p>在绿色和平组织公布的&quot;无转基因食品&quot;选购指引中获得 &quot;推荐&quot;荣誉。</p></li></ul><h2 style=\"box-sizing: inherit; margin: 0.2rem 0px 0.5rem; padding: 0px; color: rgb(102, 102, 102); font-family: Arial, Helvetica, sans-serif; line-height: 1.4; text-rendering: optimizeLegibility; font-size: 1.25rem; white-space: normal; background-color: rgb(255, 255, 255);\">HiPP 喜宝BIO有机出生段奶粉 1段 (600 g), 1 盒</h2><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">50年来，HiPP喜宝把所有关怀和经验，致力于生产最好的婴幼儿配方奶粉。作为有机婴幼儿产品的先锋，多年来在婴幼儿食品生产过程中，坚持使用最优质的有机原料。并且坚信有机质量是婴幼儿健康成长不可或缺的重要保证。HiPP喜宝有机奶粉1段为宝宝提供过渡到辅食阶段以及健康成长所需的所有营养物质。</p><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">HiPP喜宝出生段奶粉完全符合婴儿0至6个月期间的营养需求。适用于混合喂养或纯配方奶粉喂养。 HiPP喜宝有机出生段奶粉Pre段选用最优质的天然原料生产。</p><ul style=\"box-sizing: inherit; margin-bottom: 1.66667rem; margin-left: 1.1rem; padding: 0px; list-style-position: initial; list-style-image: initial; font-family: Arial, Helvetica, sans-serif; font-size: 12px; line-height: 1.6; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\" class=\" list-paddingleft-2\"><li><p>HiPP喜宝产品采用经严格控制的有机原料生产，值得信赖。所选原料的品质远远超过了婴幼儿配方奶粉严格的法律规定标准。</p></li><li><p>LCP (Omega 3&amp;6): 长链多不饱和脂肪酸。该成分对宝宝最初几个月来说非常重要，因为宝宝在这一阶段自己身体还无法产生足够的LCPs。</p></li><li><p>在绿色和平组织公布的&quot;无转基因食品&quot;选购指引中获得 &quot;推荐&quot;荣誉。</p></li></ul><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\"><span style=\"box-sizing: inherit; font-weight: 700; line-height: inherit;\">使用说明:</span></p><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">作为新生宝宝的唯一食物来源或补充母乳的不足。与母乳一样可以根据需要不限次数不限量地给宝宝饮用。</p><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\"><span style=\"box-sizing: inherit; font-weight: 700; line-height: inherit;\">建议年龄:</span>&nbsp;出生起</p><p><br/><img src=\"//cdn.tusibaby.com/o_1bll433fupep12ai11jp1vuh186j11.jpg\"/></p><p>\r\n							</p>','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1bll3tibcq43n8m12le1q64biah.jpg\", \"color\": \"出生段奶粉 Pre段 (600 g), 1 盒\", \"quantity\": 100}, {\"img\": \"//cdn.tusibaby.com/o_1bll3v8b01fkq15rpau51nm7av1s.jpg\", \"color\": \"BIO有机出生段奶粉 1段 (600 g), 1 盒\", \"quantity\": 100}]','','2017-07-22 20:24:33','2017-07-22 20:24:33'),(486,'努克First Choice + PP婴儿奶瓶',0,0,6,2,'//cdn.tusibaby.com/o_1bll49m9mvi518e6klj1i6n1230h.jpg',0,'德国','20170722002','个',4000.00,0.00,0.40,1.00,0,0,0,NULL,'<h2 style=\"box-sizing: inherit;margin: 0.2rem 0px 0.5rem;padding: 0px;color: rgb(102, 102, 102);font-family: Arial, Helvetica, sans-serif;line-height: 1.4;text-rendering: optimizeLegibility;font-size: 1.25rem;white-space: normal;background-color: rgb(255, 255, 255)\">NUK First Choice + Babyflasche PP 150ml, Silikon-Trinksauger, 0-6 Monate M, beige (10215208)</h2><p style=\"box-sizing: inherit;margin-top: 0px;margin-bottom: 1.66667rem;padding: 0px;font-family: Arial, Helvetica, sans-serif;font-size: 1.08333rem;text-rendering: optimizeLegibility;line-height: inherit;color: rgb(102, 102, 102);white-space: normal;background-color: rgb(255, 255, 255)\">- Besonders weicher, kiefergerechter Silikon-Trinksauger 0-6 Monate M (mittelfein gelocht = für Milchnahrung)</p><p style=\"box-sizing: inherit;margin-top: 0px;margin-bottom: 1.66667rem;padding: 0px;font-family: Arial, Helvetica, sans-serif;font-size: 1.08333rem;text-rendering: optimizeLegibility;line-height: inherit;color: rgb(102, 102, 102);white-space: normal;background-color: rgb(255, 255, 255)\">- Klinisch getestet**: Für eine optimale Kombination von Stillen und Flaschenfütterung</p><p style=\"box-sizing: inherit;margin-top: 0px;margin-bottom: 1.66667rem;padding: 0px;font-family: Arial, Helvetica, sans-serif;font-size: 1.08333rem;text-rendering: optimizeLegibility;line-height: inherit;color: rgb(102, 102, 102);white-space: normal;background-color: rgb(255, 255, 255)\">- Beugt Koliken vor: verbessertes Anti-Colic Air System</p><p style=\"box-sizing: inherit;margin-top: 0px;margin-bottom: 1.66667rem;padding: 0px;font-family: Arial, Helvetica, sans-serif;font-size: 1.08333rem;text-rendering: optimizeLegibility;line-height: inherit;color: rgb(102, 102, 102);white-space: normal;background-color: rgb(255, 255, 255)\">- Aus hochwertigem Polypropylen (PP), widerstandsfähig und zum Einfrieren geeignet, BPA frei</p><p style=\"box-sizing: inherit;margin-top: 0px;margin-bottom: 1.66667rem;padding: 0px;font-family: Arial, Helvetica, sans-serif;font-size: 1.08333rem;text-rendering: optimizeLegibility;line-height: inherit;color: rgb(102, 102, 102);white-space: normal;background-color: rgb(255, 255, 255)\">- Farbe: beige, 150 ml Inhalt</p><p style=\"box-sizing: inherit;margin-top: 0px;margin-bottom: 1.66667rem;padding: 0px;font-family: Arial, Helvetica, sans-serif;font-size: 1.08333rem;text-rendering: optimizeLegibility;line-height: inherit;color: rgb(102, 102, 102);white-space: normal;background-color: rgb(255, 255, 255)\"><span style=\"box-sizing: inherit;font-weight: 700;line-height: inherit\">Der natürlichste Trinksauger für Ihr Baby</span>&nbsp;<br style=\"box-sizing: inherit\"/>NUK First Choice ist seit vielen Jahren die erste Wahl für eine Flaschenernährung nach dem Vorbild des Stillens. Mit NUK First Choice&nbsp;<span style=\"box-sizing: inherit;font-size: 12px;line-height: 0;position: relative;vertical-align: baseline;top: -0.5em\">+</span>&nbsp;ist es gelungen, der Natur noch näher zu kommen – für eine gesunde Kieferentwicklung und ein Trinkgefühl wie an der Mutterbrust.</p><p style=\"box-sizing: inherit;margin-top: 0px;margin-bottom: 1.66667rem;padding: 0px;font-family: Arial, Helvetica, sans-serif;font-size: 1.08333rem;text-rendering: optimizeLegibility;line-height: inherit;color: rgb(102, 102, 102);white-space: normal;background-color: rgb(255, 255, 255)\"><span style=\"box-sizing: inherit;font-weight: 700;line-height: inherit\">Natürlich und kiefergerecht</span>&nbsp;<br style=\"box-sizing: inherit\"/>Die spezielle NUK Form ist der Mutterbrust beim Stillen nachempfunden. Sie lässt der Zunge und dem Kiefer Ihres Babys ausreichend Platz für die natürliche Saugbewegung.</p><p style=\"box-sizing: inherit;margin-top: 0px;margin-bottom: 1.66667rem;padding: 0px;font-family: Arial, Helvetica, sans-serif;font-size: 1.08333rem;text-rendering: optimizeLegibility;line-height: inherit;color: rgb(102, 102, 102);white-space: normal;background-color: rgb(255, 255, 255)\"><span style=\"box-sizing: inherit;font-weight: 700;line-height: inherit\">Weich wie die Mutterbrust</span>&nbsp;<br style=\"box-sizing: inherit\"/>Durch die neue Softzone passt sich das Lutschteil aus Silikon dem Gaumen noch besser an – für ein natürliches Trinkgefühl.</p><p><br/><img src=\"//cdn.tusibaby.com/o_1bll4c56r1pos1mhj164v134dp0vm.jpg\"/></p>','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1bll49m9mvi518e6klj1i6n1230h.jpg\", \"color\": \"150ml, Silikon-Trinksauger, 0-6 Monate M, beige \", \"quantity\": 100}]','','2017-07-22 20:29:30','2017-07-22 20:29:30'),(487,'Premium Protection系列特级棉柔纸尿裤',0,0,7,3,'//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg',0,'德国','20170722003','袋',20000.00,0.00,1.60,2.00,0,0,0,NULL,'<p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">Mit den Pampers Premium Protection Windeln unterstützen Eltern Babys Wohlbefinden. Bis vor kurzem unter dem Namen &quot;Pampers New Baby&quot; im Handel, schützen sie Tag und Nacht den Po zuverlässig vor Feuchtigkeit. Jetzt bietet die Traditionsmarke Pampers die seit vielen Jahren sehr beliebten Windeln nicht mehr nur für kleine Babys, sondern auch für Kleinkinder mit einem Gewicht von bis zu 23 kg an.</p><h2 style=\"box-sizing: inherit; margin: 0.2rem 0px 0.5rem; padding: 0px; color: rgb(102, 102, 102); font-family: Arial, Helvetica, sans-serif; line-height: 1.4; text-rendering: optimizeLegibility; font-size: 1.25rem; white-space: normal; background-color: rgb(255, 255, 255);\">Pampers Premium Protection: für angenehme Trockenheit rund um die Uhr</h2><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">Mit ihrem innovativen Saugkern halten diese Windeln die sanfte Babyhaut für bis zu zwölf Stunden trocken. Damit helfen sie, Wundsein und Hautirritationen vorzubeugen und stellen eine gute Wahl für die Nachtstunden dar. Die exklusiv von Pampers entwickelten Magical Pods verfügen über drei Kanäle, welche die Feuchtigkeit gleichmäßig in das Windelinnere weiterleiten. Dank dem leistungsfähigen Doppel-Absorbiersystem nehmen die Windeln sowohl Urin als auch weichen Stuhl zuverlässig auf.</p><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">Der Urin-Indikator (Bei Windeln bis Größe 5) erspart Eltern überflüssiges Wickeln: Erst wenn er sich blau färbt, ist es an der Zeit für einen Windelwechsel. Für optimalen Auslaufschutz sollten Eltern darauf achten, dass sie Windeln der richtigen Größe wählen: Die Premium Protection Windeln von Pampers sind in acht Größen erhältlich: Während Größe 1 bereits für Neugeborene ab einem Gewicht von 2 kg geeignet ist, passen Windeln der Größe 6 Kleinkindern ab 15 kg.</p><h2 style=\"box-sizing: inherit; margin: 0.2rem 0px 0.5rem; padding: 0px; color: rgb(102, 102, 102); font-family: Arial, Helvetica, sans-serif; line-height: 1.4; text-rendering: optimizeLegibility; font-size: 1.25rem; white-space: normal; background-color: rgb(255, 255, 255);\">Hoher Tragekomfort in jeder Wachstumsphase</h2><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">Ihr seidenweiches Material macht die Premium Protection Windeln besonders anschmiegsam. Zur bequemen Passform tragen die dehnbaren Seitenbündchen bei: Ob beim lebhaften Strampeln oder den ersten Krabbelversuchen - sie passen sich flexibel an die Bewegungen des Kindes an und verhindern ein Verrutschen. Windeln der Größen 1 und 2 verfügen über einen Nabelausschnitt, damit sie während des Abheilprozesses nicht unangenehm am empfindlichen Nabel scheuern.</p><h2 style=\"box-sizing: inherit; margin: 0.2rem 0px 0.5rem; padding: 0px; color: rgb(102, 102, 102); font-family: Arial, Helvetica, sans-serif; line-height: 1.4; text-rendering: optimizeLegibility; font-size: 1.25rem; white-space: normal; background-color: rgb(255, 255, 255);\">Pampers Premium Protection im Überblick</h2><ul style=\"box-sizing: inherit; margin-bottom: 1.66667rem; margin-left: 1.1rem; padding: 0px; list-style-position: initial; list-style-image: initial; font-family: Arial, Helvetica, sans-serif; font-size: 12px; line-height: 1.6; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\" class=\" list-paddingleft-2\"><li><p>halten Babys Po für bis zu 12 Stunden trocken</p></li><li><p>Doppel-Absorbiersystem auch für weichen Stuhl</p></li><li><p>mit praktischem Urin-Indikator (nur Windeln bis Größe 5)</p></li><li><p>bequeme Passform mit flexiblen Seitenbündchen</p></li><li><p>Größe 1 und 2 mit Nabelausschnitt</p></li></ul><p><br/><img src=\"//cdn.tusibaby.com/o_1bll4neih1le1b0r172c1914ktvm.jpg\"/></p>','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1bll4jhnjd8jubi1nlo10fj45mh.jpg\", \"color\": \"New Baby, 新生儿经济装, 尺寸 1, 4 袋x 22 片\", \"quantity\": 100}]','','2017-07-22 20:35:46','2017-07-22 20:35:46'),(488,'儿童牙胶 Rassel mit Beissring mit beweglichen Elementen1',0,0,8,4,'//cdn.tusibaby.com/o_1bll52av413pk1u0u1c4jqg647ch.jpg',0,'德国','201707220041','支',29100.00,0.00,0.11,1.01,0,1,0,NULL,'<p>								</p><h2 style=\"box-sizing: inherit; margin: 0.2rem 0px 0.5rem; padding: 0px; color: rgb(102, 102, 102); font-family: Arial, Helvetica, sans-serif; line-height: 1.4; text-rendering: optimizeLegibility; font-size: 1.25rem; white-space: normal; background-color: rgb(255, 255, 255);\">Fashy 儿童牙胶 Rassel mit Beissring mit beweglichen Elementen</h2><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\">Fashy 儿童牙胶 Rassel mit Beissring mit beweglichen Elementen让孩子们的探索潜能得到激发。该玩具适合 3 个月以上的小朋友。其净重为0.2公斤。选对玩具让孩子更开心！婴幼儿磨牙咬牙胶有助于缓解出牙期的牙龈不适。现在订购Fashy 儿童牙胶 Rassel mit Beissring mit beweglichen Elementen，把对的玩具买回家。</p><p style=\"box-sizing: inherit; margin-top: 0px; margin-bottom: 1.66667rem; padding: 0px; font-family: Arial, Helvetica, sans-serif; font-size: 1.08333rem; text-rendering: optimizeLegibility; line-height: inherit; color: rgb(102, 102, 102); white-space: normal; background-color: rgb(255, 255, 255);\"><span style=\"box-sizing: inherit; font-weight: 700; line-height: inherit;\">警告：注意！不适合3个月以下的儿童使用1</span></p><p><br/><img src=\"//cdn.tusibaby.com/o_1bll52hnd4ab8gigjncmp1muam.jpg\"/></p><p>\r\n							</p>','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1bll52av413pk1u0u1c4jqg647ch.jpg\", \"color\": \"mit beweglichen Elementen 1\", \"quantity\": 1001}]','1','2017-07-22 20:41:41','2017-07-22 20:41:41'),(489,'费雪儿童三轮车',0,0,1,5,'//cdn.tusibaby.com/o_1bt1l992i16j8ltg9j28et16m5h.jpg',0,'德国','20171022001','辆',89000.00,0.00,7.50,10.00,0,0,0,NULL,'<p><br/><img src=\"//cdn.tusibaby.com/o_1bt1lafe1o7i1ttvf9qajg1tcqm.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lanbk19r7jk81np9o9515hi12.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lanbkl9ol0pnqk6om7pp13.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lanbk2dbd9o7ecpeu100l14.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lanbk6551mhojft2v81m7g15.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lanbk159s2ps1dto1gre1iv016.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lanbk1m931dc21c77h851bfp17.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lanbl111q8sboi31gqn1n5q18.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lanbl1gia1tvmart1blmfq19.jpg\"/></p>','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1bt1l992i16j8ltg9j28et16m5h.jpg\", \"color\": \"红色\", \"quantity\": 20}]','','2017-10-22 17:06:37','2017-10-22 17:06:37'),(490,'测试用测试用',0,0,1,5,'//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg',0,'德国','20171022002','辆',89000.00,0.00,7.50,10.00,0,0,0,NULL,'<p>								</p><p><br/></p><p><br/><img src=\"//cdn.tusibaby.com/o_1bt1lu23r98210kc1nen11f9tpan.jpg\"/><br/><img src=\"//cdn.tusibaby.com/o_1bt1lu23r8b1c4v2qis3rfvlo.jpg\"/></p><p><br/></p><p>\r\n							</p>','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1bt1lt2rp1560a4f1citjb8k52h.jpg\", \"color\": \"蓝色\", \"quantity\": 10}]','','2017-10-22 17:17:21','2017-10-22 17:17:21'),(491,'德国产品的Prüfung',0,0,6,0,'//cdn.tusibaby.com/o_1bu220h8cl0h1ca0o0mafc1gclh.jpg',0,'德国','3452146743257','个',596.00,1345.00,0.80,1.20,0,10,0,NULL,'','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1bu220h8cl0h1ca0o0mafc1gclh.jpg\", \"color\": \"89\", \"quantity\": 90}]','','2017-11-04 07:03:57','2017-11-04 07:03:57'),(492,'测试专用测试专用',0,0,1,5,'//cdn.tusibaby.com/o_1c0t3lpda198g1b36ao81bt61nbsh.jpg',0,'德国','20171209001','辆',100000.00,120000.00,0.00,0.00,0,1,0,NULL,'<p>								</p><p><br/><img src=\"//cdn.tusibaby.com/o_1c0t3nggnq88oh7mtkvid5oo1c.jpg\"/></p><p>\r\n							</p>','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1c0t3lpda198g1b36ao81bt61nbsh.jpg\", \"color\": \"红\", \"quantity\": 10}, {\"img\": \"//cdn.tusibaby.com/o_1c0t3m6mt1crg1avf1fjj1lc31nfes.jpg\", \"color\": \"绿\", \"quantity\": 10}, {\"img\": \"//cdn.tusibaby.com/o_1c0t3mj9i7ll1vu1sprn0f16s717.jpg\", \"color\": \"蓝\", \"quantity\": 10}]','','2017-12-09 15:44:47','2017-12-09 15:44:47'),(493,'123456',0,0,1,5,'//cdn.tusibaby.com/o_1c0t7929jl9c183j1k6683u1ue4h.jpg',0,'德国','20171209002','辆',120689.00,269989.00,0.00,0.00,0,2,0,NULL,'<p><br/><img src=\"//cdn.tusibaby.com/o_1c0t7ai8r17osktjf3j1ne73etm.jpg\"/></p>','ONLINE',0.00,0,NULL,NULL,NULL,0,0,'[{\"img\": \"//cdn.tusibaby.com/o_1c0t7929jl9c183j1k6683u1ue4h.jpg1\", \"color\": 123, \"quantity\": 12}]','','2017-12-09 16:48:59','2017-12-09 16:48:59');
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
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