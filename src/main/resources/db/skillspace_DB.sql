# skillspace 프로젝트 DB

-- 회원 테이블
CREATE TABLE `UserInfo` (
	`user_id`			varchar(30)		NOT NULL,
	`user_pw`			varchar(60)		NOT NULL,
	`user_name`			varchar(50)		NOT NULL,
	`user_nick`			varchar(30)		NOT NULL	unique,
	`user_email`		varchar(100) 	NOT null	unique,
	`user_phone`		varchar(13)		NOT NULL,
	`user_zipcode`		char(5)			NOT NULL,
	`user_addr`			varchar(100)	NOT NULL,
	`user_addrdetail`	varchar(100)	NOT NULL,
	`user_email_receive`char(1)			NOT NULL	COMMENT 'Y,  N',
	`role`				char(1)			NOT NULL	default 'G' COMMENT '게스트 G, 호스트 H',
	`created_at`		datetime		NOT NULL	DEFAULT now(),
	`updated_at`		datetime		NOT NULL	DEFAULT now(),
	`user_status`		tinyint			NOT NULL	DEFAULT 1	COMMENT '활동 : 1, 휴면 : 2, 탈퇴 : 3, 정지 : 4',
	`user_email_receive`char(1)			NOT NULL	COMMENT 'Y, N'
);


ALTER TABLE `UserInfo` 
ADD CONSTRAINT `PK_USERINFO` 
PRIMARY KEY (`user_id`);


-- 호스트 회원 테이블
CREATE TABLE `Hostuserinfo` (
	`user_id`			varchar(30)		NOT NULL,
	`host_name`			varchar(50)		NOT NULL,
	`host_zipcode`		char(5)			NOT NULL,
	`host_addr`			varchar(100)	NOT NULL,
	`host_addrdetail`	varchar(100)	NOT NULL,
	`host_phone`		varchar(13)		NOT NULL,
	`description`		varchar(3000)	NOT NULL,
	`host_status`		tinyint			NOT NULL	DEFAULT 0	COMMENT '대기 : 0, 활동 : 1, 휴면 : 2, 탈퇴 : 3, 정지 : 4',
	`created_at`		datetime		NOT NULL	DEFAULT now(),
	`updated_at`		datetime		NOT NULL	DEFAULT now(),
	`approval`			char(1)			NOT NULL	DEFAULT 'N'	COMMENT 'Y,  N 호스트 승인'
);

ALTER TABLE `Hostuserinfo` ADD CONSTRAINT `PK_HOSTUSERINFO` PRIMARY KEY (`user_id`);

ALTER TABLE `Hostuserinfo` 
ADD CONSTRAINT `FK_UserInfo_TO_Hostuserinfo_1` 
FOREIGN KEY (`user_id`)
REFERENCES `UserInfo` (`user_id`);


-- 관리자 계정 테이블
CREATE TABLE `Admin` (
	`admin_id`		varchar(30)	NOT NULL,
	`admin_pw`		varchar(60)	NOT NULL,
	`admin_name`	varchar(30)	NOT NULL
);

ALTER TABLE `Admin` ADD CONSTRAINT `PK_ADMIN` PRIMARY KEY (`admin_id`);


-- 카테고리 테이블
drop table category;
CREATE TABLE `category` (
	`cate_id`		int 		NOT NULL,
	`cate_prtcode`	int			NULL		COMMENT '상위 카테고리 참조(참조키 설정)',
	`cate_name`		varchar(50)	NOT NULL,
	`level`			tinyint		NOT NULL	COMMENT '1차 2차 3차 카테고리',
	`sort_order`	int			NOT NULL DEFAULT 0	COMMENT '정렬 순서'
);

ALTER TABLE `catagory`
MODIFY `cate_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`cate_id`);

ALTER TABLE category
ADD CONSTRAINT FK_catagory
FOREIGN KEY (cate_prtcode)
REFERENCES category (cate_id)
ON DELETE CASCADE;

SHOW CREATE TABLE category;
ALTER TABLE category AUTO_INCREMENT = 1;




-- 호스트 공간 정보 테이블
CREATE TABLE `Host_Space` (
	`host_space_id`		int 			NOT NULL,
	`user_id`			varchar(30)		NOT NULL	COMMENT '호스트 아이디',
	`cate_id`			int				NOT NULL,
	`main_title`		varchar(100)	NOT NULL,
	`sub_title`			varchar(100)	NOT NULL,
	`space_name`		varchar(30)		NOT NULL,
	`space_intro`		varchar(3000)	NOT NULL,
	`space_guide`		varchar(3000)	NOT NULL,
	`space_zipcode`		char(5)			NOT NULL,
	`space_addr`		varchar(100)	NOT NULL,
	`space_addrdetail`	varchar(100)	NOT NULL,
	`created_at`		datetime		NOT NULL	DEFAULT now(),
	`updated_at`		datetime		NOT NULL	DEFAULT now(),
	`is_visible`		char(1)			not null
);

ALTER TABLE `host_space`
MODIFY `host_space_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`host_space_id`);

