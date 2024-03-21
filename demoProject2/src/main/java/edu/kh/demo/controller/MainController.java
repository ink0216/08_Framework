package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
//Controller : 요청에 따라 알맞은 서비스를 호출
//			+ 서비스 호출 결과에 따라 어떤 응답을 할 지 제어
import org.springframework.web.bind.annotation.RequestMapping;
@Controller //요청/응답 제어할 컨트롤러 역할 명시+Bean으로 등록
//Bean : 스프링이 만들고 관리하는 객체
//어노테이션 : 컴파일러가 보는 주석
public class MainController {
	//스프링은 메서드당 하나씩 요청을 처리함
	@RequestMapping("/") //주소 맨 앞에 /안쓰는게 좋은데 메인페이지는 써야함!
	//	-메인 페이지 지정 시에는 "/" 작성 가능하다!!
	public String mainPage(){
		//return을 하면 기본적으로 forward를 하는 것으로 되어있음
		//thymeleaf : Spring Boot에서 사용하는 템플릿 엔진
		//thymeleaf를 이용한 html파일로 forward시
		//기본적으로 사용되는 접두사,접미사가 존재한다
		//접두사 : classpath:/templates/
 		//접미사 : .html
		//view Resolver : 어디로 forward할 지 제어
		//타임리프에 접두사 접미사 준비돼있음
		//컨트롤러에서 a라는 문자열이 반환되어 view resolver로 오면
		//view resolver가 접두사 접미사 붙여서 해당 주소로 forward해준다!!!!
		//classpath:/templates/common/main.html ==풀 주소
		return "common/main"; 
	}
}
