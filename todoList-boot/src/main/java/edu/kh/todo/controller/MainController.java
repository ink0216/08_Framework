package edu.kh.todo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
//화면출력<-Servlet ->Service(Impl)->DAO(Impl)->DB 였는데

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

//Servlet ->Controller(메서드 단위로 요청 처리) 대체
//DAO ->Mapper 대체
//sql.xml ->todo-mapper.xml 대체

//대체되기만 하고 늘어나지는 않았다

//서비스 클래스 인터페이스 만들기
@Controller // 요청/응답 제어 역할 명시 + Bean으로 등록(스프링이 알아서 내부의 코드 실행시켜줌)
@Slf4j //로그 객체 자동 생성
public class MainController {
	//필드
	//캡슐화 원칙에 의해 필드는 모두 private으로 써야한다
	
	@Autowired //등록된 Bean 중 같은 타입 or 상속 관계인 것을 DI(의존성 주입)한다
	//자동으로 묶어준다
	private TodoService service; //원래 참조변수 선언만 하면 null이 들어가는데 실행을 하면 스프링이 만든 것을 넣어준다
	//TodoServiceImpl에서 @Service 어노테이션 써서 Bean으로 등록했음
	//TodoService와 똑같은 타입이거나 상속 관계에 있는 것()을 찾아보고 있으면 자동으로 넣어준다
	//private TodoService service = new TodoServiceImpl();하면 스프링이 아닌 개발자가 만든 객체가 되는데,
	//TodoServiceImpl에서 @Service 어노테이션 써서 Bean으로 등록했음->그러면 겹치게 돼서 오류 발생해서 여기서는 만들지 않음
	//		@Autowired 사용하면 TodoServiceImpl에서 만들어진 Bean이 여기에 자동으로 주입된다
	@RequestMapping("/") //방식 가리지 않고 메인페이지 요청 오면 여기서 받음
	public String mainPage(Model model) { //메서드가 실행되려면 메서드가 객체로 만들어져야 해서 new를 썼어야 했는데
		//스프링 IOC : 스프링이 Contoller 어노테이션을 통해 Bean으로 객체화한다
		//Bean 등록되는 어노테이션이 네개정도 있다
		//의존성 주입(DI) 확인(진짜 Service 객체 들어옴)
		log.debug("service : "+service); //의존성 주입이 안되면 필드에 기본적으로 null이 들어감 ->null이라고 나오고 
		//주입됐으면 주소 모양으로 나올거임
		
		//서비스 메서드 호출+결과 반환받기(Map형태로)
		//하나의 서비스가 두 개의 sql 수행하는 모양새였음 옛날에는! 지금도 비슷하게 해보자
		Map<String, Object> map = service.selectAll();
		
		//SQLException은 원래 checked라 꼭 처리 해야하는데
		//스프링이 먼저 잡아서 unchecked로 만들어준다!->예외처리하는 코드를 스프링에서는 거의 안써도 된다!
		//스프링에서 발생하는 예외는 대부분이 unchecked 상태로 바뀌어있음(예외 하나 발생했다고 해서 서버 꺼지거나 하지 않음)
		//메인페이지로 포워드하는 메서드
		
		//map에 담긴 내용을 뜯어내기(추출하기)
//		List<Todo> todoList = map.get("todoList"); //이렇게하면 우변이 Object타입이라서 다운캐스팅하기
		List<Todo> todoList = (List<Todo>)map.get("todoList"); //이렇게하면 우변이 Object타입이라서 다운캐스팅하기
		int completeCount = (int)map.get("completeCount"); //이것도 Object 타입이니까 int로 강제다운캐스팅하기
		
		//forward할 때 Model이용해서 조회 결과를 request scope에 담아서보내기
		model.addAttribute("todoList", todoList);
		model.addAttribute("completeCount", completeCount);
		
		return "common/main"; //classpath:/templates/common/main.html로 forward하겠다
		//타임리프에 설정한 접두사,접미사를 View Resolver가 붙여줌!!!!
		//common/main에서 todoList, completeCount를 쓸 수 있다
	}
}
