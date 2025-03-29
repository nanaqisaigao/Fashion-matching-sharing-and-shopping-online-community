/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50720 (5.7.20)
 Source Host           : localhost:3306
 Source Schema         : t_shop_system

 Target Server Type    : MySQL
 Target Server Version : 50720 (5.7.20)
 File Encoding         : 65001

 Date: 08/12/2024 15:59:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `image` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, '/api/upload/20241204225818.jpg', 'admin', '123', '2024-12-07 16:33:38');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gid` int(11) NULL DEFAULT NULL COMMENT '商品',
  `num` int(11) NULL DEFAULT NULL COMMENT '数量',
  `uid` int(11) NULL DEFAULT NULL COMMENT '用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '购物车' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1, 1, 2, 1, '2024-12-06 22:24:56');
INSERT INTO `cart` VALUES (8, 8, 1, 2, '2024-12-07 15:34:25');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '服装', '服装', '2024-12-04 23:02:30');
INSERT INTO `category` VALUES (2, '配饰', '配饰', '2024-12-04 23:02:50');
INSERT INTO `category` VALUES (3, '包包', '包包', '2024-12-04 23:02:58');
INSERT INTO `category` VALUES (4, '鞋子', NULL, '2024-12-08 15:30:52');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `fid` int(11) NULL DEFAULT NULL COMMENT '帖子',
  `uid` int(11) NULL DEFAULT NULL COMMENT '评论用户',
  `content` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论内容',
  `reply` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回复内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '帖子评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 3, 2, '测试', '3', '2024-12-07 07:59:01');
INSERT INTO `comment` VALUES (2, 2, 3, '真好啊', '谢谢', '2024-12-07 10:44:31');
INSERT INTO `comment` VALUES (3, 2, 4, '好看', '谢谢', '2024-12-08 15:48:45');

-- ----------------------------
-- Table structure for discuss
-- ----------------------------
DROP TABLE IF EXISTS `discuss`;
CREATE TABLE `discuss`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `oid` int(11) NULL DEFAULT NULL COMMENT '穿搭',
  `uid` int(11) NULL DEFAULT NULL COMMENT '用户',
  `content` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论内容',
  `reply` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回复内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '评论时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '穿搭评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of discuss
-- ----------------------------
INSERT INTO `discuss` VALUES (1, 1, 1, '11', '1', '2024-12-05 21:36:56');
INSERT INTO `discuss` VALUES (2, 1, 1, '333', NULL, '2024-12-05 21:37:26');
INSERT INTO `discuss` VALUES (3, 5, 1, '测试', '你好', '2024-12-06 22:08:55');
INSERT INTO `discuss` VALUES (4, 7, 2, '好看', NULL, '2024-12-07 15:56:53');
INSERT INTO `discuss` VALUES (5, 7, 4, '好看', NULL, '2024-12-08 15:41:23');

-- ----------------------------
-- Table structure for forum
-- ----------------------------
DROP TABLE IF EXISTS `forum`;
CREATE TABLE `forum`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容',
  `uid` int(11) NULL DEFAULT NULL COMMENT '发布人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '帖子表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of forum
-- ----------------------------
INSERT INTO `forum` VALUES (2, '分享一套春季穿搭', '<p>今天给大家分享一套非常适合春季的穿搭，简约又时尚。这套搭配采用了浅色系的配色方案，既清新又不失时尚感。</p><p><br></p><p><img src=\"/api/upload/20241207072737.jpg\"></p>', 2, '2024-12-07 07:27:46');
INSERT INTO `forum` VALUES (3, '今日穿搭分享', '<p>分享一套适合约会的穿搭，温柔又不失时尚...</p><p><br></p><p><img src=\"/api/upload/20241207072823.jpg\"></p>', 3, '2024-12-07 07:28:08');
INSERT INTO `forum` VALUES (4, '我喜欢的穿搭风格是这样的', '<p>我喜欢的穿搭风格是这样的，你们觉得怎么样？</p><p><br></p><p><img src=\"/api/upload/20241208154805.png\"></p>', 4, '2024-12-08 15:48:15');

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cid` int(11) NULL DEFAULT NULL COMMENT '分类',
  `image` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品图片',
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `remark` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '简介',
  `money` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '价格',
  `num` int(11) NULL DEFAULT NULL COMMENT '库存',
  `content` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品介绍',
  `status` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES (1, 2, '/api/upload/20241206205521.png', 'CIGALONG龙梓嘉小葫芦', '天然绿松石', '1199', 99, '<p>CIGALONG龙梓嘉小葫芦天然绿松石项链耳钉戒指新中式珠宝圣诞礼物</p><p><br></p><p><img src=\"/api/upload/20241206205518.png\"></p>', '已发布', '2024-12-04 23:04:03');
