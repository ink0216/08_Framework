package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		
		return "redirect:/"; //메인페이지 재요청
		
	}
}
