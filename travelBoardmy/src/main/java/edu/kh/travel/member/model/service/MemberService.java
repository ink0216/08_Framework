package edu.kh.travel.member.model.service;

import edu.kh.travel.member.model.dto.Member;

public interface MemberService {

	/**로그인 서비스
	 * @param inputMember
	 * @return
	 */
	Member login(Member inputMember);
	
}
