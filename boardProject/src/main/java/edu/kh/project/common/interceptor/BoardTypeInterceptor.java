package edu.kh.project.common.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.kh.project.board.model.service.BoardService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*Interceptor : 요청/응답을 가로채는 객체(Spring 지원)
 * Client <-> Filter <-> Dispatcher Servlet <-> Interceptor <-> Controller <-> Service <-> DAO / Mapper <-> DB
 * 
 * HandlerInterceptor 인터페이스를 상속 받아서 구현해야 한다!
 * 메서드 세 개!
 * - preHandle(전처리) : Dispatcher Servlet -> Interceptor -> Controller
 * 						(컨트롤러가 역할 하기 전에 처리한다.동작한다)
 * - postHandel(후처리) : Dispatcher Servlet <- Interceptor <- Controller
 * 						(컨트롤러가 역할 하고 난 후에 처리한다.동작한다)
 * - afterCompletion(뷰 완성 후) : forward 코드 해석*완성 후 동작
 *     					View Resolver -> Interceptor -> Dispatcher Servlet
 * 
 * 인터페이스 상속 받았는데 아무것도 오버라이딩이 강제되지 않음 
 * 인터페이스는 디폴트 메서드를 쓸 수 있는데 그건 오버라이딩 강제화되지 않는다
 * 이 인터페이스 안에 메서드 세 개 있는데 다 디폴트여서 오버라이딩 꼭 안해도 되는데 해보기
 * */
@Slf4j //로그 찍기
//@RequiredArgsConstructor
public class BoardTypeInterceptor implements HandlerInterceptor{
	//alt shift s ->Override implement methods 세개 다 선택하고 ok
	//코드 내부 주석만 지우고 내부 코드는 지우면 안된다
	
	//이 인터셉터가 어떤 요청에 대해서만 동작할 지는 InterceptorConfig 클래스에 지정해놨다!!!
	
	@Autowired
	private /*final*/ BoardService service; //이미 Bean으로 등록돼있어서 의존성 주입 받기
	
	//InterceptorConfig에서 BoardTypeInterceptor 만들 때 기본생성자를 호출해서 만든다
	//그래서 기본생성자가 있어야 하는데
	// @RequiredArgsConstructor+final필드 가 다음의 매개변수 생성자를 자동완성해줘서
	// 매개변수 생성자가 있으면 기본생성자가 호출되지 않아서 매개변수 생성자 안생기도록 
	// @RequiredArgsConstructor 사용 안하고 서비스를 @Autowired로 만든다
//	public BoardTypeInterceptor(BoardService service) {
//		super();
//		this.service = service;
//	}
	
	
	@Override //전처리
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		//진짜 request랑 response를 가로챔
		
		//application scope 객체(ServletContext) 얻어오기 
		//application scope : 서버 종료 시 까지 유지되는 Servlet 내장 객체
		//					- 서버 내에 딱 한 개만 존재!
		//					-->모든 클라이언트가 공용으로 사용한다는 뜻
		
		//여기서 DB까지 갈 건데 
		//Dispatcher Servlet에서 Controller로 가는 요청 가로챘으니까 DB로 갈 때 컨트롤러 가면 안됨
		//바로 서비스로 가서 매퍼로 감
		ServletContext application = request.getServletContext(); //요청 객체를 이용해서 얻어올 수 있다
		log.info("BoardTypeInterceptor - postHandle(전처리) 동작 실행"); //정보만 보여줄 거면 info 레벨로 하면 된다
		if(application.getAttribute("boardTypeList") ==null) {
			//속성값을 얻어왔는데 null이었을 경우
			//application scope에 boardTypeList가 없을 경우
			//없을 때에만 DB에서 조회해서 집어넣겠다
			
			//boardTypeList 조회 서비스 호출
			List<Map<String, Object>> boardTypeList = service.selectBoardTypeList();
			//map : 한 행
			// {"boardCode" : 1,
			//	"boardName" : "공지 게시판"}
			
			//map : 한 행
			// {"boardCode" : 2,
			//	"boardName" : "정보 게시판"}
			
			//map : 한 행
			// {"boardCode" : 3,
			//	"boardName" : "자유 게시판"}
			
			//위의 map들을 리스트로 묶어서 가져옴
			//그러면 DB에서 조회해서 이런 모양이 나오는데 조회 결과를 application scope에 추가하기
			application.setAttribute("boardTypeList", boardTypeList);
		}
			
		
		
		
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	@Override //후처리
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView //Model에 어디에 포워드할 지 저장돼있는 view까지 가로챔
			) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}


	
}
