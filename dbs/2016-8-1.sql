CREATE TABLE `foot_mark` (
  `id` bigint(32) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(32) NOT NULL,
  `item_id` bigint(32) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;