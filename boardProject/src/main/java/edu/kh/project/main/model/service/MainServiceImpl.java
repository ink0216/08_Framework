package edu.kh.project.main.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.main.model.mapper.MainMapper;
import lombok.RequiredArgsConstructor;
@Transactional
@Service
@RequiredArgsConstructor //mapper, bcrypt 의존성 주입 final
public class MainServiceImpl implements MainService{
	private final BCryptPasswordEncoder bcrypt; //암호화 하려고!
	private final MainMapper mapper;
	
	//비밀번호 초기화
	@Override
	public int resetPw(int inputNo) {
		//초기화 할 비밀번호 설정
		String pw = "pass01!"; //이 상태로 DB에 넣으면 안되고 암호화 하기!!!!
		String encPw = bcrypt.encode(pw);
		
		//값 두개를 매퍼에 보내야 하는데 매퍼에는 하나밖에 못보내서 묶기
		Map<String, Object> map = new HashMap<>();
		//Object 클래스는 모든 클래스의 최상위 부모클래서여서
		//다형성 적용돼서 부모 타입 참조 변수에 자식 타입 변수 대입할 수 있다
		//int는 클래스가 아닌데 Object자리에 기본 자료형 넣으려면
		//Wrapper Class를 이용해서 기본 자료형이 객체로 다뤄져야 할 때 AutoBoxing이 수행돼서 int가 Integer 객체로 변함
		//Integer로 변한 값이 map에 들어가는거다
		map.put("inputNo", inputNo);
		map.put("encPw", encPw);
		return mapper.resetPw(map);
	}
	//회원 번호 입력받아서 회원 삭제하는 것도 해보기!
	
	//탈퇴 복구
	@Override
	public int outReset(int outResetMemberNo) {
		
		/*1. 없는 회원 번호 작성
		 * 2. 있는 회원 번호인데 탈퇴 상태 아닌 경우
		 * 3. 탈퇴 상태인 회원인 경우
		 * */
		/**<!-- 탈퇴한 회원인지 확인 -->
	<select id="checkOut" (탈퇴상태이고 그 번호인 회원 수 조회 0 or 1)
	
	<!-- 탈퇴 회원 복구 --> (해당 번호 사람 N으로 바꾸기)
	<update id="outReset">
	
	<!-- 해당 번호의 회원 존재하는지 카운트 -->(해당 사람 존재하는 지 수 0 or 1)
	<select id="select" resultType="_int">
		 * 0 == 등록회원 아님 
		 * 1== 탈퇴 복구 성공
		 * 2==탈퇴상태 회원 아님
		 */
		/*일단 회원인지 확인해서 회원 아니면 리턴
		 * 회원이면 그때 탈퇴 여부 확인해서 탈퇴 상태 아니면 리턴
		 * */
		int result1 = mapper.select(outResetMemberNo);
		if(result1 ==0) {
			return 3;
		}
		int result2 = mapper.checkOut(outResetMemberNo);
		if(result2==0) {
			return 2;
		}
		return mapper.outReset(outResetMemberNo);
		
		
		
	}
}
