package edu.kh.project.websocket.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

//SessionHandshakeInterceptor로부터 세션을 넘겨받아서 웹소켓 동작 시 수행할 구문을 여기다 작성한다
/**웹소켓 동작 시 수행할 구문을 작성하는 클래스
웹소켓에 연결된 세션을 모아두고, 요청에 따라 알맞은 세션에 값을 전달할 수 있는 코드를 여기에 작성
 * 
 */
@Slf4j
@Component //자동으로 동작하도록 Bean등록
public class TestWebsocketHandler extends TextWebSocketHandler{
	// 아래의 두 메서드의 공통적인 매개변수 WebSocketSession :
	// 우리가 아는 Session과는 다르다
	// 이건 웹소켓 전용 세션이다!!!
	
	// WebSocketSession :
	// - 클라이언트 <->서버 간 전이중 통신을 담당하는 객체
	// - SessionHandshakeInterceptor가 가로챈 연결된 클라이언트의 HttpSession을 얘가 가지고 있다 (attributes에 세팅한 값이 여기에 들어있다)
	//		여기서 세션을 가져와서 접속한 클라이언트를 구분할 수 있다
	
	//필드
	//Set은 컬렉션 중의 하나
	//중복 데이터를 허용하지 않는다 -> 서로 다른 데이터만 저장되게 할 수 있어서 데이터를 구분할 수 있는 특징 있다
	private Set<WebSocketSession> sessions
		= Collections.synchronizedSet(new HashSet<>()); //동기화된 Set 생성
		
		//웹소켓은 실시간성이 과하게 부각돼서 서버는 한 대인데 클라이언트가 마구잡이로 들어오면 오류날 수 있어서
		// 강제로 줄세우는 것이다!!
		// 마구잡이로 들어오는것은 비동기인데, 강제로 줄세운다 == 강제로 동기화시킨다
		//synchronized : 동기화
		// 동기화 장점 : 충돌 발생 X  // 동기화 단점 : 느림(앞의 것 완료 전까지 뒤의 동작은 그저 대기)
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//established를 먼저
		//연결된 후에 세우겠다
		//클라이언트와 웹소켓 연결이 완료되고, 통신할 준비가 되면 실행
//		super.afterConnectionEstablished(session); -> 이건 지워도 된다
		
		sessions.add(session);
		//연결된 클라이언트의 WebSocketSession 정보를
		// Set에 추가
		// -> 웹소켓에 연결된 클라이언트 정보를 모아둠
		//    특정 클라이언트를 찾을 수 있음
	}
	
	@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		//closed를 나중에
		// 클라이언트와 연결이 종료되면 실행
		// 다했으니 나갈 떄 실행되는 부분
//			super.afterConnectionClosed(session, status);-> 이건 지워도 된다
		
		//연걸 끝낫으면 set에서 삭제 시켜줘야 한다
		//웹소켓 연결이 끊긴 클라이언트의 WebSocketSession 정보를
		// Set에서 제거
		sessions.remove(session);
		}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		//str이 message로 들어온다
//		super.handleTextMessage(session, message);-> 이건 지워도 된다
		// 클라이언트로부터 텍스트 메세지를 받았을때 실행
		// 전화인 경우 이야기를 했을 때 실행되는 코드
		
		//매개변수 TextMessage : 웹소켓으로 연결된 클라이언트가 전달한
		//						텍스트(내용)가 담겨 있는 객체
		//						문자 받을 때 String으로 주고받을 수 없고 텍스트 객체를 이용해야 한다
		log.info("전달 받은 메시지 : {}", message.getPayload());
		//PayLoad : 통신 시 탑재된 데이터 (이 안에 파라미터 등이 담겨있다)
		
		//전달 받은 메시지를 특정인에게만이 아닌, 접속한 모든 사람에게 보내보기 테스트로 (단체 채팅방처럼)
		//전달 받은 메시지를 현재 해당 웹소켓에 연결된 모든 클라이언트들에게 보내기
		for(WebSocketSession s : sessions) { //서버에서 클라이언트 쪽으로 전달해주기
			//필드로 만들어놨던 sessions에 접속한 클라이언트 정보가 다 담겨있음
			s.sendMessage(message); //전달받은 메시지를 다시 모든 클라이언트에게 보내겠따 (게임 전체 공지 같은 것이다)
		}
	}
}

/*(이 클래스에서 작성 가능한 것들)
 WebSocketHandler 인터페이스 : 웹소켓을 위한 메소드를 지원하는 인터페이스 -> 이걸 상속 받아야 한다
    -> WebSocketHandler 인터페이스를 상속받은 클래스를 이용해 웹소켓 기능을 구현 가능


WebSocketHandler 주요 메소드
        
    void handlerMessage(WebSocketSession session, WebSocketMessage message)
    - 클라이언트로부터 메세지가 도착하면 실행
    
    void afterConnectionEstablished(WebSocketSession session)
    - 클라이언트와 웹소켓 연결이 완료되고, 통신할 준비가 되면 실행

    void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
    - 클라이언트와 연결이 종료되면 실행

    void handleTransportError(WebSocketSession session, Throwable exception)
    - 메세지 전송중 에러가 발생하면 실행 
----------------------------------------------------------------------------

TextWebSocketHandler :  WebSocketHandler 인터페이스를 상속받아 구현한 
						"텍스트 메세지 전용" 웹소켓 핸들러 "클래스" -> extends하기(클래스를 상속)
 						텍스트 말고 파일을 주고받고 하고 싶으면 BinaryWebSocketHandler(2진수)를 사용하면 된다
    handlerTextMessage(WebSocketSession session, TextMessage message)
    - 클라이언트로부터 텍스트 메세지를 받았을때 실행
 */