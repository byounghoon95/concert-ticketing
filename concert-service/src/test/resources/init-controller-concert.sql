DROP TABLE IF EXISTS `concert`;
DROP TABLE IF EXISTS `concert_detail`;
DROP TABLE IF EXISTS `seat`;

CREATE TABLE `concert` (
                           `concert_id` bigint NOT NULL AUTO_INCREMENT,
                           `created_at` datetime(6) DEFAULT NULL,
                           `deleted_at` datetime(6) DEFAULT NULL,
                           `modified_at` datetime(6) DEFAULT NULL,
                           `singer` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`concert_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `concert_detail` (
                                  `concert_detail_id` bigint NOT NULL AUTO_INCREMENT,
                                  `concert_id` bigint NOT NULL,
                                  `created_at` datetime(6) DEFAULT NULL,
                                  `date` datetime(6) DEFAULT NULL,
                                  `deleted_at` datetime(6) DEFAULT NULL,
                                  `modified_at` datetime(6) DEFAULT NULL,
                                  `open_date` datetime(6) DEFAULT NULL,
                                  `name` varchar(255) DEFAULT NULL,
                                  PRIMARY KEY (`concert_detail_id`),
                                  KEY `idx_concertId` (`concert_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `seat` (
                        `seat_no` int DEFAULT NULL,
                        `concert_detail_id` bigint NOT NULL,
                        `created_at` datetime(6) DEFAULT NULL,
                        `deleted_at` datetime(6) DEFAULT NULL,
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `member_id` bigint DEFAULT NULL,
                        `modified_at` datetime(6) DEFAULT NULL,
                        `price` bigint DEFAULT NULL,
                        `reserved_at` datetime(6) DEFAULT NULL,
                        `version` bigint DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `idx_concertDetailId` (`concert_detail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `concert` (singer) VALUES ('김범수');
INSERT INTO `concert_detail` (concert_id, date) VALUES (1,'2024-06-12T00:00:00');
INSERT INTO `concert_detail` (concert_id, date) VALUES (1,'2024-06-13T00:00:00');
INSERT INTO `concert_detail` (concert_id, date) VALUES (1,'2024-06-14T00:00:00');
INSERT INTO `seat` (concert_detail_id, seat_no, price, version) VALUES (1,1,5000,0);
INSERT INTO `seat` (concert_detail_id, seat_no, price, version) VALUES (1,2,5000,0);
INSERT INTO `seat` (concert_detail_id, seat_no, price, version) VALUES (1,3,5000,0);
INSERT INTO `seat` (concert_detail_id, seat_no, price, version) VALUES (1,4,5000,0);
INSERT INTO `seat` (concert_detail_id, seat_no, price, version) VALUES (1,5,5000,0);