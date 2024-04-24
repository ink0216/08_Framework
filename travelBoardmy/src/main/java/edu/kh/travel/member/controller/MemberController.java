package edu.kh.travel.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.travel.member.model.dto.Member;
import edu.kh.travel.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("member")
@Slf4j
@SessionAttributes({"loginMember"}) //모델 중에서 같은 키값가지는 거 있으면 세션으로 올려라
//->Model객체 : 이렇게 해서 request랑 session scope 둘 다 커버 가능!!!!!!
//자바에서의 중괄호 == 배열 ->스트링 배열로 여러 키 등록해놓을 수 있다 ->밑의 모델이 세션으로 된다 
public class MemberController {
	
	@Autowired
	private MemberService service;
//	회원 로그인 서비스
	
	@PostMapping("login")
	public String login(
			Member inputMember, //일단은 로그인 해야만 게시판 들어갈 수 있도록
			RedirectAttributes ra,
			Model model,
			@RequestParam(value = "saveId", required = false) String saveId,
			@RequestParam("selectContinent") String selectContinent,
			HttpServletResponse resp) {
		
		Member loginMember = service.login(inputMember);
		// 로그인 정보가 일치하지 않을 경우
		if(loginMember == null) {
			
			ra.addFlashAttribute("message","로그인을 해야 게시판 이용이 가능합니다.");
			return "redirect:/";
		}
		else {
			//Session scope에 loginMember 추가 
			model.addAttribute("loginMember", loginMember); 
			
			
			// 아이디 저장
			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
			
			//클라이언트가 어떤 요청을 할 때 쿠키가 첨부될 지 지정
					//ex) "/"   :IP 또는 도메인 또는 localhost 뒤에 "/" -->메인 페이지 + 그 하위 주소들 의미
					//cookie.setPath("/"); 메인 페이지 및 그 하위 주소들 요청 오면 그때 마다 다 쿠키 담아서 보내주겠다
			cookie.setPath("/");
			
			if(saveId != null ) {
				
				cookie.setMaxAge(30*24*60*60);
				
			} else {
				
				cookie.setMaxAge(0);
				
			}
			resp.addCookie(cookie);
			//ra.addFlashAttribute("message","해당 회원이 존재합니다!!!!");
			return "redirect:/board/"+selectContinent; //selectContinent로 해당 게시판으로 이동되도록 PathVariable
		} 
		
		
	}
}
