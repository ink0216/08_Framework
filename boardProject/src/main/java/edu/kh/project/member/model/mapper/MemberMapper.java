package edu.kh.project.member.model.mapper;

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
}
