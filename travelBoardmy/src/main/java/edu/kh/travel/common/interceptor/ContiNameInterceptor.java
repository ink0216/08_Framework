package edu.kh.travel.common.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.kh.travel.board.model.service.BoardService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ContiNameInterceptor implements HandlerInterceptor{
	@Autowired
	private BoardService service;
	
	@Override //전처리
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
	//하는 일 : boardTypeInterceptor에 적혀있음 
	//전처리 -> application scope 객체를 얻어와서 boardTypeList가 없으면 서비스를 이용해서 다 조회해와서
	//application scope에 세팅하는 것 (최초 접속했을 때임)
	//여기서 DB까지 갈 건데 
	//Dispatcher Servlet에서 Controller로 가는 요청 가로챘으니까 DB로 갈 때 컨트롤러 가면 안됨
	//바로 서비스로 가서 매퍼로 감
	ServletContext application = request.getServletContext(); //application scope의 객체 이름이 ServletContext임
	//요청 객체를 이용해서 얻어올 수 있다
	
	if(application.getAttribute("boardTypeList") ==null) {
		//속성값을 얻어왔는데 null이었을 경우
		//application scope에 boardTypeList가 없을 경우
		//없을 때에만 DB에서 조회해서 집어넣겠다 ->결과 반환받아서 application scope에 세팅해놓겠다
		log.info("BoardTypeInterceptor - postHandle(전처리) 동작 실행"); //정보만 보여줄 거면 info 레벨로 하면 된다
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
