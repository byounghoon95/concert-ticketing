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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `member` (member_login_id, balance) VALUES ('A0', 0);
INSERT INTO `member` (member_login_id, balance) VALUES ('A1', 1000);
INSERT INTO `member` (member_login_id, balance) VALUES ('A2', 2000);
INSERT INTO `member` (member_login_id, balance) VALUES ('A3', 3000);
INSERT INTO `member` (member_login_id, balance) VALUES ('A4', 4000);
INSERT INTO `member` (member_login_id, balance) VALUES ('A5', 5000);
INSERT INTO `member` (member_login_id, balance) VALUES ('A6', 6000);
INSERT INTO `member` (member_login_id, balance) VALUES ('A7', 7000);
INSERT INTO `member` (member_login_id, balance) VALUES ('A8', 8000);
