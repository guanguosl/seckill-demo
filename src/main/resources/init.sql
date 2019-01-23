--方法很重要！！！不要做重复的无用功
CREATE TABLE `miaosha_user` (
  `id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `nickname` varchar(256) DEFAULT NULL COMMENT '昵称',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `salt` varchar(10) DEFAULT NULL COMMENT 'key',
  `head` varchar(50) DEFAULT NULL COMMENT '头像',
  `register_date` date DEFAULT NULL COMMENT '注册四件',
  `last_login_date` date DEFAULT NULL COMMENT '上次登陆时间',
  `login_count` int(11) DEFAULT NULL COMMENT '登陆次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息';