INSERT INTO `goods` VALUES (2, 2, '/api/upload/20241206205324.png', '周大生纯银红玛瑙玉髓手链', '相思豆红豆手串送女友', '195', 200, '<p>周大生纯银红玛瑙玉髓手链新女款相思豆红豆手串送女友节日礼物</p><p><br></p><p><img src=\"/api/upload/20241206205408.png\"></p>', '已发布', '2024-12-06 20:54:10');
INSERT INTO `goods` VALUES (3, 2, '/api/upload/20241206205608.png', '王嘉尔同款双层融化星芒项链', '男士潮高级感叠戴吊坠卫衣链', '100', 49, '<p>官方正品王嘉尔同款双层融化星芒项链男士潮高级感叠戴吊坠卫衣链</p>', '已发布', '2024-12-06 20:56:34');
INSERT INTO `goods` VALUES (4, 3, '/api/upload/20241206205742.png', '保兰德PLD2024新款头层牛皮包', '秋冬枕头包真皮斜挎包腋下包', '1389', 47, '<p>保兰德PLD2024新款头层牛皮包包女款秋冬枕头包真皮斜挎包腋下包\n</p><p><br></p><p><img src=\"/api/upload/20241206205802.png\"></p>', '已发布', '2024-12-06 20:58:06');
INSERT INTO `goods` VALUES (5, 3, '/api/upload/20241206205835.png', '斐乐官方女包', '冬季新款复古老花提包', '409', 100, '<p>FILA 斐乐官方女包2024冬季新款复古老花提包休闲单肩包手提包女\n</p><p><br></p><p><img src=\"/api/upload/20241206205909.png\"></p>', '已发布', '2024-12-06 20:59:11');
INSERT INTO `goods` VALUES (6, 3, '/api/upload/20241206205953.png', '奥康腋下包2024新款', '百搭单肩包圣诞礼物', '566', 50, '<p>奥康腋下包2024新款包包高级感百搭单肩包圣诞礼物斜挎包通勤女包\n</p><p><br></p>', '已发布', '2024-12-06 21:00:15');
INSERT INTO `goods` VALUES (7, 1, '/api/upload/20241206210055.png', '新中式显瘦羊绒衫', '中长款修身毛衣秋冬新款', '394', 99, '<p>新中式显瘦羊绒衫女中长款修身毛衣秋冬新款针织打底裙鱼尾连衣裙</p><p><br></p><p><img src=\"/api/upload/20241206210124.png\"></p><p><br></p>', '已发布', '2024-12-06 21:01:26');
INSERT INTO `goods` VALUES (8, 1, '/api/upload/20241206210201.png', '粉色温柔系穿搭秋冬套装裙', '蝴蝶结棉服外套', '199', 100, '<p>粉色温柔系穿搭秋冬套装裙蝴蝶结棉服外套加厚A字半身裙两件套女\n</p><p><br></p>', '已发布', '2024-12-06 21:02:28');
INSERT INTO `goods` VALUES (9, 1, '/api/upload/20241206210322.png', '大衣半高领针织打底连衣裙', 'Basic House/百家好高腰鱼尾裙', '399', 99, '<p>Basic House/百家好高腰鱼尾裙配大衣半高领针织打底连衣裙女秋冬\n</p><p><br></p>', '已发布', '2024-12-06 21:03:53');
INSERT INTO `goods` VALUES (10, 4, '/api/upload/20241208153128.png', '可愛鞋鞋子娃娃鞋', '可愛鞋鞋子娃娃鞋', '159', 499, '<p><span style=\"color: var(--uv-styles-color-text-emphasis); font-family: Arial, sans-serif; font-size: 18px;\">可愛鞋鞋子娃娃鞋</span></p><p><span style=\"color: var(--uv-styles-color-text-emphasis); font-family: Arial, sans-serif; font-size: 18px;\"><br></span></p><p><img src=\"/api/upload/20241208153156.png\"></p>', '已发布', '2024-12-08 15:32:03');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `oid` int(11) NULL DEFAULT NULL COMMENT '订单',
  `gid` int(11) NULL DEFAULT NULL COMMENT '商品',
  `num` int(11) NULL DEFAULT NULL COMMENT '数量',
  `money` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '金额',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 1, 1, '1199.00', '2024-12-06 23:13:27');
