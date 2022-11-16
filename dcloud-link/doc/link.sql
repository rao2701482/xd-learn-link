CREATE TABLE `link_group`
(
    `id`           bigint unsigned NOT NULL,
    `title`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '组名',
    `account_no`   bigint                                                 DEFAULT NULL COMMENT '账号唯一编号',
    `gmt_create`   datetime                                               DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

# 用户访问的短链库
CREATE TABLE `short_link`
(
    `id`           bigint unsigned NOT NULL,
    `group_id`     bigint                                                  DEFAULT NULL COMMENT '组',
    `title`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '短链标题',
    `original_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '原始url地址',
    `domain`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '短链域名',
    `code`         varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '短链压缩码',
    `sign`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '长链的md5码，方便查找',
    `expired`      datetime                                                DEFAULT NULL COMMENT '过期时间，长久就是-1',
    `account_no`   bigint                                                  DEFAULT NULL COMMENT '账号唯一编号',
    `gmt_create`   datetime                                                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime                                                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `del`          int unsigned NOT NULL COMMENT '0是默认，1是删除',
    `state`        varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '状态，lock是锁定不可用，active是可用',
    `link_type`    varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '链接产品层级：FIRST 免费青铜、SECOND黄金、THIRD钻石',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

# 商家访问的双写短链库
CREATE TABLE `group_code_mapping_1`
(
    `id`           bigint unsigned NOT NULL,
    `group_id`     bigint                                                  DEFAULT NULL COMMENT '组',
    `title`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '短链标题',
    `original_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '原始url地址',
    `domain`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '短链域名',
    `code`         varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '短链压缩码',
    `sign`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '长链的md5码，方便查找',
    `expired`      datetime                                                DEFAULT NULL COMMENT '过期时间，长久就是-1',
    `account_no`   bigint                                                  DEFAULT NULL COMMENT '账号唯一编号',
    `gmt_create`   datetime                                                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime                                                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `del`          int unsigned NOT NULL COMMENT '0是默认，1是删除',
    `state`        varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '状态，lock是锁定不可用，active是可用',
    `link_type`    varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '链接产品层级：FIRST 免费青铜、SECOND黄金、THIRD钻石',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

# 官方域名和用户自定义域名库
CREATE TABLE `domain`
(
    `id`           bigint unsigned NOT NULL,
    `account_no`   bigint                                                 DEFAULT NULL COMMENT '用户自己绑定的域名',
    `domain_type`  varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '域名类型，自建custom, 官方offical',
    `value`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
    `del`          int(1) unsigned zerofill DEFAULT '0' COMMENT '0是默认，1是禁用',
    `gmt_create`   datetime                                               DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;