package com.skillspace.sgs.host;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HostDTO {
	
	private String			user_id;
	private String			host_name;
	private String			host_zipcode;
	private String			host_addr;
	private String			host_addrdetail;
	private String			host_phone;
	private String			description;
	private int				host_status;	// 활동 : 1, 휴면 : 2, 탈퇴 : 3, 정지 : 4
	private String			approval;		// 호스트 가입 승인 : Y, N
	private LocalDateTime	created_at;
	private LocalDateTime	updated_at;

}