INSERT INTO `order_item` VALUES (2, 2, 4, 1, '1389.00', '2024-12-07 13:51:12');
INSERT INTO `order_item` VALUES (3, 3, 4, 1, '1389.00', '2024-12-07 14:55:25');
INSERT INTO `order_item` VALUES (4, 4, 9, 1, '399.00', '2024-12-07 15:03:01');
INSERT INTO `order_item` VALUES (5, 4, 4, 1, '1389.00', '2024-12-07 15:03:01');
INSERT INTO `order_item` VALUES (6, 5, 10, 1, '159.00', '2024-12-08 15:41:57');
INSERT INTO `order_item` VALUES (7, 6, 7, 1, '394.00', '2024-12-08 15:45:14');
INSERT INTO `order_item` VALUES (8, 6, 3, 1, '100.00', '2024-12-08 15:45:14');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `num` int(11) NULL DEFAULT NULL COMMENT '商品数量',
  `total` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '总价',
  `uid` int(11) NULL DEFAULT NULL COMMENT '用户',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, '202412030212', 1, '1199.00', 1, NULL, '01', '2024-12-06 23:13:27');
INSERT INTO `orders` VALUES (2, '20241207135112', 1, '1389.00', 2, NULL, '03', '2024-12-07 13:51:12');
INSERT INTO `orders` VALUES (3, '20241207145524', 1, '1389.00', 2, '尽快发货', '03', '2024-12-07 14:55:25');
INSERT INTO `orders` VALUES (4, '20241207150300', 2, '1788.00', 2, '尽快', '04', '2024-12-07 15:03:01');
INSERT INTO `orders` VALUES (5, '20241208154156', 1, '159.00', 4, '尽快发货吧', '04', '2024-12-08 15:41:57');
INSERT INTO `orders` VALUES (6, '20241208154513', 2, '494.00', 4, '11', '03', '2024-12-08 15:45:14');

-- ----------------------------
-- Table structure for outfit
-- ----------------------------
DROP TABLE IF EXISTS `outfit`;
CREATE TABLE `outfit`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `image` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片',
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `season` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '季节',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '内容',
  `uid` int(11) NULL DEFAULT NULL COMMENT '发布人',
  `num` int(11) NULL DEFAULT NULL COMMENT '浏览量',
  `status` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '穿搭信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of outfit
