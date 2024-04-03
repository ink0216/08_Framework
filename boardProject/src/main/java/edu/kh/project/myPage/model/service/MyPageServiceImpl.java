package edu.kh.project.myPage.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;

@Service //bean 등록
@Transactional //수정 삭제 이런거 할거니까
@RequiredArgsConstructor //final 필드(mapper)의 값을 의존성 주입해주는 코드를 자동완성해주는 어노테이션!!!!
public class MyPageServiceImpl implements MyPageService{
	private final MyPageMapper mapper;
	
	//BCrypt 암호화 객체 의존성 주입(Security Config 참고)
	//@Autowired //이건 권장 X -> @RequiredArgsConstructor를 쓰는 게 좋은데 쓸려면 final 필드여야 한다!
		private final BCryptPasswordEncoder bcrypt;
	
	//@RequiredArgsConstructor를 이용했을 때 자동 완성되는 구문
//	@Autowired
//	public MyPageServiceImpl(MyPageMapper mapper) {
//		this.mapper=mapper;
//	}
	
	//회원 정보 수정
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		//입력된 주소가 있을 경우 memberAddress를 A^^^B^^^C 형태로 가공
		//주소 입력 X -> inputMember.getMemberAddress() -> ",," 이런 모양으로 돼있다
		if(inputMember.getMemberAddress().equals(",,")) {
			//주소 입력이 안됐을 경우
			inputMember.setMemberAddress(null); //주소에 null대입
		}else {
			//주소가 입력된 것이 있다 -> memberAddress를 A^^^B^^^C 형태로 가공
			//String.join ==문자열이나 배열이 넘어오면 사이사이에 앞의 기호를 구분자로서 넣는다 
			String address = String.join("^^^", memberAddress);
			
			//주소에 가공된 데이터 대입
			inputMember.setMemberAddress(address);
			//이제 inputMember에는 번호, 주소, 닉네임, 전화번호가 들어있다
		}
		//SQL 수행 후 결과 반환
		return mapper.updateInfo(inputMember);
	}
	//비밀번호 변경
	@Override
	public int changePw(int memberNo, Map<String, Object> paramMap) {
		
		int result=0;
		//해당 회원의 ,저장돼있는 암호화된 비밀번호 조회
		String beforePw = mapper.selectPw(memberNo);
		
		if(bcrypt.matches((String)paramMap.get("currenPw"), beforePw)) {
			//(String)paramMap.get("currenPw")는 Object타입이니까 String 타입으로 강제형변환
			//matches 는 String타입을 비교
			//일치하면
			//MemberService의 회원 가입 서비스 참고하기!!!(암호화해서 저장해야 함)
			String encPw = bcrypt.encode((String)paramMap.get("newPw")); //새 비밀번호 암호화
			//얘도 Object 타입이어서 String으로 형변환 해야 한다
			
			//새 비밀번호로 변경(MAPPER)하는 Mapper 호출
			//Mapper에 전달 가능한 파라미터는 1개 뿐!!!
			//매퍼에는 하나만 보낼 수 있어서 두개를 보내야 하면 묶어서 보내기!!!!
			paramMap.put("encPw", encPw);
			paramMap.put("memberNo", memberNo);
			
			result = mapper.changePw(paramMap);
		}else {
			//일치하지 않으면
			result=0;
		}
		
		return result;
	}
}
