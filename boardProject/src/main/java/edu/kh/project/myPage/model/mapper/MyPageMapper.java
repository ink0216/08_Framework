package edu.kh.project.myPage.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper //mapper.xml의 SQL을 실행하는 역할
public interface MyPageMapper {

	/**회원 정보 수정
	 * @param inputMember
	 * @return result
	 */
	int updateInfo(Member inputMember);

	/**저장돼있던 비밀번호 조회
	 * @param memberNo
	 * @return 암호화 돼서 저장돼있던 비밀번호
	 */
	String selectPw(int memberNo);

	/**비밀번호 변경
	 * @param map
	 * @return result
	 */
	int changePw(Map<String, Object> paramMap);

	/**회원 탈퇴
	 * @param memberNo
	 * @return result
	 */
	int secession(int memberNo);

	
}
