package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
/*@SessionAttributes( {"key", "key", ...}) : Model에 추가된 속성(Attribute) 중에서 
 * key값이 일치하는 특정 속성을 session scope로 변경해주는 어노테이션
 * */
@Controller //Bean으로 등록
@RequestMapping("member") //member로 시작하는 주소 여기로 매핑
@Slf4j //log(롬복)
@SessionAttributes({"loginMember"}) //모델 중에서 같은 키값가지는 거 있으면 세션으로 올려라
//								->Model객체 : 이렇게 해서 request랑 session scope 둘 다 커버 가능!!!!!!
//자바에서의 중괄호 == 배열 ->스트링 배열로 여러 키 등록해놓을 수 있다 ->밑의 모델이 세션으로 된다 
public class MemberController {
	@Autowired //의존성 주입(DI) //근데 나중에는 다른 방법으로 할거임!
	private MemberService service; //근데 나중에는 다른 방법으로 할거임!
	/* [로그인]
	 * - 특정 사이트에 아이디/비밀번호 등을 입력해서
	 * 	해당 정보가 있으면 조회/서비스 이용
	 * - 브라우저 끌 때까지 로그인한 정보가 남아있게 하려면 session(접속시에 생성되는 객체)에 기록하여
	 * 	로그아웃 또는 브라우저 종료 시까지 해당 정보를 계속 이용할 수 있게 함 
	 * 	(로그인 한 정보를 세션에 올리기)
	 * */
	/**로그인
	 * @param inputMember : 커맨드 객체(@ModelAttribute가 생략됨)
	 * @param RedirectAttributes ra: 리다이렉트 시 잠깐 세션에 올렸다가 다시 req로 내려와서 1회성으로 데이터 전달하는 객체
	 * @param model : 스프링에서 쓰는 데이터 전달용 객체(기본적으로 request scope)
	 * @param saveId : 아이디 저장 체크 여부
	 * @param resp : 쿠키 생성,추가를 위해 얻어온 객체
	 * @return "redirect:/"
	 */
	@PostMapping("login")
	public String login(Member inputMember,
			RedirectAttributes ra, 
			Model model,
			@RequestParam(value="saveId" , required=false) String saveId,
			HttpServletResponse resp //Cookie 만들기 위한 객체!(아이디 저장)
			//(value 쓰고 required false)안하면 필수 파라미터가 되어서 null인 경우 에러남(로그인 안됨)
			) {
		//커맨드 객체 : inputMember
		// - 요청 시 전달받은 파라미터를 같은 이름의 필드에 세팅한 객체
		//파라미터 여러개 넘어와도 하나의 객체에 다 담겨져 있는 것!
		//요청시 memberEmail, memberPw 있는데 Member의 필드에 
		//스프링이 기본 생성자로 객체 만들고 setter를 호출해서 필드에 세팅한 후 여기로 보내준다
		//inputMember안에는 memberEmail, memberPw가 세팅된 상태이다!!!!
		
		//커맨드객체 만들어지는 원리!
		//제출하는 인풋태그의 name속성값이 자료형의 필드명과 일치하면 자동으로 값이 들어간다
		//자동으로 값이 들어가는 원리
		//전달된 값이 두개인데 같은 이름의 필드가 있나 살펴보고, 있으면, 기본생성자로 객체 하나 만들고 setter가 실행됨
		//값 두개가 세팅된 객체를 inputMember자리에 집어넣는다
		
		//일반 암호화는 글자를 그냥 길게 만드는데 같은 글자 입력 시 똑같이 암호화됨->역이용해서 해킹 가능
		//but Bcrypt암호화는 복호화가 불가!! ->암호화한 비밀번호(DB에 존재)랑 원래 비밀번호를 matches함수를 이용해서 판단!
		
		//체크박스에 value가 없을 때
		// - 체크가 된 경우   -> 체크 되면 on 나옴(null아님)
		// - 체크가 안 된 경우 -> null이 나옴
		log.debug("saveId : " +saveId); //saveId 얻어오기
		//로그인 서비스 호출
		Member loginMember = service.login(inputMember); //입력 받은 회원정보(inputMember)를 넘겨주겠다
		
		//로그인 실패 시 
		if(loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
		}
		//세분화해서 아이디가 틀린 경우, 비밀번호가 틀린 경우로 나누지 않음!(보안 상 이유!)
		
		//로그인 성공 시 ->요즘은 else 잘 안씀
		if(loginMember !=null) {
			//Session scope에 loginMember 추가 
			model.addAttribute("loginMember", loginMember); 
			//리다이렉트하는데 request scope로 설정함!
			// 1단계 : request scope에 세팅됨
			// 2단계 : 클래스 위에 @SessionAttributes() 어노테이션 작성하면
			//			session scope로 이동됨
		//***************************************************************
		//아이디 저장(Cookie) == 브라우저에 쌓이는 데이터
		//브라우저 : 클라이언트 컴퓨터에 설치된 것
		//쿠키 == 클라이언트 쪽에 데이터를 저장시키는 것 (서버에 저장하는 것 아님)
			
		//쿠키 객체 생성
		Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail()); //key value 형식
		//이메일을 저장
		
		
		//클라이언트가 어떤 요청을 할 때 쿠키가 첨부될 지 지정
		//ex) "/"   :IP 또는 도메인 또는 localhost 뒤에 "/" -->메인 페이지 + 그 하위 주소들 의미
		//cookie.setPath("/"); 메인 페이지 및 그 하위 주소들 요청 오면 그때 마다 다 쿠키 담아서 보내주겠다
		cookie.setPath("/");
			
		//쿠키에 만료 기간 기록하기
		if(saveId !=null) {
			//아이디 저장 체크 시(아이디 저장 하겠다는 거)
			cookie.setMaxAge(30*24*60*60); //최대 나이가 몇인지 "초 단위"로 지정 ->60 쓰면 60초동안 유지하겠다
			//30일을 초단위로 지정
		}else {
			//아이디 저장 미체크시
			cookie.setMaxAge(0); //0초 -> 클라이언트의 쿠키 삭제
			//클라이언트가 saveId라는 쿠키 가지고 있었는데
			//29일 남았을 경우
			//로그인하고 새 쿠키 받아왔는데 또 saveId를 받아왔는데 0초짜리면 29일짜리가 0초짜리로 덮어쓰기돼서 들어가자마자 사라짐
			//들어가자마자 클라이언트의 쿠키를 삭제
		}
		
		
		//응답 객체에 쿠키 추가하기 -> 클라이언트로 전달된다
		resp.addCookie(cookie);
		
		//타임리프는 화면 출력 용이고, 쿠키를 JS에서 다뤄보자!
		//***************************************************************
			
		}
		return "redirect:/"; //일단 메인페이지 재요청
	}
	/**
	 * @param SessionStatus : 세션 할 일 다했으니까 세션 없애겠다 하는 애 (@SessionAttributes로 올라간 세션만 완료시킨다!!!!)
	 * 						세션을 완료(없앰)시키는 역할의 객체 
	 * 						->이 코드로 완료를 시키면 기존의 세션이 사라지고 request처럼 다시 세션 객체가 새로 만들어짐!!!
	 * 				- //@SessionAttributes로 등록된 세션을 완료시킴
	 * 				- 서버에서 기존 세션 객체가 사라짐과 동시에 새로운 세션 객체가 생성되어 클라이언트와 다시 연결된다
	 * @return "redirect:/"
	 */
	@GetMapping("logout")
	public String logout(
			//로그아웃 == Session에 저장된 로그인된 회원 정보를 없애는(만료시키는, 무효화시키는, "완료시키는") 것!!!
			SessionStatus status //세션 상태
			) {
		
//		status.isComplete() :만료되었는지 확인하는 것
		status.setComplete(); //세션을 완료 시킴(없앰) ->왼쪽에 나오던, 세션에 저장돼있던 회원 정보도 같이 사라진다!!(세션이 사라져서)
		return "redirect:/"; //메인페이지 리다이렉트
		//로그인멤버만 없애고싶으면 
		//HttpSession session으로 해서 session.removeAttribute("loginMember")로 하나씩 없앨 수 있다
		//세션 만료 시간 SessionConfig 객체 만들어서 시간 지정 가능
	}
	/**로그인 페이지 이동
	 * @return
	 */
	@GetMapping("login")
	public String loginPage() {
		return "member/login"; //login.html로 포워드
	}
	/**회원 가입 페이지 이동
	 * @return
	 */
	@GetMapping("signup")
	public String signupPage() {
		return "member/signup";
	}
	/**회원 가입
	 * @param inputMember : 입력된 회원 정보
	 * 			(memberEmail, memberPw, memberNickname, memberTel)
	 * @param memberAddress : 입력한 주소 input 3개의 값을 배열로 전달
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달하는 객체
	 * @return
	 * 
	 *
	 */
	@PostMapping("signup")
	public String signup(
			/*@ModelAttribute*/ Member inputMember,
			//주소가 세개 -> DTO에 담길 때 덮어쓰기가 돼서 
			//(memberEmail, memberPw, memberNickname, memberTel)
			@RequestParam("memberAddress") String[] memberAddress, //String 배열
			RedirectAttributes ra //리다이렉트 시 메시지 전달용 객체!
			) {
		//input태그들 name속성 파라미터 넘어옴
		//이메일, 어스키, 비밀번호, 비밀번호 확인(js), 닉네임, 전화번호, 주소 ->MemberDTO 필드명
		//@RequestParam으로 하나씩 얻어오지 말고
		//@ModelAttribute로 한 번에 받아오기
		
		//전달 받은 두 개를 서비스에 다 넘기기
		
		//회원 가입 서비스 호출
		//서비스에서는 매퍼 호출
		//회원가입 == INSERT -> int가 반환됨
		int result = service.signup(inputMember, memberAddress);
		String path = null;
		String message=null;
		if(result>0) {
			//가입 성공 시
			message=inputMember.getMemberNickname()+"님의 가입을 환영 합니다!!!😀😀😀";
			path="/";//메인페이지로
		}else {
			message="회원 가입 실패...";
			path="signup"; //앞에 / 들어가면 절대경로 안들어가면 상대경로 
			//상대경로 하면 뒷 부분이 바뀌는데 signup이 되는데 get방식으로 바뀌어서 회원가입 화면으로 돌아가도록
		}
		ra.addFlashAttribute("message", message); //footer.html에 이 메시지 처리하는 스크립트가 있다
		return "redirect:"+path; //path로 리다이렉트
	}
}
/*Cookie란?
 * - 클라이언트 측(브라우저)에서 관리하는 데이터(옛날에는 파일 형식으로 저장, )
 * - Cookie에는 만료 기간(쿠키 파일이 언제까지 유지될 지), 데이터(key=value 형태로 되어있음), 쿠키를 사용하는 사이트(주소)가
 * 			기록되어 있음
 * - 클라이언트가 쿠키에 기록된 사이트로 요청을 보낼 때, 요청에 해당 사이트에 적용되는 쿠키가 담겨져서 서버로 넘어간다
 * 		(파라미터만 넘어가는 줄 알았는데 쿠키도 같이 넘어간다)
 * - Cookie의 생성, 수정, 삭제는 Server가 관리
 * 		but Cookie 저장은 Client가 함!
 * - Cookie는 HttpServletResponse를 이용해서 생성해야 하고, 
 * 	클라이언트에게 전달(응답)할 수 있다
 * */

