package edu.kh.project.common.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
@Slf4j //log찍기
public class BoardNameInterceptor implements HandlerInterceptor{

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception { 
		//컨트롤러가 일 다 하고 응답하기 직전!
		//중간에 인터셉터가 값을 더 끼워 넣겠다
		//이번에는 후처리(컨트롤러에서 Dispatcher Servlet으로 이동하는 사이에 가로채 동작)로 해보기
		
		//application scope에서 boardTypeList 얻어오기
		ServletContext application = request.getServletContext(); //ServletContext : application scope 객체!!
		List<Map<String, Object>> boardTypeList=
		(List<Map<String, Object>>)application.getAttribute("boardTypeList"); //이렇게 하면 얻어와진다
		//boardTypeList은
		// [{boardCode:1, boardName=공지게시판}, {boardCode:2, boardName=자유게시판},...]
		//리스트인데 리스트안의 각 요소가 Map으로 저장돼있는 형태
		
		//이걸 인터셉터로 등록해놔야된다 InterceptorConfig
		
		//application.getAttribute("boardTypeList"); 하면 Object 타입이어서 List의 기능 못쓴다(업캐스팅 돼있어서)
		//그거 쓰려면 List 타입으로 다운캐스팅 해줘야한다
		log.debug(boardTypeList.toString()); //application에서 잘 꺼내왔나 확인
		
//		log.debug("boardCode : "+request.getAttribute("boardCode")); //@PathVariable가 잘 했는지 확인
		//이거 안된다 request scope로 등록되는 시점이 Dispatcher Servlet에 와서 등록되기 때문에
		//아직 거기 도착하기 전이므로 request scope에 등록돼있지 않다!!(없다)
		
		//Uniform Resource Identifier : 통합 자원 식별자
		// - 자원 이름(주소)만 봐도 무엇인지 구별할 수 있는 문자열
		String uri = request.getRequestURI();
		
		
		//uri == /board/1 -> 주소만 봐도 목록조회인 지 알 수 있는 것(뒤의 쿼리스트링은 안들어간다!!딱 요청 주소만)
		//url == localhost/board/1 -> 앞에 locator(localhost)까지 들어가는 것
		//컴퓨터 주소는 ip 주소로 구분
		log.debug("uri : "+uri); // /board/1 나옴 -> 요청 주소에 있는 맨 뒤를 짤라와서 boardCode를 뽑아내서 scope에 세팅하겠다!
		
		//좋아요 개수 하다가 
		// /board/like -> ["", board, like]
		//근데 바로 밑의 코드에서 2번 인덱스인 like를 꺼내서 integer로 파싱한다고 하니까 오류가 났다
		// -> try-catch로 묶기
		
		try {
			
		
		//잘라내기
		int boardCode = Integer.parseInt(uri.split("/")[2]); //자르면  ["", "board","1"]로 반환됨
				// /board/1
				// /board/2
				//split -> /로 쪼개면 비어있는게 0번인덱스, board가 1번인덱스, 1이 2번인덱스 됨
		
		//boardTypeList에서 boardCode를 하나씩 꺼내서 비교
		for(Map<String, Object> boardType : boardTypeList) {
			//하나씩 꺼내서 boardType이라고 하겠다
			int temp= //int로 바꼈다
			Integer.parseInt(String.valueOf(boardType.get("boardCode"))); //얻어올 수 있는데 Object타입인데
			//boardCode랑 비교하려면 int로 바꿔야 한다
			//parseInt는 String을 int로 바꾸는 것이고
			//Object를 String으로 바꾸고 그거를 int로 바꾸는 코드 작성
			// String.valueOf(값) : String으로 변환해줌!
			//그걸 parseInt하면 int로 바뀐다
			
			if(temp==boardCode) {//하나씩 꺼낸 것 비교해서
				//temp랑, 위에서 잘라서 꺼내온 boardCode랑 같다면
				request.setAttribute("boardName", boardType.get("boardName")); 
				//비교 결과가 같다면 request scope에 boardname 을 추가
				break; //같은 것 찾았다면 그 뒤에 더 검사할 필요 없다
			}
		}
		}catch(Exception e) {
			//아무것도 안쓰면 
			//int temp= //int로 바꼈다
//			Integer.parseInt(String.valueOf(boardType.get("boardCode")));
			//여기서 오류 날 건데 여기 아무것도 안쓰면 아무것도 안하고 그냥 넘어간다 오류 안난다
		}
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
}
