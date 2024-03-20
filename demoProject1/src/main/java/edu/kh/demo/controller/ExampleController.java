package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//instance = 개발자가 만들고 관리하는 객체
//Bean : 스프링이 만들고 관리하는 객체 
//@Controller : 객체 스프링보고 만들라고 하는 것
//@Controller : 요청/응답 제어 역할 명시 + Bean 등록
@Controller
public class ExampleController {
	/*요청 주소 매핑하는 방법
	 * 1) @RequestMapping("주소") 이용
	 * 2) @GetMapping("주소") : GET 방식 요청(조회 시 사용 권장)만 매핑
	 * 	  @PostMapping("주소") : POST 방식 요청(삽입 시 사용 권장)만 매핑
	 *    @PutMapping("주소") : 일반적인 form태그에서는 쓸 수 없는 매핑(수정 시 사용 권장)
	 *    						(form, js, a태그 요청 불가! 비동기로 작동함)
	 *    @DeleteMapping("주소") : DELETE (삭제) 방식 요청 매핑
	 *    						(form, js, a태그 요청 불가! 비동기로 작동함)
	 *    */
	@GetMapping("example") // /example GET방식 요청을 매핑한다
	public String exampleMethod() {
		//return값에 forward하려는 html파일 경로를 작성하는데
		// 단, ViewResolver(반환되는 문자열 가지고 주소를 만들어서 forward해주는 객체)가 제공하는
		//		Thymeleaf 접두사, 접미사를 제외하고 작성해야한다!
		return "example"; //forward하는 코드 완성!
	}
	
	
}
