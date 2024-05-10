package edu.kh.project.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import edu.kh.project.websocket.handler.NotificationWebsocketHandler;
import edu.kh.project.websocket.handler.TestWebsocketHandler;
import lombok.RequiredArgsConstructor;

@Configuration //서버 실행 시 내부에 작성된 메서드를 모두 수행해서 설정을 적용시키는 설정용 클래스이다
@EnableWebSocket // 웹소켓 활성화한다는 어노테이션
@RequiredArgsConstructor // 의존성 주입
public class WebsocketConfig implements WebSocketConfigurer{ //WebSocketConfigurer : 웹소켓 관련 설정용 인터페이스
	
	//웹소켓 처리 동작이 작성된 객체 의존성 주입받기(DI)
	private final TestWebsocketHandler testWebsocketHandler;
	
	//Bean으로 등록된 SessionHandshakeInterceptor가 의존성 주입될거다(상속관계이면 자동으로 주입된다)
	private final HandshakeInterceptor handshakeInterceptor;
	
	//알림 웹소켓 처리 객체 의존성 주입
	private final NotificationWebsocketHandler notificationWebsocketHandler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// registerWebSocketHandlers : 웹소켓 핸들러를 등록하는 메서드
		//만들어만 놓아서 동작 아직은 못하는 testWebsocketHandler을 동작 가능하게 등록시킴
		
		registry.addHandler(testWebsocketHandler, "/testSock")
		//registry : 등록해주는 객체
		//addHandler(웹소켓 핸들러, 웹소켓 요청 주소)
		//	websocket://localhost/testSock이라는 요청을 클라이언트가 보내게 만들면
		//				testWebsocketHandler가 처리하도록 등록한것이다!!
				.addInterceptors(handshakeInterceptor) //이 요청 오면 악수하면서 가로챔
				//이러면 클라이언트 연결 시 HttpSession을 가로채 Handler에게 전달을 해준다
				
				.setAllowedOriginPatterns("http://localhost/",
											"http://127.0.0.1/", //이건 localhost랑 똑같은거다 (루프백)
											"http://192.168.10.242/") 
				//다른 사람이 들어오려면 별도의 ip주소나 도메인을 알려줘야함(localhost는 나 혼자 쓸 떄 임!)
												//웹소켓 요청이 허용되는 ip/도메인을 지정->세 개 정도를 보통 써둔다	
				.withSockJS();
				//SockJS(websocket 보완하는 라이브러리) 지원 + 브라우저 호환성 증가
		
		//------------------------------------------------------------------
		//알림 처리 핸들러와 주소 연결
		registry.addHandler(notificationWebsocketHandler, "/notification/send") //header.js에서 요청함
				.addInterceptors(handshakeInterceptor) //이 요청 오면 악수하면서 가로챔
				.setAllowedOriginPatterns("http://localhost/",
											"http://127.0.0.1/", //이건 localhost랑 똑같은거다 (루프백)
											"http://192.168.10.242/") 
				.withSockJS();
	}
	
	
	
}
