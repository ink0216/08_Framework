package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;

@Service //비즈니스 로직 처리 역할 + Bean 등록
public class MemberServiceImpl implements MemberService{ //샘플 멤버 비밀번호== pass01!
	@Autowired  //등록된 bean 중에서 같은 타입 또는 상속 관계인 bean을
	//자동으로 여기에 의존성 주입(DI)해준다
	private MemberMapper mapper;
	
	//BCrypt 암호화 객체 의존성 주입(Security Config 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	//로그인 서비스
	@Override
	public Member login(Member inputMember) {
		// 테스트(디버그모드로)
		//bcrypt.encode(문자열) : 문자열을 암호화시켜서 반환해줌!
		//String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		
		//1. 이메일이 일치하면서 탈퇴하지 않은 회원 조회하기
		Member loginMember = mapper.login(inputMember.getMemberEmail()); //이메일은 inputMember에 있다
		
		//2. 만약에 일치하는 이메일이 없어서 조회 결과가 null인 경우
		if(loginMember ==null) return null; //할 거 없으니 돌아갓!!
		
		//조회된 게 있으면 밑으로 내려옴
		//3. 입력 받은 비밀번호(inputMember.getMemberPw() (암호화 안 된 평문))와
		//	암호화된 비밀번호(loginMember.getMemberPw())
		//	두 비밀번호가 일치하는지 확인하기
		if( !bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			//일치하지 않으면
			//rawPassword : 암호화 하기 전의 비밀번호 
			//encodedPassword : 암호화 된 비밀번호
			return null; //이메일은 일치했는데 비밀번호는 틀린 상황!!!!!
			
		}
		//if문 건너왔으면 이메일, 비밀번호 일치하는 경우!
			
		//로그인 결과에서 비밀번호 제거하기 (보안 때문에)
		loginMember.setMemberPw(null);
		return loginMember;
		
		/*로그인 할 때 SQL :
		 * SELECT * FROM "MEMBER" WHERE MEMBER_EMAIL = ? AND MEMBER_PW = ?
		 * 비밀번호를 그냥 텍스트로 저장하면 안됨 -> 암호화(복호화 안되는 암호화 방법 이용해야 함)
		 * 옛날에는 비밀번호 찾기 하면 평문 비밀번호를 보여줬는데(DB에 평문으로 저장돼있다는 뜻)
		 * 요즘에는 그러면 안돼서 비밀번호 새로 세팅하도록 함!
		 * 암호화 : Hash함수(문자열을 특정 길이 문자열로 변환시키는 함수) 이용
		 * hashcode : 주소를 다른 모양으로 바꾸는 것
		 * ex) 1234 -> werdsdfg 
		 * 요즘 BCrypt 암호화 이용!
		 * */
		
	}
}
/*BCrypt 암호화(비크립트)
 *  - 입력된 문자열(비밀번호)에 salt를 추가(항상 같은 위치에,같은 양 넣는 것 불가능)한 후 암호화함
 *  	->암호화 할 때 마다 결과가 달라진다
 *  ex) 1234입력 -> $12@asdfew
 *  	다시 1234입력 -> $12@werwg //앞부분은 조금 일치하는데 뒷부분이 완전히 달라지는 암호화 방법
 *  				->Spring security에서 제공하는 기능이어서 DB에서 AND MEMBER_PW = ?로 비교 불가!!!
 *   - 비밀번호 확인 방법
 *   	->DB에 저장된 비밀번호를 가져옴
 *   	->BCryptPasswordEncoder.matches(평문 비밀번호, 암호화된 비밀번호) -> 1234 - $12@werwg가 대응되는 것인지 검사해줌
 *   	->평문 비밀번호와 암호화된 비밀번호가 같은 경우 true, 아니면 false를 반환해주는 메서드!
 *   	->비밀번호를 DB에 보내주는 것이 아닌, DB에 암호화돼서 저장돼있는 비밀번호를 스프링으로 가져와야 한다
 *   * 로그인 / 비밀번호 변경 / 탈퇴 등 비밀번호가 입력돼야 하는 경우,
 *   	DB에 저장된 암호화된 비밀번호를 조회해와서 matches() 메서드로 비교해야 한다!!!!!
 * */
