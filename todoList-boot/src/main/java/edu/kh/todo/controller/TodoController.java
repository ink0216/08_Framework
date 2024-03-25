package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;

@Controller //컨트롤러 역할 명시 + Bean으로 등록
@RequestMapping("todo") // /todo로 시작하는 요청을 get/post 가리지 않고 모두 여기로 매핑하겠다
public class TodoController {
	//여기다가 수정 삭제 다 할건데 항상 서비스 호출할거니까 필드에 놓는게 좋다
	@Autowired // 같은 타입 Bean의 의존성 주입(DI)
	private TodoService service; //TodoService을 상속받은 TodoServiceImpl이 Bean으로 등록돼있다->그것좀 줘봐 ==Autowired
	
	@PostMapping("add") // /todo/add post방식 요청을 이 메서드에 매핑
	public String addTodo(
			//전달받은 파라미터 받는 방법 네가지 있었다
			@RequestParam("todoTitle") String todoTitle,
			//요청에 담긴 파라미터 중에서 name속성값이 todoTitle인 값을 가져와서 TodoTitle에 집어넣어라
			@RequestParam("todoContent") String todoContent,
			RedirectAttributes ra 
			//리다이렉트 속성들
			) {
		// RedirectAttributes : 리다이렉트 시 값을 계속 세션에 올려놓지 않고, 1회성으로만 전달하는 객체
		
		// RedirectAttributes.addFlashAttribute("key", value) 형식으로 잠깐 세션에 속성 추가
		// 잠깐만 세션으로 갔다가 돌아옴
		// [원리]
		// 응답 전 : request scope로 있다가
		// redirect 중 : session scope로 잠깐 이동
		// 응답 후 : reqeust scope로 복귀->request는 그 페이지에서만 사용가능하다가 없어지게된다
				
		
		
		
		
		
		//서비스 메서드 호출 후 결과 반환 받기
		//insert -> int가 나온다
		int result = service.addTodo(todoTitle, todoContent); //두 값을 전달해준다
		
		//삽입 결과에 따라 message값 지정하기
		String message = null;
		
		if(result>0) message="할 일 추가 성공!!!";
		else  		message="할 일 추가 실패...";
		
		//리다이렉트 후 1회성으로 사용할 데이터를 속성으로 추가했다(세션에 올렸다가 다시 없애는 js구문 안쓰려고 만듦)
		ra.addFlashAttribute("message", message);
		//Model은 기본적으로 request scope인데 재요청하면 request가 새로 만들어져서 기존에는 session을 이용했었다
		//session을 이용하면 브라우저 켜져있는 동안 계속 유지돼서 
		//RedirectAttributes 이용(redirect할 때에만 잠깐 세션 올라갔다가 바로 request scope로 내려옴)
		return "redirect:/"; //메인페이지 재요청
		
	}
	//GetMapping, Postmapping, RequestMapping : Handler Mapping
	
	//상세 조회하기
	@GetMapping("detail") //메인페이지 요청을 제외하고는 맨 앞에 / 쓰면 안된다!!!!
	public String todoDetail(
			@RequestParam("todoNo") int todoNo,
			//html에서 넘어오는 것은 다 String인데 스프링의 Arguments resolver(형변환, 객체 생성)가 parsing도 해준다(int로)
			Model model, //결과를 받아서 전달해줄 변수
			
			RedirectAttributes ra //redirect할 때 데이터를 1회성으로 전달할 수 있게 해주는 객체
			) {
		//서비스 호출
		Todo todo =service.todoDetail(todoNo); //todoNo는 Primary Key역할을 해서 1행만 조회됨 -> Todo가 반환된다
		//주민번호 같은 사람은 오직 한명
		
		String path = null;
		if(todo !=null) {
			//조회 결과 있을 경우
			path="todo/detail"; //forward 할 경로'
			//templates/todo/detail.html로 forward하겠다는 뜻!!! 
			model.addAttribute("todo", todo); //request scope로 값 세팅
		}
		
		else {
			//조회 결과가 없을 경우
			path="redirect:/"; //메인 페이지로 재요청(삭제돼서 없는 경우)
			
			//RedirectAttributes :
			//- 리다이렉트 시 데이터를 request scope로 전달할 수 있는 객체
			ra.addFlashAttribute("message", "해당 할 일이 존재하지 않습니다"); //잠깐 request로 올라갔다가 내려올 거여서 flash써야함!
		}
		return path; //조회 결과가 있으면 detail.html로 forward하겠다
	}
	