ALTER TABLE `host_space` 
ADD CONSTRAINT `FK_Hostuserinfo_TO_host_space_1` 
FOREIGN KEY (`user_id`)
REFERENCES `Hostuserinfo` (`user_id`);

ALTER TABLE `host_space` 
ADD CONSTRAINT `FK_catagory_TO_host_space_1` 
FOREIGN KEY (`cate_id`)
REFERENCES `catagory` (`cate_id`);


-- 상품 테이블
CREATE TABLE `Products` (
	`product_id`	int 			NOT NULL,
	`host_space_id`	int				NOT NULL,
	`user_id`		varchar(30)		NOT NULL	COMMENT '호스트 아이디',
	`name`			varchar(100)	NOT NULL,
	`product_intro`	varchar(3000)	NOT NULL,
	`price`			DECIMAL			NOT NULL	DEFAULT 0.00,
	`max_headcount`	tinyint			NOT NULL	DEFAULT 1,
	`is_visible`	char(1)			NOT NULL	COMMENT 'Y, N',
	`time_slot`		tinyint			NOT NULL,
	`open_time`		time			NOT NULL,
	`close_time`	time			NOT null,
	`created_at`	datetime		NOT NULL	DEFAULT now(),
	`updated_at`	datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Products`
MODIFY `product_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`product_id`);

ALTER TABLE `Products` 
ADD CONSTRAINT `FK_host_space_TO_Products_1` 
FOREIGN KEY (`host_space_id`)
REFERENCES `host_space` (`host_space_id`);

ALTER TABLE `Products` 
ADD CONSTRAINT `FK_Hostuserinfo_TO_Products_1` 
FOREIGN KEY (`user_id`)
REFERENCES `Hostuserinfo` (`user_id`);


-- 예약 테이블
CREATE TABLE `Reservations` (
	`reservation_id`	int 			NOT NULL,
	`user_id`			varchar(30)		NOT NULL	COMMENT '게스트 회원 아이디',
	`product_id`		int 			NOT NULL,
	`total_payment`		decimal(10, 2)	NOT NULL,
	`status`			varchar(15)		NOT NULL	COMMENT 'Pending, Completed, Cancelled',
	`reservation_date`  DATE            NOT NULL 	COMMENT '예약 날짜',
    `start_time`        TIME            NOT NULL 	COMMENT '예약 시작 시간',
    `end_time`          TIME            NOT NULL 	COMMENT '예약 종료 시간',
	`headcount`			tinyint			NOT NULL,
	`reservation_name`	varchar(30)		NOT NULL,
	`reservation_phone`	varchar(13)		NOT NULL,
	`reservation_email`	varchar(100)	NOT NULL,
	`is_review`			char(1)			NOT NULL	DEFAULT 'N'	COMMENT '리뷰 1개만 가능 하게',
	`created_at`		datetime		NOT NULL	DEFAULT now(),
	`updated_at`		datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Reservations`
MODIFY `reservation_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`reservation_id`);

ALTER TABLE `Reservations` 
ADD CONSTRAINT `FK_Products_TO_Reservations_1`
FOREIGN KEY (`product_id`)
REFERENCES `Products` (`product_id`);

ALTER TABLE `Reservations` 
ADD CONSTRAINT `FK_UserInfo_TO_Reservations_1` 
FOREIGN KEY (`user_id`)
REFERENCES `UserInfo` (`user_id`);


-- 결제 테이블
CREATE TABLE `Payments` (
	`payment_id`		int 			NOT NULL,
	`reservation_id`	int				NOT NULL,
	`payment_method`	varchar(100)	NOT NULL	COMMENT '카드, 계좌이체, pay 등',
	`amount`			decimal(10, 2)	NOT NULL,
	`status`			varchar(15)		NOT NULL	COMMENT 'Pending, Completed , Failed, Refunded',
	`created_at`		datetime		NOT NULL	DEFAULT now(),
	`updated_at`		datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Payments`
MODIFY `payment_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`payment_id`);

