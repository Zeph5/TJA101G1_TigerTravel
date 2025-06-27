CREATE DATABASE IF NOT EXISTS TigerTravelDB;

USE TigerTravelDB;

DROP TABLE IF EXISTS ticket_list;
DROP TABLE IF EXISTS travel_plan_day;
DROP TABLE IF EXISTS travel_image;
DROP TABLE IF EXISTS tourist_id;
DROP TABLE IF EXISTS ticket_order_receipt;
DROP TABLE IF EXISTS ticket_order;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS tour_order;
DROP TABLE IF EXISTS manager;
DROP TABLE IF EXISTS coupon;
DROP TABLE IF EXISTS travel_itinerary;
DROP TABLE IF EXISTS favorite_tour;
DROP TABLE IF EXISTS favorite_scenery;
DROP TABLE IF EXISTS travel_plan;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS tagsdb;
DROP TABLE IF EXISTS scenery_image;
DROP TABLE IF EXISTS scenery_score;
-- DROP TABLE IF EXISTS scenery;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    member_id INT AUTO_INCREMENT NOT NULL,
    mem_account VARCHAR(20) NOT NULL,
    mem_name VARCHAR(20) NOT NULL,
    mem_password VARCHAR(50) NOT NULL,
    mem_email VARCHAR(50) NOT NULL,
    mem_phone VARCHAR(20),
    mem_address VARCHAR(50),
    mem_status TINYINT NOT NULL,
    avatar LONGBLOB,
    create_time TIMESTAMP,
    PRIMARY KEY (member_id)
) AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS scenery (
    scenery_id INT AUTO_INCREMENT NOT NULL COMMENT '景點ID',
    sce_name VARCHAR(255) NOT NULL UNIQUE COMMENT '景點名稱',
    sce_intro VARCHAR(500) NOT NULL COMMENT '評分留言',
    sce_total_score INT COMMENT '總評分數',
    score_sce_total_score INT COMMENT '總評分人數',
    sce_address VARCHAR(500) COMMENT '景點地址',
    sce_longitude DECIMAL(9,6) COMMENT '景點經度',
    sce_latitude DECIMAL(9,6) COMMENT '景點緯度',
    create_time TIMESTAMP COMMENT '創建時間',
    PRIMARY KEY (scenery_id)
) AUTO_INCREMENT = 1;

