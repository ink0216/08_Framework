package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//Java에서 객체 : new 연산자에 의해 heap 영역에 클래스에 작성된 내용대로 생성된 것
//	instance : 개발자가 만들고 관리하는 객체(new로 만든 모든 객체)
//근데 스프링에서는 개발자가 아닌 스프링이 만들어줌
// Bean(콩) : Spring Container(Spring)가 만들고 관리하는 객체
//----------------------------------------------------------------------------
//@Controller : 요청/응답을 제어할 컨트롤러 역할 명시 
//				+ Bean으로 등록(==이 클래스를 객체로 생성해서 스프링이 관리)
//					(실행됐을 때 객체로서 동작함)
@Controller
public class TestController {
//이제 서블릿 안만들고 컨트롤러 만든다!
//자동으로 콘솔에서 리로드 함
	
	//Dispatcher Servlet : 모든 요청을 일단 받고,
	//이 요청을 ExampleController로 보내고 싶다 하면
	//컨트롤러는 객체가 아닌 클래스여서 객체로 만들어야한다
	//스프링 : IOC가 있어서 객체 생성을 스프링이 대신 해준다
	
	//기존 Servlet : 클래스 단위로 하나의 요청만 처리 가능했었다!!
	//Spring : 클래스가 아닌 메서드 단위로 요청 처리가 가능하다
	//->클래스 위에가 아니라, 메서드마다 매핑
	//요청이 세개였을 경우 클래스(서블릿)를 세개 만들어야 했었는데
	//이제는 한 클래스만 만들고 메서드를 세개 만들면 된다
	//--------------------------------------------------------------------------------
	//일단 public String으로 만들기
	//쓸 수 있는 어노테이션 5종류정도 있다
	//@RequestMapping("요청주소")    --->요청주소를 처리할 메서드를 매핑하는 어노테이션
	//1) 메서드 위에 작성하는 경우 
	//	- 요청 주소와 메서드를 매핑
	//	- GET/POST 가리지 않고 매핑함(속성을 통해서 지정 가능한데 요즘은 안씀)
	
	//2) 클래스 위에 작성하는 경우
	//	- 공통 주소를 매핑
	//	ex) /todo/insert, /todo/select, /todo/update 주소를 사용한 경우
	//			공통된 주소 : /todo
	
	// /는 쓰지말기!
	//@RequestMapping("todo")
	//public class 클래스명(){
	//	@RequestMapping("insert")
	//	public String 메서드명(){} //이 메서드는 /todo/insert를 매핑
	
	//	@RequestMapping("update")
	//	public String 메서드명(){} //이 메서드는 /todo/update를 매핑
	// }
	//관련 있는 것도 주소를 폴더처럼 적음
	
	/********************************************************************/
	//Spring Boot Controller에서
	//특수한 경우를 제외하고
	//매핑 주소 제일 앞에 /를 작성하지 않는다!!!
	/********************************************************************/
	@RequestMapping("test")  // /test 요청 시 처리할 메서드를 매핑함(메서드에 작성한 경우)
	public String testMethod() {
		System.out.println("/test 요청 받음");
		
		//원하는 페이지로 forward하기
		
		/*Controller 메서드의 반환형이 String인 이유
		 *  - 메서드에서 반환되는 문자열이 
		 *    forward할 html파일의 경로가 되기 때문!*/
		//test.html 페이지가 보이도록 forward할거임
		//src/main/resources/templates/test.html   == 풀 경로
		
		/*Thymeleaf : JSP 대신 사용하는 템플릿 엔진
		 * 이걸 이용하면 다음의 접두사,접미사가 자동으로 제공됨
		 * classpath: == src/main/resources 여기까지를 의미
		 * 접두사 : classpath:/templates/
		 * 접미사 : .html 
		 * */
		//test 접두사와 접미사 지운 나머지만 리턴하면 된다
		
		return "test"; // 접두사 + 반환값(test) + 접미사 경로의 html로 forward함
		/*이런 접두사,접미사, forward 설정은 View Resolver 객체가 담당한다!
		 * Resolver == 해결사
		 * "test"가 Controller로부터 View Resolver까지 이동되면 View Resolver가 접두사 접미사 붙임*/
	}
	
}