	//할 일 삭제하기
	/**할 일 삭제
	 * @param todoNo : 삭제할 할 일 번호(PK)
	 * @param ra : 리다이렉트 시 1회성으로 데이터 전달하는 객체
	 * @return 메인페이지/상세페이지
	 */
	@GetMapping("delete")
	public String todoDelete(
			@RequestParam("todoNo") int todoNo,
			RedirectAttributes ra
			) {
		int result = service.todoDelete(todoNo);
		String path;
		String message;
		if(result>0) {
			path="/"; //메인페이지
			message="삭제 성공!!!"; 
			
		}
		else		{
			path="/todo/detail?todoNo="+todoNo; //상세 조회 페이지
			message="삭제 실패...";
		}
		ra.addFlashAttribute("message", message);  //key value로 써야 한다
		return "redirect:"+path;
	}
	//------------------------------------------------------------------------------------------------------
	//할 일 수정하기
	/** 수정 화면으로 전환하기
	 * @param todoNo :수정할 할 일 번호
	 * @param model : 데이터 전달 객체(기본적으로는 request scope)
	 * @param todo/update.html로 forward하기
	 * @return
	 */
	@GetMapping("update")
	public String todoUpdate(
			@RequestParam("todoNo") int todoNo,
			Model model //model을 왜 준비할까?
			//수정 화면 전환(수정하기 )
			//조회된 내용 전달할 때  model 필요
			) {
		//수정하는 페이지로 가는 get방식 요청
		
		//상세 조회 서비스 호출 ->수정화면에 출력할 이전 내용
		Todo todo = service.todoDetail(todoNo);
		model.addAttribute("todo", todo);
		return "todo/update"; //update.html로 forward
	}
	/**할 일 수정하기
	 * @param updateTodo : 커맨드 객체(전달 받은 파라미터가 자동으로 필드에 세팅된 객체)
	 * @param ra 
	 * @return
	 */
	@PostMapping("update")
	public String todoUpdate(
			//위의 메서드랑 이름 똑같아도 가능하다
			//매개변수가 다르면 오버로딩 가능
			@ModelAttribute Todo todo, //Todo에는 todoNo, todoTitle, todoContent, regDate, complete 로 5개 필드 있음
			//전달받아온 것이 필드명과 같으면
			//Todo라는 객체가 만들어지면서 전달받은 값이 세팅 됨
			//	->RequestParam을 하나씩 안 해도 된다
			//Mybatis는 파라미터를 하나밖에 못 받아서 이걸로 하면 좋다
			// 또한 ModelAttribute는 생략가능해서 좋다
			RedirectAttributes ra //리다이렉트할 때 한 번만 데이터 보내기
			) {
		//수정 서비스 호출
		int result = service.todoUpdate(todo);
		String path="redirect:";
		String message;
		if(result>0) {
			path +="/todo/detail?todoNo="+todo.getTodoNo();
			//수정된 것 바로 볼 수 있게 상세 조회로 redirect
			message="수정 성공!!!";
		}else {
			//실패 시 다시 수정하던 화면으로 오고싶다
			path +="/todo/update?todoNo="+todo.getTodoNo(); //리다이렉트는 다 get방식!!!!(다시 수정화면으로 전환)
			message="수정 실패...";
		}
		ra.addFlashAttribute("message", message);
		return path;
	}
	//------------------------------------------------------------------------------------------------------
	/**완료 여부 변경
	 * @param todo : 커맨드 객체 (@ModelAttributes 생략 가능)
	 * 				- todoNo, complete 두 필드가 세팅된 상태!
	 * @param ra
	 * @return "detail?todoNo="+todoNo (상대 경로로 작성해보기)
	 */
	@GetMapping("changeComplete")
	public String changeComplete(Todo todo, RedirectAttributes ra) {
		
		//변경 서비스 호출
		//성공 시 "변경 성공!!!"
		//실패 시 "변경 실패..."
		String message;
		int result = service.changeComplete(todo);
		if(result>0) message="변경 성공!!!";
		else		message="변경 실패...";
		ra.addFlashAttribute("message", message);	//잠깐 세션 갔다가 내려오도록
		return "redirect:detail?todoNo="+todo.getTodoNo(); //앞에 redirect안쓰면 forward가 된다
		//현재 요청 주소 : /todo/changeComplete
		//응답 주소 : 현재 주소에서 뒤가 바뀌어서 todo/detail이 되고 뒤에 쿼리스트링이 붙는다
		// /todo/detail?todoNo= 
	}

}
