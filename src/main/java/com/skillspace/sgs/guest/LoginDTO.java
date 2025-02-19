package com.skillspace.sgs.guest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginDTO {

	private String user_id;
	private String user_pw;
}
