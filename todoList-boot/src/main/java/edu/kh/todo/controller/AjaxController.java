package edu.kh.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;
//ctrl + shift o == import자동
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
 *   
 *   --둘의 해석 순서 : 요청 -> @RequestBody 해석(선) -> 연산 수행 후 반환된 결과값 나옴 -> @ResponseBody 해석(후) (값만 보낼거야)
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
 * key값과 필드명이 동일하면 자동으로 매핑이된다! 
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
	
	@ResponseBody //fetch 문 위치로 값만 보낼거야 포워드 아니야
	@GetMapping("selectList")
	public List<Todo> selectList() { //목록을 조회해서 List형태로 반환할거다
		List<Todo> todoList = service.selectList();
		//List 자료형은 자바스크립트에서 사용할 수 없다 -> 이걸 JSON으로 변환 ->"{K:V, K:V, ..}" js 객체에 쌍따옴표 찍으면 문자열이 되고
		//js가 객체 모양인 문자열을 인식해서 객체로 만든다
		//자바 - 자바스크립트 - String, Boolean : 모든 언어가 가지고 있는 자료형
		//List타입은 Java 타입으로
		//Js에서 사용 불가능!!
		// -> JSON으로 변환(GSON라이브러리 이용!)하기
		//log.debug(todoList.toString());
		//js에서 [] : 배열
		//js 객체 배열 모양이 된다 [ {}, {}, {} ,...] : JSON 모양
		// List 타입은 Java의 타입으로
		// JS에서 사용 불가능!!
		// -> JSON 변환(GSON 라이브러리 이용)
		//Gson gson = new Gson();
		//log.debug(gson.toJson(todoList)); 원래는 되는데 안되니까 지우기->Gson 안쓰는 버전으로 하기!
		
		return todoList;
		//List(Java 전용 타입)를 반환->List를 반환하지만 HttpMessageConverter가 String으로 바꿔서 js에 준다
		//	->이걸 반환 받는 JS가 인식할 수 없다(JS에는 없는 자료형이어서)
		//	->HttpMessageConverter가 이것을 JSONArray 형태로 변환을 해준다(객체배열==JSON array 모양으로)
		//		[{}, {}, {},...]==JSONArray(JSON으로 이루어진 배열)
	}
	
	//할 일 상세 조회
	@ResponseBody //요청한 곳으로 값 그대로 보내줘
	@GetMapping("detail")
	public Todo selectTodo(
			@RequestParam("todoNo") int todoNo
			//@RequestBody는 post방식으로 body에 담겨있을 때만 사용
			) {
			
			//return 자료형은 Todo타입
			//	->HttpMessageConverter가 String(JSON형태)으로 변환해서 반환해준다!!! //응답 받는 곳에서는 
		return service.todoDetail(todoNo);
	}
	//Delete 방식 요청 처리(비동기 요청만 Delete방식 가능)
	//동기식 요청은 Get/Post방식으로밖에 못한다!
	@ResponseBody
	@DeleteMapping("delete")
	public int todoDelete(
			//body에 담겨서 넘어왔으니까 RequestParam이 아닌 RequestBody로 해야한다!
			@RequestBody int todoNo
			) {
		return service.todoDelete(todoNo); //호출해서 결과 반환 ->fetch문 위치로
	}
	
	//완료 여부 변경 버튼 클릭 시
	@ResponseBody //비동기할거니까 호출한 곳으로 값을 보내줘라
	@PutMapping("changeComplete")
	public int changeComplete(
			//JSON으로 보내주면 HttpMessageConverter가 
			//자동으로 자바 객체(DTO)로 만들어줌
			@RequestBody Todo todo) {
		return service.changeComplete(todo);
	}
	
	@ResponseBody
	@PutMapping("update")
	public int todoUpdate(
			@RequestBody Todo todo
			//자동으로 DTO로 변환됨
			) {
		return service.todoUpdate(todo);
	}
	
	
}