-- ----------------------------
INSERT INTO `outfit` VALUES (1, '/api/upload/20241206210704.png', '休闲', '秋季', '原来博主会穿是因为懂审美', '<p>对于穿搭小白来说，“实穿的时髦感”才是重点，观看那些时尚博主的穿搭，你会发现她们都有一个共同点：审美超好！ 她们很懂得如何将单品与单品之间搭配好看，色彩之间又是怎么配才和谐，加入什么样的技巧能更时髦等等。因此虽然穿着都一样，但她们就是能给人一种很独特的高级感。</p><p><br></p><p><img src=\"/api/upload/20241206210745.png\"></p><p><br></p><p><br></p><p>所以我们在模仿穿搭时，不妨也试着品味一下她们对美的捕捉。</p><p><br></p><p>那下面就来分享秋冬12组穿搭，提升审美同时也可一键复制：</p><p><br></p><p> 一、今冬流行的4组搭配 冬天的美，其实万变不离其宗，几组经典简单的搭配，不需要太多不大用的单品，简洁高效才是穿衣的真理！</p><p><br></p><p> 1.毛衣+半身裙=简单又优雅的美丽 经典永不过时，这句话在毛衣+半身裙的组合当中很好地体现了。</p><p><br></p><p>无论时尚如何变迁，毛衣与半身裙的搭配都是每年秋冬的流行，不过这一组经典搭配，今年在色彩以及单品组合上会更突出！</p>', 1, 20, '已发布', '2024-12-04 23:06:16');
INSERT INTO `outfit` VALUES (2, '/api/upload/20241206210949.png', '休闲', '秋季', '好看的穿搭，谁见了都喜欢！', '<p>一个好看时髦的穿搭谁会不爱呢？</p><p><br></p><p><img src=\"/api/upload/20241206211005.png\">\n\n到了早春，又该烦恼着该怎么去穿衣打扮了，乍暖还寒的季节，如何穿才能兼顾温度和风度？\n\n\n很适合早春的一套韩系穿搭，带点慵懒又不失精致，运动鞋的加入，为整个造型增添了几分休闲的feel。\n\n卫衣+灰色百褶半裙，一整个韩里韩气的。</p>', 2, 24, '已发布', '2024-12-06 21:10:08');
INSERT INTO `outfit` VALUES (3, '/api/upload/20241206211125.png', '运动', '春季', '“教科书级别”的通勤穿搭', '<p>每天早晨，面对衣橱中的服装，如何在最短的时间内搭配出既得体又时尚的通勤装，是许多职业女性的共同难题。通勤穿搭不仅仅是一种日常装束，它更代表了你在职场中的形象与态度。如何通过穿搭展现出优雅与知性，成为了每一位女性职场精英追求的目标。</p><p><br></p><p><img src=\"/api/upload/20241206211149.png\">\n\n然而，通勤穿搭并不需要复杂繁琐的搭配技巧，也不需要花费大量时间与精力。只需掌握一些简单但有效的搭配原则，你就可以轻松打造出“教科书级别”的通勤造型，无论是面对重要会议还是日常工作，你都能从容自信，随时散发出优雅与专业的气质。\n\n\n通勤穿搭的色彩搭配技巧\n\n在通勤穿搭中，色彩搭配是最为直观且影响力最大的元素之一。适当的色彩选择和巧妙的搭配，不仅可以提升整体的职业感，还能让你在职场中展现出优雅与知性的气质。\n\n在职场穿搭中，色彩的协调性至关重要。经典的中性色调，如黑色、白色、灰色、米色和驼色，能够轻松为你的穿搭奠定沉稳、专业的基调。这些颜色不仅百搭，还能传达出一种冷静、自信的态度，非常适合职场环境。\n\n黑白配色：黑白配色是最经典、最简洁的组合，能够营造出强烈的对比效果，提升整体的时尚感与职场气质。白色衬衫搭配黑色西装裤或黑色铅笔裙，是永不过时的通勤经典穿搭，简洁大方，干练利落。</p>', 2, 152, '已发布', '2024-12-06 21:11:53');
INSERT INTO `outfit` VALUES (4, '/api/upload/20241206211339.png', '休闲', '夏季', '绝美出游穿搭！显瘦、显贵、洋气！谁穿谁美！', '<p>表妹们，下午好呀~</p><p><br></p><p><img src=\"/api/upload/20241206211402.png\">\n\n明天就是国庆啦，不用想，接下来的朋友圈不是清迈就是普吉岛，反正不在家就对了！\n\n毕竟一年就一个十一，咱该对这段时间的压力say byebye~\n\n但是我发现，太久没出门，没有认真打扮又突然可以出门玩的话，真的不知道穿什么怎么穿！\n\n所以这次贴心的我，为准备外出的姐妹们，准备好了不同场景的搭配作弊法~\n\n姐妹们根据衣柜里现有的单品照抄就好了！</p><p><br></p><p><img src=\"/api/upload/20241206211407.png\"></p>', 2, 157, '已发布', '2024-12-06 21:14:09');
INSERT INTO `outfit` VALUES (5, '/api/upload/20241206211519.png', '商务', '春季', '被三木博主圈粉了！', '<p>每到换季的时候，你是不是跟风买了很多衣服，卫衣、牛仔裤、风衣、半身裙各种各样的衣服一大堆，但是到真正搭配的时候，还是发现衣服太少，完全不知道怎么穿，甚至有些衣服穿了一次两次就放在 衣柜最底层？</p><p><br></p><p>其实秋季的衣服不必买太多，选择经典款式与百搭款式，利用高级的搭配技巧进行烘托，一件衣服就能够解锁出超多时尚的搭配，让你赚满回头率。\n\n不信的话，你可以多看看三木博主的穿衣搭配，明明没有太多的衣服，但是经过她的搭配后，一件衣服有N种搭配方式，使得造型更加百搭，比如同样的西装外套，配上不同的裤子和裙子，风格完全不一样，让人眼前一亮，那她到底是怎么做到的呢？一衣多穿又有哪些好处呢？下面我们一起来看看吧！</p><p><br></p><p><img src=\"/api/upload/20241206211517.png\"></p>', 2, 525, '已发布', '2024-12-06 21:15:35');
INSERT INTO `outfit` VALUES (6, '/api/upload/20241206211736.png', '休闲', '夏季', '深秋穿搭，照着穿一定美！', '<p>共和君说：</p><p>\n不管你喜欢哪种风格，总有一套适合你！</p><p><br></p><p><img src=\"/api/upload/20241206211732.png\"></p><p>\n一年四季</p><p>\n秋冬是最能展现搭配品位的时候</p><p>\n叠穿、混搭、长短搭配</p><p>\n而深秋这个季节过渡段</p><p>\n更让一大波姑娘陷入不知穿什么的困境</p><p>\n共和君整理了45套超时髦超实穿的深秋搭配</p>', 1, 563, '已发布', '2024-12-06 21:17:56');
INSERT INTO `outfit` VALUES (7, '/api/upload/20241206211913.png', '休闲', '秋季', '深秋冬装搭配一整套韩剧小香学院风富家千金', '<p>不知不觉，已是深秋。轻薄的衬衫、卫衣、T恤，早就不足以御寒了。\n\n软糯细密的针织衫、厚实防风的外套、美飒洋气的短靴，在这个秋天又一次成为焦点。</p><p><br></p><p><img src=\"/api/upload/20241206211910.png\"></p>', 1, 357, '已发布', '2024-12-06 21:19:25');
INSERT INTO `outfit` VALUES (8, '/api/upload/20241206212032.png', '休闲', '春季', '深秋穿搭look，慵懒时髦、舒适大方，也太好看了', '<p>等了一年又一年，她们终于可以大展身手，挽起衣袖来开始施展自己的才华，那就是深秋的穿搭。 </p><p><br></p><p>随着气温的下降，深秋季节的穿搭总是让人眼前一亮，秋冬过后又是寒冬，穿衣打扮逐渐不那么重要了。 </p><p><br></p><p>所以，能在深秋的时节用衣服给大家的视觉来个全方位的大洗，让每个看见她们的人都眼前一亮，都心动不已，这真的是一件非常有成就感的事情。 </p><p><br></p><p>今天，小编就为大家准备了27套实用的深秋搭配推荐，这些搭配优点众多，或是时尚、或是舒适、或是百搭，简直让人事事心动。</p><p><br></p><p><img src=\"/api/upload/20241206212029.png\"></p>', 1, 955, '已发布', '2024-12-06 21:20:53');
INSERT INTO `outfit` VALUES (9, '/api/upload/20241208154656.png', '休闲', '春季', '优雅得体的穿搭风格', '<p><span style=\"color: var(--uv-styles-color-text-emphasis);\">优雅得体的穿搭风格</span></p><p><span style=\"color: var(--uv-styles-color-text-emphasis);\"><br></span></p><p><br></p><p><img src=\"/api/upload/20241208154704.png\"></p>', 4, 2, '已发布', '2024-12-08 15:47:07');

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gid` int(11) NULL DEFAULT NULL COMMENT '商品',
  `uid` int(11) NULL DEFAULT NULL COMMENT '评论用户',
  `content` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 1, 1, '很好！', '2024-12-06 07:27:58');
INSERT INTO `review` VALUES (4, 9, 2, '很好的商品', '2024-12-07 15:53:34');
INSERT INTO `review` VALUES (6, 10, 4, '商品不错！', '2024-12-08 15:51:14');

-- ----------------------------
-- Table structure for stars
-- ----------------------------
DROP TABLE IF EXISTS `stars`;
CREATE TABLE `stars`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `fid` int(11) NULL DEFAULT NULL COMMENT '帖子',
  `uid` int(11) NULL DEFAULT NULL COMMENT '用户',
  `create_time` datetime NULL DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of stars
-- ----------------------------
INSERT INTO `stars` VALUES (1, 2, 2, '2024-12-07 10:40:19');
INSERT INTO `stars` VALUES (6, 2, 4, '2024-12-08 15:48:38');
INSERT INTO `stars` VALUES (7, 4, 4, '2024-12-08 15:53:02');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `image` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `realname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `sex` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `age` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '年龄',
  `address` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `create_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '/api/upload/20241204225818.jpg', '15263236323', '123456', '张曼', '女', '26', '北京市', '2024-12-04 22:58:35');
INSERT INTO `user` VALUES (2, '/api/upload/20241206235620.jpg', '15874859656', '123456', '刘玉', '女', '26', '北京市', '2024-12-06 23:38:02');
INSERT INTO `user` VALUES (3, '/api/upload/20241206235809.jpg', '18745632141', '123456', '赵宇', '男', '22', '北京市', '2024-12-06 23:40:00');
INSERT INTO `user` VALUES (4, '/api/upload/20241208154005.jpg', '17485965632', '123456', '张薇薇', '女', '23', '北京市朝阳区', '2024-12-08 15:39:20');

SET FOREIGN_KEY_CHECKS = 1;
