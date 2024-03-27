package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import lombok.extern.slf4j.Slf4j;
/*@SessionAttributes( {"key", "key", ...}) : Model에 추가된 속성(Attribute) 중에서 
 * key값이 일치하는 특정 속성을 session scope로 변경해주는 어노테이션
 * */
@Controller //Bean으로 등록
@RequestMapping("member") //member로 시작하는 주소 여기로 매핑
@Slf4j //log(롬복)
@SessionAttributes({"loginMember"}) //자바에서의 중괄호 == 배열 ->스트링 배열로 여러 키 등록해놓을 수 있다 ->밑의 모델이 세션으로 된다 
public class MemberController {
	@Autowired //의존성 주입(DI) //근데 나중에는 다른 방법으로 할거임!
	private MemberService service; //근데 나중에는 다른 방법으로 할거임!
	/* [로그인]
	 * - 특정 사이트에 아이디/비밀번호 등을 입력해서
	 * 	해당 정보가 있으면 조회/서비스 이용
	 * - 브라우저 끌 때까지 로그인한 정보가 남아있게 하려면 session에 기록하여
	 * 	로그아웃 또는 브라우저 종료 시까지 해당 정보를 계속 이용할 수 있게 함 
	 * 	(로그인 한 정보를 세션에 올리기)
	 * */
	/**로그인
	 * @param inputMember : 커맨드 객체(@ModelAttribute가 생략됨)
	 * @param RedirectAttributes : 리다이렉트 시 잠깐 세션에 올렸다가 다시 req로 내려와서 1회성으로 데이터 전달하는 객체
	 * @param model : 스프링에서 쓰는 데이터 전달용 객체(기본적으로 request scope)
	 * @return
	 */
	@PostMapping("login")
	public String login(Member inputMember,
			RedirectAttributes ra, 
			Model model
			) {
		//커맨드 객체 : 
		// - 요청 시 전달받은 파라미터를 같은 이름의 필드에 세팅한 객체
		//파라미터 여러개 넘어와도 하나의 객체에 다 담겨져 있는 것!
		//요청시 memberEmail, memberPw 있는데 Member의 필드에 
		//스프링이 기본 생성자로 객체 만들고 setter를 호출해서 필드에 세팅한 후 여기로 보내준다
		//inputMember안에는 memberEmail, memberPw가 세팅된 상태이다!!!!
		
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
			// 2단계 : 클래스 우에 @SessionAttributes() 어노테이션 작성하면
			//			session scope로 이동됨
			
		}
		return "redirect:/"; //일단 메인페이지 재요청
	}
}
