CREATE DATABASE IF NOT EXISTS shanwei_skill DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shanwei_skill;

DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS service_order;
DROP TABLE IF EXISTS service_request;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS skill_category;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  nickname VARCHAR(64),
  email VARCHAR(128),
  phone VARCHAR(32),
  avatar VARCHAR(255),
  create_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE skill_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE skill (
  skill_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  skill_name VARCHAR(128) NOT NULL,
  category VARCHAR(64) NOT NULL,
  description TEXT,
  price_type VARCHAR(64),
  service_mode VARCHAR(64),
  price VARCHAR(64),
  skill_level VARCHAR(64),
  status VARCHAR(32) NOT NULL,
  create_time DATETIME NOT NULL,
  INDEX idx_skill_user(user_id),
  INDEX idx_skill_name(skill_name),
  CONSTRAINT fk_skill_user FOREIGN KEY(user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE service_request (
  request_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  description TEXT,
  category VARCHAR(64),
  budget VARCHAR(64),
  deadline DATETIME,
  status VARCHAR(32) NOT NULL,
  create_time DATETIME NOT NULL,
  INDEX idx_request_user(user_id),
  CONSTRAINT fk_request_user FOREIGN KEY(user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE service_order (
  order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  skill_id BIGINT NOT NULL,
  buyer_id BIGINT NOT NULL,
  seller_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  remark VARCHAR(255),
  create_time DATETIME NOT NULL,
  INDEX idx_order_skill(skill_id),
  INDEX idx_order_buyer(buyer_id),
  INDEX idx_order_seller(seller_id),
  CONSTRAINT fk_order_skill FOREIGN KEY(skill_id) REFERENCES skill(skill_id),
  CONSTRAINT fk_order_buyer FOREIGN KEY(buyer_id) REFERENCES `user`(id),
  CONSTRAINT fk_order_seller FOREIGN KEY(seller_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE comment (
  comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  from_user BIGINT NOT NULL,
  to_user BIGINT NOT NULL,
  score INT NOT NULL,
  content VARCHAR(500),
  time DATETIME NOT NULL,
  INDEX idx_comment_order(order_id),
  CONSTRAINT fk_comment_order FOREIGN KEY(order_id) REFERENCES service_order(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  to_user BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  content VARCHAR(500),
  status VARCHAR(32) NOT NULL,
  create_time DATETIME NOT NULL,
  INDEX idx_message_user(to_user),
  CONSTRAINT fk_message_user FOREIGN KEY(to_user) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
