package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.main.model.mapper.MainMapper;
import edu.kh.project.main.model.service.MainService;
import lombok.RequiredArgsConstructor;

@Controller //bean(스프링이 관리하는 객체) 등록
@RequiredArgsConstructor //service 의존성 주입 final
public class MainController {
	private final MainService service;
	@RequestMapping("/") // "/" 요청 매핑(method 가리지 않음(get이든 post든 뭐든 다 받음))
	public String mainPage() {
		return "common/main"; //common에 있는 main.html로 포워드하겠다
	}
	/**비밀번호 초기화
	 * @param inputNo : 비밀번호 초기화 할 회원 번호
	 * @return result
	 */
	@PutMapping("resetPw")
	@ResponseBody //
	public int resetPw(
			@RequestBody int inputNo
			//전달받은 데이터는 fetch로 요청 보낼 때 body에 회원번호 담아서 보냈다 -> body에서 꺼내야 함!!!
			) {
		
		return service.resetPw(inputNo);
	} 
	/**탈퇴 복구
	 * @param outResetMemberNo
	 * @return
	 */
	@ResponseBody
	@PutMapping("outReset")
	public int outReset(
			@RequestBody int outResetMemberNo
			) {
		return service.outReset(outResetMemberNo);
	}
	//ra는 컨트롤러에서만 사용 가능한 객체!
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		//LoginFilter -> /loginError를 리다이렉트 한 거임
		//	-> message를 만들어서 다시 메인페이지로 리다이렉트 하는 코드
		//		필터에서 여기로 리다이렉트 했는데 여기서 메시지만 담아서 다시 리다이렉트 함!
		ra.addFlashAttribute("message", "로그인 후 이용해 주세요");
		return "redirect:/"; //메인페이지에서 이 메시지가 출력되면서 메인페이지로 넘어온다
	}
}
