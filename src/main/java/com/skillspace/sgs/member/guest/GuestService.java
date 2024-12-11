package com.skillspace.sgs.member.guest;

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

	public void join(GuestVO vo) {
		guestMapper.join(vo);
	}

	public GuestVO login(String user_id) {
		return guestMapper.login(user_id);
	}

	
	

}
