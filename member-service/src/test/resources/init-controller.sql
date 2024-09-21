DROP TABLE IF EXISTS `member`;

CREATE TABLE `member` (
                          `balance` bigint DEFAULT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `deleted_at` datetime(6) DEFAULT NULL,
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `modified_at` datetime(6) DEFAULT NULL,
                          `version` bigint DEFAULT NULL,
                          `member_login_id` varchar(255) NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `UK8eaedgr362q8tog924fo6u7nv` (`member_login_id`),
                          KEY `idx_memberLogin_id` (`member_login_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `member` (member_login_id, balance, version) VALUES ('A1', 5000, 0);