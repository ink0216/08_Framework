package edu.kh.project.myPage.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.service.MyPageService;
import lombok.RequiredArgsConstructor;

@Controller //컨트롤러 역할 명시 + bean 등록
@RequestMapping("myPage") //공통 주소 매핑
@RequiredArgsConstructor
public class MyPageController {
	private final MyPageService service; //알아서 의존성 주입이 될것이다!!(@RequiredArgsConstructor가 해줌)
	/**내 정보 조회/수정 화면으로 전환
	 * @param loginMember : 세션에 존재하는 loginMember를 얻어와 매개변수에 대입
	 * @param model : 데이터 전달용 객체 (기본 request scope)
	 * @return myPage-info.html로 forward
	 * 
	 */
	@GetMapping("info") //   /myPage/info GET 방식 요청 매핑
	public String info(
			@SessionAttribute("loginMember") Member loginMember,
/*<!-- 주소는 입력하는 곳은 세개인데 컬럼은 하나여서 하나의 문자열로 합쳐서 DB에 저장해둠 -->
                        <!-- 타임리프에서 쪼개는 거 이제 지원 안해서 DB에서 쪼개서 가져오든가 JS로 쪼개도 된다 -->
                        <!-- 백엔드에서 쪼개보자! -->
                        <!-- 서버 Controller에서 로그인한 회원 주소를 ^^^를 기준으로 쪼갤거다 -->
                        */
			Model model //forward같은 것 할 때 데이터 전달하는 객체(기본 request scope, 어노테이션 사용 시 세션으로도 올라간다)
			) {
		//로그인 정보 중 다 필요한 것 아니고 주소만 필요해서 주소만 꺼내옴
		String memberAddress = loginMember.getMemberAddress();
		
		//주소가 필수 입력이 아닌 선택사항이어서 없는(null인) 사람도 있다
		
		//주소가 있을 경우에만 동작할거다
		if(memberAddress !=null) {
			//쪼개기를 해보겠다
			
			String[] arr = memberAddress.split("\\^\\^\\^"); //구분자 "^^^"를 기준으로 쪼개서 String 배열로 반환
			// 정규표현식에서 ^가 시작이라는 뜻이 된다
			// \^로 \를 하나만 쓰면 백슬래시 하나는 escape 문자로 인식돼서
			//	백슬래시 두개쓰기( \\ == "\") (문자 그대로 인식하게 됨)
			//  "\\^" == "^"
			
			// "04540^^^서울시 중구 남대문로 120^^^2층 A강의장"
			// --> ["04540", "서울시 중구 남대문로 120", "2층 A강의장"]
			
			//이 배열을 모델에 세팅해서 보낼거다
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
		}
		return "myPage/myPage-info"; //  /templates/myPage/myPage-info.html로 forward
	}
	/**프로필 이미지 변경 화면으로 이동(forward)
	 * @return
	 */
	@GetMapping("profile")
	public String profile() {
		return "myPage/myPage-profile";
		
	}
	/**비밀번호 변경 변경 화면으로 이동(forward)
	 * @return
	 */
	@GetMapping("changePw")
	public String changePw() {
		return "myPage/myPage-changePw";
		
	}
	/**회원 탈퇴 화면으로 이동(forward)
	 * @return
	 */
	@GetMapping("secession")
	public String secession() {
		return "myPage/myPage-secession";
		
	}
	/**회원 정보 수정
	 * @param inputMember : 제출된 회원 닉네임, 전화번호, 주소가 담겨있다
	 * @param loginMember : 로그인한 회원 정보(이 중 회원 번호를 사용할 예정)
	 * @param memberAddress : 주소만 따로 받아온 String[]
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달하는 객체(flash)
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(
			//제출 받은 데이터를 받기
			/*@ModelAttribute*/ Member inputMember,
			//회원 번호가 일치하는 사람의 정보를 수정하겠다(PK여서)
			//세션에서 얻어오기
			@SessionAttribute("loginMember") Member loginMember,
			
			//주소를 세 칸으로 나눠서 입력받기 때문에 가공해야한다
			@RequestParam("memberAddress") String[] memberAddress,
			//주소만 따로 String 배열로 받아오기
			
