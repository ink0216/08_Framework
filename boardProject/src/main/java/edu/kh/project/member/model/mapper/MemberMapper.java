package edu.kh.project.member.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper //인터페이스를 상속받은 클래스를 만들어서 이 인터페이스를 bean으로 등록
//mapper.xml과 연결돼있어야한다
public interface MemberMapper {
	//Mybatis에서는 DAO대신 Mapper 사용!!!
	
	/**로그인 SQL 실행
	 * @param memberEmail
	 * @return loginMember
	 */
	public Member login(String memberEmail);

	/**회원가입 SQL 실행
	 * @param inputMember
	 * @return result
	 */
	public int signup(Member inputMember);

	/**이메일 중복 검사
	 * @param memberEmail
	 * @return count
	 */
	public int checkEmail(String memberEmail);

	/**닉네임 중복 검사
	 * @param memberNickname
	 * @return count
	 */
	public int checkNickname(String memberNickname);

	/**전화번호 중복 검사
	 * @param memberTel
	 * @return count
	 */
	public int checkTel(String memberTel);

	/**모든 회원 조회
	 * @return
	 */
	public List<Member> selectMemberList();
}
