package com.skillspace.sgs.guest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GuestService {
	
	private final GuestMapper guestMapper;

	public String idCheck(String user_id) {
		return guestMapper.idCheck(user_id);
	}

	public String nickCheck(String user_nick) {
		return guestMapper.nickCheck(user_nick);
	}

	public void join(GuestDTO dto) {
		guestMapper.join(dto);
	}

	public GuestDTO login(String user_id) {
		return guestMapper.login(user_id);
	}

	public String idsearch(String user_name, String user_email) {
		return guestMapper.idsearch(user_name, user_email);
	}

	public String pwtemp_confirm(String user_id, String user_email) {
		return guestMapper.pwtemp_confirm(user_id, user_email);
	}

	public void pwchange(String user_id, String user_pw) {
		guestMapper.pwchange(user_id, user_pw);
	}

	public GuestDTO modify(String user_id) {
		return guestMapper.modify(user_id);
	}

	public String emailCheck(String user_email) {
		return guestMapper.emailCheck(user_email);
	}

	public void modify_save(GuestDTO vo) {
		guestMapper.modify_save(vo);
	}

	
	

}
