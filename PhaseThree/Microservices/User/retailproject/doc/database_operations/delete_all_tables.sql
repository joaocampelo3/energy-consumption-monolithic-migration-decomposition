SELECT CONCAT('DROP TABLE IF EXISTS `',table_schema, '`.`', table_name, '`;')
  FROM information_schema.tables
 WHERE table_schema = 'dummy';

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS dummy.categories;
DROP TABLE IF EXISTS dummy.customers;
DROP TABLE IF EXISTS dummy.items;
DROP TABLE IF EXISTS dummy.item_quantities;
DROP TABLE IF EXISTS dummy.merchant_orders;
DROP TABLE IF EXISTS dummy.merchants;
DROP TABLE IF EXISTS dummy.orders;
DROP TABLE IF EXISTS dummy.orders_item_quantities;
DROP TABLE IF EXISTS dummy.payments;
DROP TABLE IF EXISTS dummy.shipping_orders;
DROP TABLE IF EXISTS dummy.accounts;
DROP TABLE IF EXISTS dummy.addresses;
SET FOREIGN_KEY_CHECKS = 1;