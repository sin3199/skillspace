# skillspace 프로젝트 DB

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
	`host_status`		tinyint			NOT NULL	DEFAULT 1	COMMENT '활동 : 1, 휴면 : 2, 탈퇴 : 3, 정지 : 4',
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
CREATE TABLE `catagory` (
	`cate_id`		int 		NOT NULL,
	`cate_prtcode`	int			NULL		COMMENT '상위 카테고리 참조(참조키 설정)',
	`cate_name`		varchar(50)	NOT NULL,
	`cate_level`	tinyint		NOT NULL	COMMENT '상품 카테고리 : 1, 메뉴 카테고리 : 2 ...'
);

ALTER TABLE `catagory`
MODIFY `cate_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`cate_id`);


-- 호스트 정보 테이블
CREATE TABLE `Host_Guide` (
	`host_guide_id`	int 			NOT NULL,
	`user_id`		varchar(30)		NOT NULL	COMMENT '호스트 아이디',
	`cate_id`		int				NOT NULL,
	`title`			varchar(100)	NOT NULL,
	`description`	varchar(3000)	NOT NULL,
	`location`		varchar(100)	NOT NULL,
	`phone_number`	varchar(13)		NOT NULL,
	`created_at`	datetime		NOT NULL	DEFAULT now(),
	`updated_at`	datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Host_Guide`
MODIFY `host_guide_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`host_guide_id`);

ALTER TABLE `Host_Guide` 
ADD CONSTRAINT `FK_Hostuserinfo_TO_Host_Guide_1` 
FOREIGN KEY (`user_id`)
REFERENCES `Hostuserinfo` (`user_id`);

ALTER TABLE `Host_Guide` 
ADD CONSTRAINT `FK_catagory_TO_Host_Guide_1` 
FOREIGN KEY (`cate_id`)
REFERENCES `catagory` (`cate_id`);


-- 상품 테이블
CREATE TABLE `Products` (
	`product_id`	int 			NOT NULL,
	`host_guide_id`	int				NOT NULL,
	`name`			varchar(100)	NOT NULL,
	`price`			DECIMAL			NOT NULL	DEFAULT 0.00,
	`created_at`	datetime		NOT NULL	DEFAULT now(),
	`updated_at`	datetime		NOT NULL	DEFAULT now()
);

ALTER TABLE `Products`
MODIFY `product_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`product_id`);

ALTER TABLE `Products` 
ADD CONSTRAINT `FK_Host_Guide_TO_Products_1` 
FOREIGN KEY (`host_guide_id`)
REFERENCES `Host_Guide` (`host_guide_id`);


-- 날짜와 시간 단위로 나눈 예약 상품 테이블
CREATE TABLE `Availability` (
	`availability_id`		int 			NOT NULL,
	`product_id`			int				NOT NULL,
	`availability_datetime`	datetime		NOT NULL,
	`is_available`			char(1)			NOT NULL	DEFAULT 'Y'	COMMENT '가능 : Y, 불가능 : N',
	`additional_amount`		decimal(10, 2)	NOT NULL	DEFAULT 0.00,
	`discount_amount`		decimal(10, 2)	NOT NULL	DEFAULT 0.00,
	`max_capacity`			tinyint			NOT NULL	DEFAULT 1,
	`current_capacity`		tinyint			NOT NULL	DEFAULT 0
);

ALTER TABLE `Availability`
MODIFY `availability_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`availability_id`);

ALTER TABLE `Availability` 
ADD CONSTRAINT `FK_Products_TO_Availability_1` 
FOREIGN KEY (`product_id`)
REFERENCES `Products` (`product_id`);


-- 예약 테이블
CREATE TABLE `Reservations` (
	`reservation_id`	int 			NOT NULL,
	`availability_id`	int				NOT NULL,
	`user_id`			varchar(30)		NOT NULL	COMMENT '게스트 회원 아이디',
	`total_price`		decimal(10, 2)	NOT NULL,
	`status`			varchar(15)		NOT NULL	COMMENT 'Pending, Completed, Cancelled',
	`reservation_date`	datetime		NOT NULL	DEFAULT now(),
	`updated_at`		datetime		NOT NULL	DEFAULT now(),
	`is_review`			char(1)			NOT NULL	DEFAULT 'N'	COMMENT '리뷰 1개만 가능 하게'
);

ALTER TABLE `Reservations`
MODIFY `reservation_id` INT NOT NULL AUTO_INCREMENT,
ADD PRIMARY KEY (`reservation_id`);

ALTER TABLE `Reservations` 
ADD CONSTRAINT `FK_Availability_TO_Reservations_1`
FOREIGN KEY (`availability_id`)
REFERENCES `Availability` (`availability_id`);

ALTER TABLE `Reservations` 
ADD CONSTRAINT `FK_UserInfo_TO_Reservations_1` 
FOREIGN KEY (`user_id`)
REFERENCES `UserInfo` (`user_id`);


-- 결제 테이블
CREATE TABLE `Payments` (
	`payment_id`	int 	NOT NULL,
	`reservation_id`	int	NOT NULL,
	`payment_method`	varchar(30)	NOT NULL	COMMENT '카드, 계좌이체, pay 등',
	`amount`	decimal(10, 2)	NOT NULL,
	`status`	varchar(15)	NOT NULL	COMMENT 'Pending, Completed , Failed, Refunded',
	`transaction_id`	varchar(100)	NOT NULL,
	`payment_date`	datetime	NOT NULL	DEFAULT now(),
	`created_at`	datetime	NOT NULL	DEFAULT now(),
	`updated_at`	datetime	NOT NULL	DEFAULT now()
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
	`image_id`		int 			NOT NULL,
	`entity_type`	varchar(15)		NOT NULL	COMMENT 'Host_Guide, Products, Reviews',
	`entity_id`		int				NOT NULL	COMMENT '회원 아이디',
	`image_url`		varchar(255)	NOT NULL,
	`created_at`	datetime		NOT NULL	DEFAULT now()
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






















