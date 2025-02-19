package com.skillspace.sgs.host;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class HostService {
	
	private final HostMapper hostMapper;
	
	public void join(HostDTO vo) {
		hostMapper.join(vo);
	}

}
