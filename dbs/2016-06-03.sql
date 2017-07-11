ALTER TABLE `item` 
ADD COLUMN `price_ranges` JSON NULL DEFAULT NULL AFTER `properties`;
