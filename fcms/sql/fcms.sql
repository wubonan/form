/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50616
Source Host           : localhost:3306
Source Database       : fcms

Target Server Type    : MYSQL
Target Server Version : 50616
File Encoding         : 65001

Date: 2017-09-27 11:14:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `article_type`
-- ----------------------------
DROP TABLE IF EXISTS `article_type`;
CREATE TABLE `article_type` (
  `id` int(11) NOT NULL COMMENT '文章类别ID',
  `article_type` varchar(255) NOT NULL COMMENT '文章分类',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of article_type
-- ----------------------------
INSERT INTO `article_type` VALUES ('1', '编程');
INSERT INTO `article_type` VALUES ('2', '兴趣');
INSERT INTO `article_type` VALUES ('3', '电影');
INSERT INTO `article_type` VALUES ('4', '摄影');

-- ----------------------------
-- Table structure for `auth_code`
-- ----------------------------
DROP TABLE IF EXISTS `auth_code`;
CREATE TABLE `auth_code` (
  `id` varchar(33) NOT NULL,
  `expireAt` bigint(20) NOT NULL,
  `accountId` int(11) NOT NULL,
  `type` int(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of auth_code
-- ----------------------------
INSERT INTO `auth_code` VALUES ('5816431282d84ea2b5cadc37af2a8125', '1504084414732', '2', '0');

-- ----------------------------
-- Table structure for `friend`
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
  `userId` int(11) NOT NULL COMMENT '用户ID',
  `friendId` int(11) NOT NULL COMMENT '朋友ID',
  `createAt` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`userId`,`friendId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of friend
-- ----------------------------

-- ----------------------------
-- Table structure for `friend_link`
-- ----------------------------
DROP TABLE IF EXISTS `friend_link`;
CREATE TABLE `friend_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `logo` varchar(255) DEFAULT NULL COMMENT '友情链接logo',
  `site_name` varchar(255) NOT NULL COMMENT '网站名',
  `url` varchar(255) NOT NULL COMMENT '网站url',
  `sort` int(11) DEFAULT NULL COMMENT '顺序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of friend_link
-- ----------------------------
INSERT INTO `friend_link` VALUES ('2', '/upload/51f881397251f.jpg', '百度', 'www.baidu.com', null);

-- ----------------------------
-- Table structure for `like_message_log`
-- ----------------------------
DROP TABLE IF EXISTS `like_message_log`;
CREATE TABLE `like_message_log` (
  `accountId` int(11) NOT NULL,
  `refType` int(11) NOT NULL,
  `refId` int(11) NOT NULL,
  `createAt` datetime NOT NULL COMMENT 'creatAt用于未来清除该表中时间比较久远的记录',
  PRIMARY KEY (`accountId`,`refType`,`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用于保存点赞的记录，用于记录点赞后发布过系统消息，保障只发一次';


-- ----------------------------
-- Table structure for `login_log`
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `accountId` int(11) NOT NULL,
  `loginAt` datetime NOT NULL,
  `ip` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for `menu`
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(18) DEFAULT NULL,
  `target` varchar(18) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `weight` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES ('1', '文章', '_self', 'blog', '0', '0');
INSERT INTO `menu` VALUES ('2', '视频', '_self', 'video', '0', '0');
INSERT INTO `menu` VALUES ('3', '问答', '_self', 'ask', '0', '0');
INSERT INTO `menu` VALUES ('4', '发现', '_self', 'tags', '0', '0');
INSERT INTO `menu` VALUES ('5', '走廊', '_self', 'gallery', '0', '0');

-- ----------------------------
-- Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL COMMENT '消息的主人',
  `friend` int(11) NOT NULL COMMENT '对方的ID',
  `sender` int(11) NOT NULL COMMENT '发送者',
  `receiver` int(11) NOT NULL COMMENT '接收者',
  `type` tinyint(2) NOT NULL COMMENT '0：普通消息，1：系统消息',
  `content` text NOT NULL,
  `createAt` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for `news_feed`
-- ----------------------------
DROP TABLE IF EXISTS `news_feed`;
CREATE TABLE `news_feed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL COMMENT '动态创建者',
  `refType` tinyint(2) NOT NULL COMMENT '动态引用类型',
  `refId` int(11) NOT NULL DEFAULT '0' COMMENT '动态引用所关联的 id',
  `refParentType` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'reply所属的贴子类型, 与type 字段填的值一样',
  `refParentId` int(11) NOT NULL DEFAULT '0',
  `createAt` datetime NOT NULL COMMENT '动态创建时间',
  PRIMARY KEY (`id`),
  KEY `refId` (`refId`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `posts`
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '发布内容ID',
  `userId` int(11) NOT NULL COMMENT '作者',
  `comments` int(11) DEFAULT '0' COMMENT '评论数',
  `content` longtext NOT NULL COMMENT '内容',
  `createAt` datetime NOT NULL COMMENT '创建时间',
  `images` text DEFAULT NULL COMMENT '图片',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `views` int(11) NOT NULL DEFAULT '0' COMMENT '浏览数',
  `likeCount` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态，1表示发布，0表示草稿，2表示锁定',
  `summary` varchar(255) DEFAULT NULL COMMENT '摘要',
  `post_type` int(11) NOT NULL COMMENT '发布的类型，比如文章，图片，视频等',
  `last_images` varchar(255) DEFAULT NULL COMMENT '最后一张图片',
  `video` varchar(255) DEFAULT NULL COMMENT '视频地址',
  `article_type` int(11) DEFAULT NULL COMMENT '文章分类，可为空，发布文章时才需要选择分类',
  `privacy` int(11) DEFAULT NULL COMMENT '权限，0表示公开，1表示不公开',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `posts_comment`
-- ----------------------------
DROP TABLE IF EXISTS `posts_comment`;
CREATE TABLE `posts_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `userId` int(11) NOT NULL COMMENT '作者Id',
  `content` text NOT NULL COMMENT '评论内容',
  `createAt` datetime NOT NULL COMMENT '创建时间',
  `postId` int(11) NOT NULL COMMENT 'POST的ID',
  `toId` int(11) DEFAULT NULL COMMENT '要回复的评论的id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `posts_like`
-- ----------------------------
DROP TABLE IF EXISTS `posts_like`;
CREATE TABLE `posts_like` (
  `userId` int(11) NOT NULL COMMENT '点赞用户ID',
  `refId` int(11) NOT NULL COMMENT '点赞关联表ID,这里统一为posts的ID',
  `createAt` datetime NOT NULL COMMENT '点赞时间',
  PRIMARY KEY (`userId`,`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of posts_like
-- ----------------------------

-- ----------------------------
-- Table structure for `post_type`
-- ----------------------------
DROP TABLE IF EXISTS `post_type`;
CREATE TABLE `post_type` (
  `id` int(11) NOT NULL COMMENT 'post_type的ID',
  `post_type` varchar(255) NOT NULL COMMENT 'post_type类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of post_type
-- ----------------------------
INSERT INTO `post_type` VALUES ('1', '文章');
INSERT INTO `post_type` VALUES ('2', '视频');
INSERT INTO `post_type` VALUES ('3', '图片');
INSERT INTO `post_type` VALUES ('4', '问答');

-- ----------------------------
-- Table structure for `refer_me`
-- ----------------------------
DROP TABLE IF EXISTS `refer_me`;
CREATE TABLE `refer_me` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `referAccountId` int(11) NOT NULL COMMENT '接收者账号id',
  `newsFeedId` int(11) NOT NULL COMMENT 'newsFeedId',
  `type` tinyint(2) NOT NULL COMMENT '@我、评论我等等的refer类型',
  `createAt` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of refer_me
-- ----------------------------

-- ----------------------------
-- Table structure for `remind`
-- ----------------------------
DROP TABLE IF EXISTS `remind`;
CREATE TABLE `remind` (
  `accountId` int(11) NOT NULL COMMENT '用户账号id，必须手动指定，不自增',
  `referMe` int(11) NOT NULL DEFAULT '0' COMMENT '提到我的消息条数',
  `message` int(11) NOT NULL DEFAULT '0' COMMENT '私信条数',
  `fans` int(11) NOT NULL DEFAULT '0' COMMENT '粉丝增加个数',
  PRIMARY KEY (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of remind
-- ----------------------------

-- ----------------------------
-- Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL,
  `role_name` char(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------

-- ----------------------------
-- Table structure for `sensitive_words`
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_words`;
CREATE TABLE `sensitive_words` (
  `id` int(11) NOT NULL,
  `word` varchar(32) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `word_pinyin` varchar(60) NOT NULL COMMENT '敏感词',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sensitive_words
-- ----------------------------

-- ----------------------------
-- Table structure for `session`
-- ----------------------------
DROP TABLE IF EXISTS `session`;
CREATE TABLE `session` (
  `id` varchar(33) NOT NULL,
  `accountId` int(11) NOT NULL,
  `expireAt` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `site_info`
-- ----------------------------
DROP TABLE IF EXISTS `site_info`;
CREATE TABLE `site_info` (
  `id` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL COMMENT '网站标题',
  `description` text COMMENT '网站描述',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词',
  `logo` varchar(255) DEFAULT NULL COMMENT '网站logo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of site_info
-- ----------------------------
INSERT INTO `site_info` VALUES ('1', 'MB，Media & Blog，萌博', 'MB官网', 'Media，Blog，媒体博客，博客，技术，Jfinal，分享，生活', '/upload/logo/mb_big.png');

-- ----------------------------
-- Table structure for `tags`
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) NOT NULL COMMENT '标签名',
  `posts` int(11) DEFAULT '0' COMMENT '发表数',
  `locked` int(11) DEFAULT '0' COMMENT '是否锁定，0表示不锁定，1表示锁定',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `upload_counter`
-- ----------------------------
DROP TABLE IF EXISTS `upload_counter`;
CREATE TABLE `upload_counter` (
  `uploadType` varchar(50) NOT NULL,
  `counter` int(11) NOT NULL,
  `descr` varchar(50) NOT NULL,
  PRIMARY KEY (`uploadType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of upload_counter
-- ----------------------------
INSERT INTO `upload_counter` VALUES ('article', '11', '记录article模块上传图片，用于生成相对路径');
INSERT INTO `upload_counter` VALUES ('blog', '70', '记录blog模块上传图片的总数量，用于生成相对路径');
INSERT INTO `upload_counter` VALUES ('gallery', '0', '记录gallery模块上传图片的总数量，用于生成相对路径');
INSERT INTO `upload_counter` VALUES ('share', '202', '记录share模块上传图片的总数量，用于生成相对路径');
INSERT INTO `upload_counter` VALUES ('video', '311', '记录video模块上传图片的总数量，用于生成相对路径');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `email` varchar(255) NOT NULL COMMENT '用户邮箱',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `salt` varchar(255) NOT NULL COMMENT '盐值',
  `nickName` varchar(255) NOT NULL COMMENT '昵称',
  `userName` varchar(255) NOT NULL COMMENT '用户名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `gender` int(11) DEFAULT NULL COMMENT '性别',
  `roleId` int(11) DEFAULT NULL COMMENT '角色ID',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
  `signature` varchar(255) DEFAULT NULL COMMENT '签名',
  `createAt` datetime NOT NULL COMMENT '创建时间',
  `status` int(11) DEFAULT NULL COMMENT '用户状态',
  `ip` varchar(255) NOT NULL COMMENT '注册IP',
  `likeCount` int(11) DEFAULT '0' COMMENT '获赞数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'test@qq.com', '08677a86de819764f8022971ee5b5e02d4248ef0e54855005ce0d47cb01cfe44', 'SrZAswE1su0SKKDWnGQvl1kO4lnk-fTH', 'test', 'test', '/assets/images/avatar/default.jpg', null, '2', null, 'just do it!', '2017-08-30 16:17:13', '1', '0:0:0:0:0:0:0:1', '0');

-- ----------------------------
-- Table structure for `users_open_oauth`
-- ----------------------------
DROP TABLE IF EXISTS `users_open_oauth`;
CREATE TABLE `users_open_oauth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_token` varchar(255) DEFAULT NULL,
  `expire_in` varchar(255) DEFAULT NULL,
  `oauth_code` varchar(255) DEFAULT NULL,
  `oauth_type` int(11) DEFAULT NULL,
  `oauth_user_id` varchar(255) DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users_open_oauth
-- ----------------------------
