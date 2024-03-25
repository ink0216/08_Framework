package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

/*@ResponseBody : 자바에서 js로 갈 때 사용
 * - 컨트롤러 메서드의 반환값을 
 * 	HTTP 응답 본문에 직접 바인딩하는 역할임을 명시
 * ==컨트롤러 메서드에서 반환되는 12라는 값으로 포워드 하는 것이 아닌, 메서드를 호출한 fetch 자리에 넣겠다
 * (쉬운 해석)
 * =>컨트롤러 메서드의 반환값을 
 * 		비동기 (ajax) 요청했던 HTML/JS 파일 부분에 값을 그대로 돌려 보낼 것이라고 명시
 * 		==>forward/redirect 하려는 주소로 인식하지 않음!!!(나 그냥 값 그대로 돌려보내기만 할거야) 
 * 
 * @RequestBody : js에서 자바로 들어올 때 사용
 * - 비동기 요청(ajax) 시 전달되는 데이터 중
 * 	body 부분에 포함된 요청 데이터를
 * 	알맞은 Java 객체 타입으로 바인딩하는 어노테이션
 *   (쉬운 설명)
 *   - 비동기 요청 시 body에 담긴 JSON(String타입)값을
 *   	알맞은 타입으로 변환해서 매개변수로 저장을 한다
 *   	String타입을 Todo타입으로 HttpMessageConverter가 바꿔준다
 * */


/* [HttpMessageConverter] : 메시지를 바꿔주는 애
 *  Spring에서 비동기 통신 시
 * - 전달되는 데이터의 자료형
 * - 응답하는 데이터의 자료형
 * 위 두가지 알맞은 형태로 가공(변환)해주는 객체
 * 
 * 서로서로 바꿔준다
 * - 문자열, 숫자 <-> TEXT
 * - Map <-> JSON
 * - DTO <-> JSON
 * 
 * (참고)
 * HttpMessageConverter가 동작하기 위해서는
 * Jackson-data-bind 라이브러리가 필요한데
 * Spring Boot 모듈에 내장되어 있음
 * (Jackson : 자바에서 JSON 다루는 방법 제공하는 라이브러리)
 */
@RequestMapping("ajax")
@Controller
@Slf4j //로그 자동완성
public class AjaxController {
	@Autowired //TodoService 또는 TodoService를 상속받은 객체가 bean으로 등록돼있으면 여기에다가 의존성 주입해라
	//@Autowired
	//- 등록된 bean 중 같은 타입 또는 상속 관계에 있는 bean을
	//	해당 필드에 의존성 주입(DI)
	private TodoService service;
	
	@GetMapping("main")
	public String ajaxMain() {
		return "ajax/main"; //main.html로 forward
	}
	
	//전체 Todo 개수 조회
	@ResponseBody //값 그대로 호출한 곳으로 돌려보낼 거라는 어노테이션(주소로 인식되지 않아서 접두사 접미사 붙지 않음)
	//부분부분 바꾸려면 바꿀 값이 필요함
	//forward가 아닌, 값만 돌아가는거다
	@GetMapping("totalCount")
	public int getTotalCount() {
		log.info("getTotalCount() 메서드 호출됨!!!!!"); //값을 보려는 게 아니어서 info 사용
		//화면에서 비동기 요청해보기
		
		//전체 할 일 개수 조회 서비스 호출
		int totalCount=service.getTotalCount(); //반환된 값을 이 메서드 호출한 ajax-main.js에 보내서
		//result에 담고, 화면에 보여주기
		//전체 개수를 포함한 화면(forward나 redirect)을 원하는 것이 아닌,
		//"전체 Todo 개수" 라는 데이터가 반환되기를 원한다
		return totalCount; //우리는 주소로 forward나 redirect하는 것이 아닌 숫자를 반환하므로
		//이렇게 하면 
		//templates/12.html로 forward하는 거구나 로 됨(접두사 접미사 붙어서)
		
		//패치 요청하면
		//약속 하면 promise 객체 생긴다
		//비동기 통신해서 12 결과 들어오면 promise 안에 들어옴
		//text타입이니까 text로 바꿔서 써 라고 하기
		//js에서 number, string 타입 == html에서는 text타입임
	}
	
	@ResponseBody
	@GetMapping("completeCount")
	public int getCompleteCount() {
		//이 메서드 호출 결과가 forward나 redirect가 아닌
		//completeCount 값만 DB에서 조회해서 그대로 반환하길 원함 ->@ResponseBody(호출한 곳으로 값 반환하기만 할거야) 사용
		return service.getCompleteCount();
	}
	
	
	@ResponseBody//응답할 때 값만 필요하다 ->비동기 요청 결과로 값만 반환(화면 X)
	@PostMapping("add")
	public int addTodo(
			//JSON으로 받아오는 것 하나씩 뜯어보기 ->안됨
			//JSON이 파라미터로 전달된 경우 아래 방법으로 얻어오기 불가능하다
			//@RequestParam("todoTitle") String todoTitle,
			//@RequestParam("todoContent") String todoContent
			//@ModelAttribute Todo todo //HttpMessageConverter가 알아서 담기게 해준다
			//@ModelAttribute : 동기식일 때에만 동작
			//요청할 때 body에 JSON 데이터를 담음
			@RequestBody Todo todo //요청 body에 담긴 String 타입 JSON을 Todo로 형변환해서 todo에 저장했다
			) {
		log.debug(todo.toString());
		int result = service.addTodo(todo.getTodoTitle(), todo.getTodoContent()); //묶여있으니까 풀어서 넣어주기
		return result; //호출한 곳으로 돌려보내주기
		//0,1로 나올 것이다
	}
}