			//리다이렉트할 때 메시지 보내고싶다
			RedirectAttributes ra
			) {
		
		//정보 수정하기
		
		//inputMember에 로그인한 회원번호 추가
		int memberNo = loginMember.getMemberNo(); //로그인한 회원의 번호를 꺼내와서 세팅
		inputMember.setMemberNo(memberNo); //loginMember에서 꺼내와서 inputMember에 넣기
		
		//회원 정보 수정 서비스 호출
		//DB에서 수정하면 int가 돌아온다
		int result = service.updateInfo(inputMember, memberAddress);
		//memberAddress를 넘겨서 가공할거다
		
		String message;
		
		//SQL의 업데이트 구문 수행 시 DB에서만 바뀐거다
		//홍길동이라고 DB에 저장돼있고 그걸로 로그인 시 홍길동이 세션에도 기록됨
		//고길동이라고 바꾼 경우, 세션의 값은 아직 홍길동이니까 
		//수정 성공 시 세션의 값도 바꾼 고길동으로 바꿔야한다
		//세션은 컨트롤러에서만 다룰 수 있다
		
		//자바 기본 자료형 8개 : byte, short, long, char, boolean, int, double, float
		//얘네만 값 자체를 저장 가능
		
		//그 외는 참조형 변수(주소를 저장해서 해당 주소의 데이터 참조)
		//매개변수로 받아온 loginMember에는, 객체가 아닌, 주소가 저장돼있다!!!
		//주소만 복사해서 나눠줌 -> 얕은 복사
		//loginMember에는 원본의 주소가 저장돼있고, 원본은 세션에 있다
		//변수를 수정 시 원본(세션의 정보)도 수정된다
		if(result>0) {
			//수정 성공
			message = "회원 정보 수정 성공!!";
			//loginMember는
			//세션에 저장된 로그인된 회원 정보가 저장된 객체를
			//참조하고 있다!!!
			// -> loginMember를 수정하면
			//		세션에 저장된 로그인된 회원 정보가 수정된다!!!
			//		== 세션 데이터가 수정된다
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
			//참조형은 주소만 대입된다 -> 그 참조형 변수값을 수정하면 그 주소의 원본도 수정된다!!!
			
			//inputMember
		}else {
			//실패
			message="회원 정보 수정 실패...";
		}
		ra.addFlashAttribute("message", message);
		//업데이트 성공/실패 시 내정보 조회하는 페이지로 리다이렉트
		return "redirect:info"; //get방식!
	}
	@PostMapping("changePw")
	public String changePw(
			@SessionAttribute("loginMember") Member loginMember,//회원 번호 얻어오려고
			@RequestParam Map<String, Object> paramMap, // 이거 쓰면 파라미터들이 다 들어가서 넘어온다
			RedirectAttributes ra
			
			
			) {
//		<!-- 제출될 때 새 비밀번호 확인은 새 비밀번호랑 동일한 지 비교할 용도여서
//        현재 비밀번호랑 새 비밀번호만 신경쓰면 된다
//    컨트롤러에 두 개를 파라미터를 전달해서 현재 비밀번호가 일치하는 사람의 비밀번호를 새 비밀번호로 바꿔라
//    누구인지는 세션에서 회원 번호 얻어와서 그 사람이 누구인지 확인하기
//    현재 비밀번호, 새 비밀번호, 회원번호 세 개를 묶어서 서비스로 보내서 호출
//
//    서비스에서 
//    -1. 바로 매퍼 호출하는 게 아니라 비크립트 암호화를 했기 때문에 DB에 비밀번호를 가져가서 비교할 수 없음
//    비크립트 암호화 된 비밀번호를 DB에서 조회해야 함(회원 번호 필요(몇 번 회원의 암호화된 비밀번호 가져와))
//    -2. 현재 비밀번호랑 암호화돼서 저장돼있던 비밀번호 비교
//    (BcryptPasswordEncoder.matches(평문, 암호화된 비밀번호) 메서드 이용) ->둘이 같으면 true, 다르면 false나옴
//        -> 같을 경우 -> 새 비밀번호를 암호화 진행(DB에 암호화해서 저장해야 함)
//                        새 비밀번호로 변경(UPDATE)하는 Mapper 호출
//                        (회원 번호, 새 비밀번호를 하나로 묶기(Member 또는 Map 또는 아무거나))
//                        (매퍼로 전달할 수 있는 파라미터는 항상 1개만 가능)
//                    -> 결과 1 또는 0 반환
//        -> 다를 경우 -> 실패 (0 반환하도록 하기)
//    서비스에서 SQL 두 번 수행하려면 매퍼 두 번 호출하면 된다
//
//[컨트롤러로 돌아옴]
//- 변경 성공 시 alert("비밀번호가 변경 되었습니다.") + /myPage/info 로 리다이렉트
//- 변경 실패 시(현재 비밀번호 틀린 경우) alert("현재 비밀번호가 일치하지 않습니다.") + /myPage/changePw로 리다이렉트 -->
		int memberNo = loginMember.getMemberNo();
		int result = 0;
		
		result = service.changePw(memberNo, paramMap);
		String path;
		String message;
		if(result>0) {
			path="/myPage/info";
			message="비밀번호가 변경 되었습니다.";
		}else {
			message="현재 비밀번호가 일치하지 않습니다.";
			path="/myPage/changePw";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:"+path;
	}
}