CREATE TABLE scenery_score(
    score_id INT AUTO_INCREMENT NOT NULL COMMENT '評分ID',
    member_id INT NOT NULL COMMENT '用戶ID',
    score TINYINT NOT NULL COMMENT '評分星數',
    scenery_id INT NOT NULL COMMENT '景點資料ID',
    sce_comment VARCHAR(200) COMMENT '評論',
    create_time TIMESTAMP COMMENT '創建時間',
    PRIMARY KEY (score_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    FOREIGN KEY (scenery_id) REFERENCES scenery(scenery_id)
) AUTO_INCREMENT = 1;

CREATE TABLE scenery_image(
    sce_image_id INT AUTO_INCREMENT NOT NULL COMMENT '景點圖ID',
    scenery_id INT NOT NULL COMMENT '景點ID',
    sce_image LONGBLOB NOT NULL COMMENT '圖片ID',
    create_time TIMESTAMP COMMENT '創建時間',
    PRIMARY KEY (sce_image_id),
    FOREIGN KEY (scenery_id) REFERENCES scenery(scenery_id)
) AUTO_INCREMENT = 1;

CREATE TABLE tagsdb(
    tagsdb_id INT AUTO_INCREMENT NOT NULL COMMENT '景點圖ID',
    tags_name VARCHAR(100) NOT NULL UNIQUE COMMENT '標籤名稱',
    create_time TIMESTAMP COMMENT '創建時間',
    PRIMARY KEY (tagsdb_id)
) AUTO_INCREMENT = 1;

CREATE TABLE tags(
    tags_id INT AUTO_INCREMENT NOT NULL COMMENT '標籤ID',
    tagsdb_id INT NOT NULL COMMENT '標籤庫ID',
    scenery_id INT NOT NULL COMMENT '景點ID',
    create_time TIMESTAMP COMMENT '創建時間',
    PRIMARY KEY (tags_id),
    FOREIGN KEY (tagsdb_id) REFERENCES tagsdb(tagsdb_id),
    FOREIGN KEY (scenery_id) REFERENCES scenery(scenery_id)
) AUTO_INCREMENT = 1;

CREATE TABLE travel_plan (
    travel_plan_id INTEGER NOT NULL AUTO_INCREMENT,
    travel_title VARCHAR(64) NOT NULL,
    travel_plan_banner MEDIUMBLOB,
    travel_plan_description VARCHAR(256),
    published_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (travel_plan_id)
) AUTO_INCREMENT = 100;

CREATE TABLE favorite_scenery (
    favorite_sce_id INTEGER AUTO_INCREMENT NOT NULL,
    member_id INTEGER NOT NULL,
    scenery_id INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (favorite_sce_id),
    FOREIGN KEY (scenery_id) REFERENCES scenery(scenery_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) AUTO_INCREMENT = 1;

CREATE TABLE favorite_tour (
    favorite_tour_id INTEGER AUTO_INCREMENT NOT NULL,
    member_id INTEGER NOT NULL,
    travel_plan_id INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (favorite_tour_id),
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plan(travel_plan_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE travel_itinerary (
    travel_itinerary_id INTEGER NOT NULL AUTO_INCREMENT,
    travel_plan_id INTEGER NOT NULL,
    max_tourist INTEGER NOT NULL,
    total_price INTEGER NOT NULL,
    published_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    travel_start_date TIMESTAMP NOT NULL,
    travel_end_date TIMESTAMP NOT NULL,
    PRIMARY KEY (travel_itinerary_id),
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plan(travel_plan_id)
);

CREATE TABLE coupon (
    coupon_id INTEGER NOT NULL AUTO_INCREMENT,
    coupon_key VARCHAR(20) NOT NULL UNIQUE,
    coupon_amount INTEGER NOT NULL,
    coupon_start_date TIMESTAMP NOT NULL,
    coupon_end_date TIMESTAMP NOT NULL,
    published_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    travel_plan_id INTEGER NOT NULL,
    last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (coupon_id),
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plan(travel_plan_id)
);

CREATE TABLE manager (
    manager_id INTEGER AUTO_INCREMENT NOT NULL,
    manager_email VARCHAR(64),
    manager_account VARCHAR(20),
    manager_password VARCHAR(64),
    manager_name VARCHAR(20),
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (manager_id)
) AUTO_INCREMENT = 1;

CREATE TABLE tour_order (
    tour_order_id INTEGER AUTO_INCREMENT NOT NULL,
    member_id INTEGER NOT NULL,
    travel_itinerary_id INTEGER NOT NULL,
    tour_price INTEGER NOT NULL,
    total_amount INTEGER NOT NULL,
    tour_order_status TINYINT NOT NULL,
    coupon_id INTEGER,
    total_after_coupon INTEGER NOT NULL,
    manager_id INTEGER,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tour_order_id),
    FOREIGN KEY (travel_itinerary_id) REFERENCES travel_itinerary(travel_itinerary_id),
    FOREIGN KEY (coupon_id) REFERENCES coupon(coupon_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE ticket (
    ticket_id INT AUTO_INCREMENT COMMENT '商品ID',
    ticket_stock INT NOT NULL COMMENT '商品庫存數量',
    ticket_price DECIMAL(10,2) NOT NULL COMMENT '商品價格',
    ticket_name VARCHAR(100) NOT NULL COMMENT '商品名稱',
    ticket_description TEXT COMMENT '商品描述',
    ticket_status TINYINT DEFAULT 1 COMMENT '商品狀態（1=有庫存, 0=無庫存）',
    ticket_image LONGBLOB COMMENT '商品圖片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    PRIMARY KEY (ticket_id)
);

CREATE TABLE ticket_order (
    ticket_order_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '訂單ID',
    member_id INT NOT NULL COMMENT '下單會員ID',
    order_price DECIMAL(10,2) NOT NULL COMMENT '訂單總金額',
    order_status TINYINT DEFAULT 0 COMMENT '訂單狀態（0=未付款, 1=已付款, 2=已取消）',
    order_datetime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下單時間',
    manager_id INT COMMENT '負責處理訂單的管理員ID',
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    FOREIGN KEY (manager_id) REFERENCES manager(manager_id)
);

CREATE TABLE ticket_order_receipt (
    ticket_order_receipt_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '購票明細編號',
    ticket_order_id INT NOT NULL COMMENT '對應的訂單編號',
    ticket_id INT NOT NULL COMMENT '商品ID',
    ticket_count INT NOT NULL COMMENT '購買數量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    FOREIGN KEY (ticket_order_id) REFERENCES ticket_order(ticket_order_id),
    FOREIGN KEY (ticket_id) REFERENCES ticket(ticket_id)
);

CREATE TABLE tourist_id (
    tourist_id INTEGER AUTO_INCREMENT NOT NULL,
    tour_order_id INTEGER NOT NULL,
    tourist_name VARCHAR(50) NOT NULL,
    tourist_personal_id VARCHAR(10) NOT NULL,
    contact_number VARCHAR(10) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tourist_id),
    FOREIGN KEY (tour_order_id) REFERENCES tour_order(tour_order_id)
);

CREATE TABLE travel_image (
    travel_image_id INTEGER NOT NULL AUTO_INCREMENT,
    travel_plan_id INTEGER NOT NULL,
    image LONGBLOB,
    PRIMARY KEY (travel_image_id),
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plan(travel_plan_id)
);

CREATE TABLE travel_plan_day (
    travel_plan_day_id INTEGER NOT NULL AUTO_INCREMENT,
    travel_plan_id INTEGER NOT NULL,
    scenery_id INTEGER NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    travel_day_num INTEGER NOT NULL,
    travel_seq_num INTEGER NOT NULL,
    travel_time TIMESTAMP NOT NULL,
    PRIMARY KEY (travel_plan_day_id),
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plan(travel_plan_id),
    FOREIGN KEY (scenery_id) REFERENCES scenery(scenery_id)
);

CREATE TABLE ticket_list (
    ticket_list_id INT AUTO_INCREMENT COMMENT '商品列表編號',
    ticket_order_id INT NOT NULL COMMENT '商品ID（對應商品表）',
    ticket_id INT NOT NULL COMMENT '商品訂單',
    ticket_count INT NOT NULL COMMENT '該商品數量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '新增時間',
    PRIMARY KEY (ticket_list_id),
    FOREIGN KEY (ticket_id) REFERENCES ticket(ticket_id),
    FOREIGN KEY (ticket_order_id) REFERENCES ticket_order(ticket_order_id)
);