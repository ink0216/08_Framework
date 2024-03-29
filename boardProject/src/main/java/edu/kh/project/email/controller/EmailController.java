package edu.kh.project.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.kh.project.email.model.service.EmailService;
import edu.kh.project.email.model.service.EmailServiceImpl;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("email")
@RequiredArgsConstructor //final 필드에 자동으로 의존성 주입을 해준다 ->상수여도 값 안넣어도 에러 안난다
							//@Autowired 생성자 방식 코드 자동 완성
							// ->그럼 여기에 의존성 주입(DI) 된다
@SessionAttributes({"authKey"}) //model 값 중 해당 key가지는 값을 session scope로 변경
public class EmailController {

	private final EmailService service;
	
	@PostMapping("signup")
	@ResponseBody //값 그대로 fetch로 반환
	public int signup(@RequestBody String email,
			//이메일이 넘어오면 그 이메일로 인증번호 보내야된다
			Model model
			) {
		String authKey = service.sendEmail("signup", email); //이메일 보내는 서비스 호출
		
		if(authKey !=null) {
			//인증번호가 반환돼서 돌아옴 == 이메일 보내기가 성공
			
			//이메일로 전달한 인증번호를 Session에 올려두기->그러면 브라우저를 끄지 않는 이상 계속 유지된다 ->Model 필요!
			//사람이 입력한 것과 세션에 있는 것을 비교
			model.addAttribute("authKey", authKey); //기본적으로는 model은 request scope인데 @SessionAttributes
			return 1;
		}
		return 0; //이메일 보내기 실패
	}
}



/*@Autowired를 이용한 의존성 주입 방법은 총 세 가지가 있다
 *  1) 필드 위에 사용하는 방법
 *  2) setter를 이용하는 방법
 *  	private EmailService service;

		@Autowired
		public void setService(EmailService service) {
			this.service = service;
		}
 	3) 매개변수 생성자 이용
 	*/

// 1) 필드에 의존성 주입하는 방법(요즘에는 권장 X)
//@Autowired //근데 이게 귀찮아서 요즘 이 방식 잘 안쓴다!!!!
//	EmailService service = new EmailServiceImpl(); 이렇게 안쓰고 의존성 주입 받기!!!(Service에서 @Service로 Bean등록 해놨으니까!!!)
//private EmailService service;


// 2) setter 이용

//private EmailService service;
//@Autowired
//public void setService(EmailService service) {
//	this.service = service;
//}


// 3) 매개변수 생성자 이용
//private EmailService service;
//private MemberService service2;
//
//@Autowired ->Autowired 하나로 두 개를 의존성 주입 할 수 있다
//				매개변수 자리에 자동으로 넣어준다
//public EmailController(EmailService service, MemberService service2) {
//	this.service = service;
//	this.service2 = service2;
//}



/* @Autowired를 이용한 의존성 주입 방법은 3가지가 존재
 * 1) 필드
 * 2) setter
 * 3) 생성자 (권장)
 * */
//이제는 생성자 위에 @Autowired 쓸거다

//Lombok 라이브러리(자주 쓰는 코드 자동완성)에서 제공하는
//@RequiredArgsConstructor를 이용하면
//필드(클래스 아래에 쓰는 변수) 중 
// 1) 초기화 되지 않은 final이 붙은 필드(상수 -> 값이 반드시 대입돼있어야 함)
// 2) 초기화 되지 않은 @NotNull이 붙은 필드 (이것도 값이 반드시 있어야 한다는 뜻임)
// 1,2 중 하나에 해당하는 필드에 대한
//	@Autowired 생성자 구문을 자동 완성해준다