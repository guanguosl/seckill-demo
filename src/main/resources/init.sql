/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50639
Source Host           : localhost:3306
Source Database       : miaosha

Target Server Type    : MYSQL
Target Server Version : 50639
File Encoding         : 65001

Date: 2019-01-24 00:01:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` bigint(20) NOT NULL COMMENT '商品ID',
  `goods_name` varchar(32) DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(128) DEFAULT NULL COMMENT '商品图片',
  `goods_detail` longtext COMMENT '商品详情介绍',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品单价',
  `goods_stock` int(11) DEFAULT NULL COMMENT '商品库存，-1表示没有限制',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品表';

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('1', 'iphoneX', '苹果iPhone X（全网通）', '/img/iphonex.png', '苹果iPhone X（全网通）', '6299.00', '10000');
INSERT INTO `goods` VALUES ('2', 'mate10', '华为Mate 10 Pro（全网通）', '/img/meta10.png', '华为Mate 10 Pro（全网通）', '3599.00', '-1');

-- ----------------------------
-- Table structure for miaosha_goods
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_goods`;
CREATE TABLE `miaosha_goods` (
  `id` bigint(20) NOT NULL COMMENT '秒杀商品ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品名称',
  `miaosha_price` decimal(10,2) DEFAULT NULL COMMENT '秒杀商品单价',
  `stock_count` int(11) DEFAULT NULL COMMENT '秒杀库存',
  `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀商品表';

-- ----------------------------
-- Records of miaosha_goods
-- ----------------------------
INSERT INTO `miaosha_goods` VALUES ('1', '1', '0.01', '4', '2019-01-23 15:57:01', '2019-01-30 15:57:06');
INSERT INTO `miaosha_goods` VALUES ('2', '2', '0.01', '10', '2019-01-23 15:57:01', '2019-01-30 15:57:06');

-- ----------------------------
-- Table structure for miaosha_order
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_order`;
CREATE TABLE `miaosha_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='秒杀订单表';

-- ----------------------------
-- Records of miaosha_order
-- ----------------------------
INSERT INTO `miaosha_order` VALUES ('8', '15088695596', '1', '2');

-- ----------------------------
-- Table structure for miaosha_user
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_user`;
CREATE TABLE `miaosha_user` (
  `id` bigint(20) NOT NULL COMMENT '用户ID',
  `nickname` varchar(256) DEFAULT NULL COMMENT '昵称',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `salt` varchar(10) DEFAULT NULL COMMENT 'key',
  `head` varchar(50) DEFAULT NULL COMMENT '头像',
  `register_date` date DEFAULT NULL COMMENT '注册四件',
  `last_login_date` date DEFAULT NULL COMMENT '上次登陆时间',
  `login_count` int(11) DEFAULT NULL COMMENT '登陆次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息';

-- ----------------------------
-- Records of miaosha_user
-- ----------------------------
INSERT INTO `miaosha_user` VALUES ('15088695596', 'guanyl', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c4d', null, '2019-01-23', '2019-01-23', '0');

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `delivery_addr_id` bigint(20) DEFAULT NULL COMMENT '收货地址ID',
  `goods_name` varchar(32) DEFAULT NULL COMMENT '商品名称',
  `goods_count` int(11) DEFAULT NULL COMMENT '商品数量',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品单价',
  `order_channel` tinyint(4) DEFAULT NULL COMMENT '订单来源：1PC,2Android,3IOS',
  `status` tinyint(4) DEFAULT NULL COMMENT '订单状态：0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date` datetime DEFAULT NULL COMMENT '订单创建时间',
  `pay_date` datetime DEFAULT NULL COMMENT '订单支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='订单表';

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('2', '15088695596', '2', null, 'mate10', '1', '0.01', '1', '0', '2019-01-23 23:20:11', null);
INSERT INTO `order_info` VALUES ('3', '15088695596', '2', null, 'mate10', '1', '0.01', '1', '0', '2019-01-23 23:23:31', null);
INSERT INTO `order_info` VALUES ('4', '15088695596', '2', null, 'mate10', '1', '0.01', '1', '0', '2019-01-23 23:32:19', null);
INSERT INTO `order_info` VALUES ('5', '15088695596', '2', null, 'mate10', '1', '0.01', '1', '0', '2019-01-23 23:33:19', null);
INSERT INTO `order_info` VALUES ('6', '15088695596', '2', null, 'mate10', '1', '0.01', '1', '0', '2019-01-23 23:33:43', null);
INSERT INTO `order_info` VALUES ('7', '15088695596', '2', null, 'mate10', '1', '0.01', '1', '0', '2019-01-23 23:39:28', null);
INSERT INTO `order_info` VALUES ('8', '15088695596', '2', null, 'mate10', '1', '0.01', '1', '0', '2019-01-23 23:40:01', null);
INSERT INTO `order_info` VALUES ('9', '15088695596', '2', null, 'mate10', '1', '0.01', '1', '0', '2019-01-23 23:42:37', null);
