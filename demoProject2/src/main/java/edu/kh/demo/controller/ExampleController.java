package edu.kh.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.demo.model.dto.Student;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
/*Model ->두 scope를 커버 가능(request,session)
 * - Spring에서 데이터 전달 역할을 하는 객체
 * - org.springframework.ui 패키지에서 제공
 * - 기본 scope : request
 * - @SessionAttributes 어노테이션과 함께 사용 시 session scope로 변환 가능
 * [기본 사옹볍]
 * Model.addAttribute("key", value);
 * 
 * (Model과 비슷한 ModelAndView 객체도 존재)
 * - ModelAndView : 데이터 전달(Model)+forward할 파일 경로 지정(View)
 * 		=>반환을 String이 아닌 ModelAndView객체를 반환함(그 안에 forward 정보 들어있음)*/

@Controller // 요청/응답 제어 역할 명시 + Bean으로 등록
@RequestMapping("example")
//주소가 /example로 시작하는 모든 요청을 이 컨트롤러로 매핑
@Slf4j //lombok 라이브러리가 제공하는 log객체 자동생성 어노테이션
// lomok 라이브러리 = 자주쓰는 객체를 자동완성시켜주는 라이브러리
public class ExampleController {
	// /example/ex1 GET방식 요청을 매핑
	@GetMapping("ex1")
	public String ex1(HttpServletRequest req, Model model) {
		//Servlet/JSP 내장 객체 범위(scope)
		//page<request<session<application
		//request : 요청 받은 곳과 요청 위임받은 곳까지
		//application : 서버 켜지고 꺼질 때까지
		//--------------------------------------------------------------
		//값 전달 두 가지 방법!
		//1) request scope로 전달
		req.setAttribute("test1", "HttpServletRequest로 전달한 값");
		
		//2) Model로 전달
		model.addAttribute("test2", "Model로 전달한 값");
		
		//단일 값(숫자,문자열)을 Model을 이용해서 html로 전달해보기
		model.addAttribute("productName","종이컵");
		model.addAttribute("price",2000);
		
		//복수 값(배열, List)을 Model을 이용해서 html로 전달해보기
		List<String> fruitList = new ArrayList<>();
		fruitList.add("사과");
		fruitList.add("딸기");
		fruitList.add("바나나"); //세 개 담아서 전달해보기
		model.addAttribute("fruitList",fruitList );
		
		//DTO 객체를 Model을 이용해서 html로 전달해보기
		//DTO : 값을 옮겨주기 위한 객체
		Student std = new Student();
		std.setStudentNo("12345");
		std.setName("홍길동");
		std.setAge(22);
		model.addAttribute("std",std);
		
		//List<Student> 객체를 Model을 이용해서 html로 전달하기
		//List를 뜯어봤더니 그 안에 Student객체가 또 있다
		List<Student> stdList = new ArrayList<>();
		stdList.add(new Student("11111", "김일번", 20));
		stdList.add(new Student("22222", "최이번", 23));
		stdList.add(new Student("33333", "홍삼번", 26));
		model.addAttribute("stdList", stdList);
		
		return "example/ex1"; 
		//templates/example/ex1.html로 요청위임
	}
	@PostMapping("ex2") // /example/ex2 POST 방식 매핑
	public String ex2(Model model) {
		//Model : 데이터 전달용 객체 (기본적으로 request scope이다)
		model.addAttribute("str", "<h1>테스트 중 &times; </h1>");
		//&times; : 곱하기 모양!
		return "example/ex2"; //example/ex2.html로 forward하겠다
	}
	@GetMapping("ex3")
	public String ex3(Model model) {
		model.addAttribute("boardNo", 10); //게시글 번호
		model.addAttribute("key", "제목"); //key라는 변수명 (제목 검색)
		model.addAttribute("query", "검색어"); //query라는 변수명 (검색어 검색)
		return "example/ex3";
	}
	/* @PathVariable 어노테이션 : 요즘 많이 사용(정규표현식과 같이 쓰기도 함!)
	 * - 주소 중 일부분을 변수 값처럼 사용하는 것
	 * 	+ 해당 어노테이션으로 얻어온 값은 자동으로 request scope에 세팅된다
	 * */
	@GetMapping("ex2/{number}")
	public String pathVariableTest(
			@PathVariable("number") int number
			//주소 중 {number} 부분의 값을 가져와 매개변수에 저장
			//	+ request scope에 세팅
			
			// @RequestParam("number")
			//name속성값이 number인 값을 int number에 넣겠다
			) {
		log.debug("number : "+number); //1. 정말 가져왔는지 확인(콘솔창에)
		return "example/testResult"; //2.forward한 곳에서 출력할 수 있는지 확인(request scope에 세팅됐는지 확인)
	}
	@GetMapping("ex4")
	public String ex4(Model model) {
		Student std = new Student("67890", "잠만보", 22); 
		model.addAttribute("std", std); //model에 객체를 담아서
		model.addAttribute("num", 100);
		return "example/ex4"; //forward하는 곳에 보내주기!
	}
}
