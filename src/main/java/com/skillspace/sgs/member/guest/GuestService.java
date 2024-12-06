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

}
