package edu.kh.project.member.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
@Transactional //선언적 트랜잭션
//해당 클래스 메서드 종료 시까지 예외가 발생하지 않으면 자동으로 commit, 중간에 예외(RuntimeException) 발생 시 자동으로 rollback
//서비스에서 매퍼 호출하고 결과에 따라 트랜잭션 제어 처리 해야하는데 이 어노테이션 쓰면 자동으로 된다
//	(AOP 기반의 기술)(코드 중간중간에 다른 코드를 끼워 넣는 것)
//Controller   <- Service
//				|
//			커밋?롤백?
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
	//회원가입 서비스
	@Override
		public int signup(Member inputMember, String[] memberAddress) {
		//주소가 아무것도 입력되지 않으면
		//inputMember.getMemberAddress() -> ",," 모양
		//memberAddress -> [,,] 모양
		//주소 입력창은 세칸인데 DB에 주소 저장은 컬럼 하나임
		//->세개를 하나로 합쳐서 DB에 넣어야함
		
		//주소에 , 들어가는 경우 있어서 나중에 DB에서 꺼내서 쓸 때에 문제가 됨
		
		
		if( !inputMember.getMemberAddress().equals(",,")) {
			//주소가 입력된 경우!
			
			//구분자로 "^^^" 쓴 이유 : 주소, 상세 주소에 없는 특수문자 아무거나 작성 
			//->나중에 DB에서 합쳐서 저장돼있던 주소 꺼내와서 3분할 할 때 그걸 기준으로 쪼개려고!
			String address = String.join("^^^", memberAddress); //"a^^^b^^^c"
			
			//String.join("구분자", 배열)
			// - 배열의 모든 요소 사이에 "구분자"를 추가하여
			//	하나의 문자열로 만드는 메서드
			
			inputMember.setMemberAddress(address); 
			//Mybatis는 파라미터를 하나밖에 못받아서 묶어서 한 번에 보내기 위해 inputMember를 통째로 다시 세팅
		}else {
			//주소가 입력되지 않았을 때
//			주소는 not null 제약조건 없어서 null저장 가능
			inputMember.setMemberAddress(null); //null 저장 
			
		}
		//inputMember에 비밀번호 등 들어있어서 그대로 DB에 추가하면 안된다!(암호화)
		//비밀번호를 암호화 하여 inputMember에 세팅
		String encPw = bcrypt.encode(inputMember.getMemberPw()); //암호화한 비밀번호
		//입력받은 비밀번호를 암호화 한 것을 받아와서
		inputMember.setMemberPw(encPw); //암호화한 비밀번호로 다시 세팅해라
		
		//회원가입 매퍼 메서드 호출
		//	->Mybatis에 의해서 자동으로 INSERT하는 SQL이 수행된다!
		//		(매퍼 메서드 호출 시 SQL에 사용할 파라미터는 1개만 전달 가능하다!!!!)
			return mapper.signup(inputMember);
		}
	
	//이메일 중복 검사
	@Override
		public int checkEmail(String memberEmail) {
			return mapper.checkEmail(memberEmail);
		}
	//닉네임 중복 검사
	@Override
		public int checkNickname(String memberNickname) {
			return mapper.checkNickname(memberNickname);
		}
	//전화번호 중복 검사
	@Override
		public int checkTel(String memberTel) {
			return mapper.checkTel(memberTel);
		}
	//빠른 로그인 -> 위의 일반 로그인에서 비밀번호 비교만 제외하면 된다(빠른 로그인 시 비밀번호 입력은 안할거라서!)
	@Override
		public Member quickLogin(String memberEmail) {
		Member loginMember = mapper.login(memberEmail); 
		if(loginMember ==null) return null; //탈퇴 또는 없는 회원인 경우
		
		//조회된 비밀번호 null로 변경하기
		loginMember.setMemberPw(null);
			return loginMember;
		}
	
	//모든 회원 조회
	@Override
		public List<Member> selectMemberList() {
			return mapper.selectMemberList();
		}
	//회원 삭제
	@Override
		public int delete(int memberNo) {
			return mapper.delete(memberNo);
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
