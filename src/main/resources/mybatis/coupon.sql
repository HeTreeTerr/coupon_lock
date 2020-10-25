/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : coupon

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2020-10-26 00:10:31
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for coupon_class
-- ----------------------------
DROP TABLE IF EXISTS `coupon_class`;
CREATE TABLE `coupon_class` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(255) NOT NULL,
  `number` int(11) NOT NULL,
  `secret_key` varchar(255) NOT NULL COMMENT '秘钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of coupon_class
-- ----------------------------
INSERT INTO `coupon_class` VALUES ('1', '和其正', '10', 'HEQIZHENG');

-- ----------------------------
-- Table structure for coupon_record
-- ----------------------------
DROP TABLE IF EXISTS `coupon_record`;
CREATE TABLE `coupon_record` (
  `id` int(11) NOT NULL COMMENT '编号',
  `class_id` int(11) NOT NULL COMMENT '优惠券编号',
  `user_name` varchar(255) NOT NULL COMMENT '参与人名称',
  `seq_no` int(11) NOT NULL COMMENT '排名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of coupon_record
-- ----------------------------
