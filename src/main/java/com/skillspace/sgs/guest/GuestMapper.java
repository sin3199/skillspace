package com.skillspace.sgs.guest;

import org.apache.ibatis.annotations.Param;

public interface GuestMapper {
	
	// 아이디 중복체크
	String idCheck(String user_id);
	
	// 닉네임 중복체크
	String nickCheck(String user_nick);

	// 회원가입 저장
	void join(GuestDTO dto);
	
	// 로그인 처리 
	GuestDTO login(String user_id);
	
	// 회원수정 폼
	GuestDTO modify(String user_id);

	//회원수정 처리
	void modify_save(GuestDTO dto);

	// mapper 인터페이스에서 파라미터가 2개이상이면, @Param 어노테이션을 사용해야 한다.
	// 비밀번호 변경 처리
	void pwchange(@Param("user_id") String user_id, @Param("user_pw") String user_pw); 
	
	// 아이디 찾기
	String idsearch(@Param("user_name") String user_name, @Param("user_email") String user_email);
	
	// 임시 비밀번호, 아이디 이메일 일치 되는 이메일 리턴
	String pwtemp_confirm(@Param("user_id") String user_id, @Param("user_email") String user_email);

	String emailCheck(String user_email);

}