ALTER TABLE `Payments` 
ADD CONSTRAINT `FK_Reservations_TO_Payments_1` 
FOREIGN KEY (`reservation_id`)
REFERENCES `Reservations` (`reservation_id`);


-- 리뷰 테이블
CREATE TABLE `Reviews` (
	`review_id`			int 			NOT NULL,
	`reservation_id`	int				NOT NULL,
	`rating`			tinyint			NOT NULL,
	`review_text`		varchar(3000)	NULL,
	`created_at`		datetime		NOT NULL	DEFAULT now(),
	`updated_at`		datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Reviews`
MODIFY `review_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`review_id`);

ALTER TABLE `Reviews` 
ADD CONSTRAINT `FK_Reservations_TO_Reviews_1` 
FOREIGN KEY (`reservation_id`)
REFERENCES `Reservations` (`reservation_id`);


-- 답글 테이블
CREATE TABLE `Reply` (
	`reply_id`		int 			NOT NULL,
	`review_id`		int				NOT NULL,
	`reply_text`	varchar(3000)	NULL,
	`created_at`	datetime		NOT NULL	DEFAULT now(),
	`updated_at`	datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Reply`
MODIFY `reply_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`reply_id`);

ALTER TABLE `Reply` 
ADD CONSTRAINT `FK_Reviews_TO_Reply_1` 
FOREIGN KEY (`review_id`)
REFERENCES `Reviews` (`review_id`);

-- 질문 테이블
CREATE TABLE `Questions` (
	`question_id`		int 			NOT NULL,
	`host_space_id`		int				NOT NULL,
	`user_id`			varchar(30)		NOT NULL,
	`question_content`	varchar(200)	NOT NULL,
	`created_at`		datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Questions`
MODIFY `question_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`question_id`);

ALTER TABLE `Questions` 
ADD CONSTRAINT `FK_Host_space_TO_Questions_1` 
FOREIGN KEY (`host_space_id`)
REFERENCES `Host_space` (`host_space_id`);


-- 답변 테이블
CREATE TABLE `Answer` (
	`answer_id`			int 			NOT NULL,
	`question_id`		int				NOT NULL,
	`answer_content`	varchar(200)	NOT NULL,
	`created_at`		datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Answer`
MODIFY `answer_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`answer_id`);

ALTER TABLE `Answer` 
ADD CONSTRAINT `FK_Question_TO_Answer_1` 
FOREIGN KEY (`question_id`)
REFERENCES `Question` (`question_id`);


-- 찜 목록
CREATE TABLE `Wishlist` (
	`wish_id`		int 		NOT NULL,
	`product_id`	int			NOT NULL,
	`user_id`		varchar(30)	NOT NULL
);

ALTER TABLE `Wishlist`
MODIFY `wish_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`wish_id`);


ALTER TABLE `Wishlist` 
ADD CONSTRAINT `FK_Products_TO_Wishlist_1` 
FOREIGN KEY (`product_id`)
REFERENCES `Products` (`product_id`);

ALTER TABLE `Wishlist` 
ADD CONSTRAINT `FK_UserInfo_TO_Wishlist_1` 
FOREIGN KEY (`user_id`)
REFERENCES `UserInfo` (`user_id`);


-- 장바구니
CREATE TABLE `Cart` (
	`cart_id`			int 	NOT NULL,
	`reservation_id`	int		NOT NULL,
	`quantity`			tinyint	NOT NULL
);

ALTER TABLE `Cart`
MODIFY `cart_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`cart_id`);


ALTER TABLE `Cart` 
ADD CONSTRAINT `FK_Reservations_TO_Cart_1` 
FOREIGN KEY (`reservation_id`)
REFERENCES `Reservations` (`reservation_id`);


-- 이미지 테이블
CREATE TABLE `Images` (
	`image_id`			int 			NOT NULL,
	`entity_type`		varchar(30)		NOT NULL	COMMENT 'host_space, Products, Reviews',
	`entity_id`			int				NOT NULL	COMMENT '테이블 아이디',
	`image_up_folder`	varchar(255)	NOT NULL,
	`image_name`		varchar(100)	NOT NULL,
	`created_at`		datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Images`
MODIFY `image_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`image_id`);


-- 로그인 기록 테이블
CREATE TABLE `loginlog` (
	`log_id`			int 		NOT NULL,
	`log_user`			varchar(30)	NOT NULL,
	`log_accesstime`	datetime	NOT NULL,
	`log_role`			char		NOT NULL	COMMENT '관리자 a, 회원 g'
);

ALTER TABLE `loginlog`
MODIFY `log_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`log_id`);