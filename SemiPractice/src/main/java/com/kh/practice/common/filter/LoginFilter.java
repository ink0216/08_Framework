package com.kh.practice.common.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//정수기 필터 같은 것!!!
/*Filter : 요청, 응답 시 걸러내거나 추가할 수 있는 객체
 * 
 * [필터 클래스 생성 방법]
 *  - 1. jakarta.servlet.Filter 인터페이스를 상속 받아야 한다
 *  		jakarta.servlet == 톰캣이 제공
 *  		인터페이스의 모든 메서드는 추상 메서드 -> 상속 받는 곳에서 오버라이딩이 강제화됨
 *  - 2. doFilter()메서드를 오버라이딩 해야 함!
 * 
 * */

//로그인이 되어있지 않은 경우 특정 페이지로 돌아가게 만들거다!!(로그인 필터니까)
public class LoginFilter implements Filter{
	//필터 동작을 정의하는 메서드(이 필터는 어떤 역할을 하는 필터인지 정의)
	//염소를 걸러줄거야 먼지를 걸러줄거야
	@Override
	public void doFilter(
			ServletRequest request,  //
			ServletResponse response, //
			FilterChain chain) //
			throws IOException, ServletException {
		//ServletRequest : HttpServletRequest의 부모 타입이다!
		//ServletResponse : HttpServletResponse의 부모 타입이다!
		
		//HTTP 통신이 가능한 형태로 다운 캐스팅 (HttpServletRequest, HttpServletResponse로)
		//인터넷에서 요청 응답을 처리하고 있다 ->우변의 것을 써야 해서 다운캐스팅 해야 함(강제 형변환)
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		//req에서 Session을 얻어올 수 있다
		HttpSession session = req.getSession();
		
		//session.setAttribute, session.getAttribute
		//세션에서 로그인한 회원 정보를 얻어와봤는데 없을 때(참조하는 것이 없을 때)
		//	==로그인이 되어있지 않은 상태
		if(session.getAttribute("loginMember") == null) {
			//resp 객체를 이용해서 원하는 곳으로 날려버릴 수 있다!
			resp.sendRedirect("/loginError"); //해당 주소로 재요청 -> 컨트롤러로 감
			//여기로 날려버리는 역할의 필터이다!!
		}else {
			//로그인 되어있는 경우
			//어디로 날려버리지 않을 거고 
			//FilterChain chain 연결
			//1번 통과 시 2 번. 통과 시 3번 필터,...하면서 가다가, 모든 필터 다 통과 시 다음 단계로 넘어감 
			//필터 여러 장 사용 가능
			//FilterChain
			// - 다음으로 가야 할 곳이 
			//다음 필터인지 Dispatcher Servlet으로 
			//다음 필터 또는 Dispatcher Servlet과 연결된 객체
			
			//다음 필터로 요청/응답 객체 전달
			//(만약 없으면 Dispatcher Servlet으로 전달해준다)
			chain.doFilter(request, response);
			
			//필터 만들었으니까 원하는 위치에 필터 끼워넣어야 한다!
		}
		
		
		
		
		
	}
}
