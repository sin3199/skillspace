package com.skillspace.sgs.guest;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GuestDTO {
	
	private String 	user_id;
	private String 	user_pw;
	private String 	user_name;
	private String 	user_nick;
	private String 	user_email;
	private String 	user_phone;
	private String 	user_zipcode;
	private String 	user_addr;
	private String 	user_addrdetail;
	private String 	user_email_receive;
	private String 	role;
	private Date 	created_at;
	private Date 	updated_at;
	private int 	user_status;
	

}
