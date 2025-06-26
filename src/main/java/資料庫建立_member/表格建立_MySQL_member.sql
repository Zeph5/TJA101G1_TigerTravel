use TigerTravelDB;

DROP TABLE IF EXISTS member;

-- 以下設定: 自增主鍵的起點值，也就是初始值，取值範圍是1 .. 655355 --
set auto_increment_offset=1;
-- 以下設定: 自增主鍵每次遞增的量，其預設值是1，取值範圍是1 .. 65535 --
set auto_increment_increment=1; 

CREATE TABLE member (
    member_id    INT AUTO_INCREMENT NOT NULL,
    mem_account        VARCHAR(20) NOT NULL,
    mem_name        VARCHAR(20) NOT NULL,
    mem_password    VARCHAR(50) NOT NULL,
    mem_email        VARCHAR(50) NOT NULL,
    mem_phone        VARCHAR(20),
    mem_address        VARCHAR(50),
    mem_status        TINYINT,
    avatar            LONGBLOB,
    create_time        timestamp,

    CONSTRAINT member_KEY 
        PRIMARY KEY (member_id)
) AUTO_INCREMENT = 1;

INSERT INTO member (mem_account, mem_name, mem_password, mem_email, mem_phone, mem_address, mem_status, create_time)
VALUES 
('sunny88', '維麗嘉杜斯妥也夫斯基', 'Xy!83kPz', 'sunny88@example.com', '0912345678', '台北市大安區1號', 1, CURRENT_TIMESTAMP),
('travel_lily', '林莉莉', 'AbC#2024z', 'lily123@example.com', '0922333444', '新北市板橋區2號', 1, CURRENT_TIMESTAMP),
('johnny777', 'John Chen', 'Js@98lkQ', 'johnchen@example.com', '0933444555', '桃園市中壢區3號', 1, CURRENT_TIMESTAMP),
('oceanBlue', '周海洋', 'oC3!paSe', 'oceanblue@example.com', '0966888999', '高雄市左營區4號', 1, CURRENT_TIMESTAMP),
('grace_yu', '游小慧', 'Yu#z7Tg1', 'gracey@example.com', '0955667788', '台中市西屯區5號', 1, CURRENT_TIMESTAMP),
('tiger2025', '張大虎', 'TiGeR@88', 'tigerzhang@example.com', '0988776655', '新竹市東區6號', 1, CURRENT_TIMESTAMP),
('moon_river', '何月', 'MoOn99^q', 'moonriver@example.com', '0922885566', '嘉義市西區7號', 1, CURRENT_TIMESTAMP),
('doraemon', '陳宜蓁', 'DoRa!521', 'dora@example.com', '0911223344', '台南市安平區8號', 1, CURRENT_TIMESTAMP),
('sky_hawk', '黃天翔', 'SkyH@wk7', 'skyhawk@example.com', '0933111222', '基隆市仁愛區9號', 1, CURRENT_TIMESTAMP),
('amy_travel', '黃愛米', 'AmY*888z', 'amytravel@example.com', '0977665544', '花蓮市中正路10號', 1, CURRENT_TIMESTAMP);