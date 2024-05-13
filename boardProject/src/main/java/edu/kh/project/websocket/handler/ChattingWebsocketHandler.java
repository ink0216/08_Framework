package edu.kh.project.websocket.handler;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.websocket.model.dto.Message;
import edu.kh.project.websocket.model.service.ChattingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//핸들러가 뭐였더라?
@Component
@Slf4j
@RequiredArgsConstructor //매개변수 생성자를 자동완성해주는 구문
public class ChattingWebsocketHandler extends TextWebSocketHandler{
	// ChattingService Bean 의존성 주입
	private final ChattingService service;
	
	//채팅 하려는 사람들이 다 여기에 연결돼있어야 한다
	
	//Set : 중복 저장을 못하게 한다
	//WebSocketSession : 클라이언트와 서버의 전이중 통신을 담당하는 객체
	//						실시간으로 통신할 수 있게 해주는 객체
	//					세션을 소매치기 하는 HandShakeInterceptor이 세션을 소매치기해서 그 세션을 핸들러에게 넘겨줘서
	//					이 안에 인터셉터가 가로채기 해준 클라이언트의 세션이 담겨있다
	//				
	// sessions : 연결된 클라이언트들(== /chatting 페이지에 접속한 모든 사람들)의 접속 정보를 모두 모아놓은 것
	private Set<WebSocketSession> sessions
	= Collections.synchronizedSet(new HashSet<>()); //많은 사람들이 채팅하다보니까 오류 날 수 있어서 줄서서 사용하도록 줄세우기(동기화시키기,synchronize)
	//------------------------------------------------------------------------------------------------
	//sessions에 언제 들어오고 언제 나가는 지 지정해야 한다
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//초기 설정
		// 클라이언트와 웹소켓 연결이 완료되고, 통신할 준비가 되면 실행
		sessions.add(session); //매개변수 session에는 현재 접속한 클라이언트 한 명이 담겨있는데 그걸 sessions에 추가하겠다
		//연결된 클라이언트의 정보를 sessions에 추가
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 클라이언트와 연결이 종료되면 실행
		// 연결 종료된 클라이언트를 sessions에서 제거
		sessions.remove(session);
	}
	//-------------------------------------------------------------------------------------------------
	//클라이언트로부터 텍스트 메시지를 전달받은 경우
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message //message는 JSON이다
			) throws Exception {
		// 매개변수 message에 전달받은 메시지가 담겨있다!!
		 // message에는 JSON형태의 메시지가 담겨있어서 Message DTO로 바꾸겠다 -> 그럼 자동 세팅되도록
		//ObjectMapper 이용해서 바꿀거다
		
		// JSON <->DTO 를 서로 변환할 수 있는 Jackson 라이브러리(자바에서 JSON 다룰 수 있게 해주는 라이브러리) 제공 객체
		ObjectMapper objectMapper = new ObjectMapper();
		Message msg = objectMapper.readValue(message.getPayload() //JSON을
				, Message.class //어떤 DTO로 바꿔서 msg에 대입할 지 지정
				); //objectMapper로 값을 읽어서 옮겨담는 작업
		// PayLoad : 제출된 값
		

		// 채팅을 보낸 사람의 회원 번호 얻어오기
        HttpSession currentSession =  (HttpSession)session.getAttributes().get("session");
    	Member sendMember = ((Member)currentSession.getAttribute("loginMember"));
    	msg.setSenderNo(sendMember.getMemberNo()); //이거 하면 msg에 chattingNo, messageContent, targetNo, sendMember가 들어가있게 된다
		//몇 번 채팅방의 어떤 메시지를 누가 누구에게 보낸다
    	
    	// DB 삽입 서비스 호출
        int result = service.insertMessage(msg);
        
        if(result == 0) return;
        
        //채팅이 보내진 시간을 msg에 세팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");
        msg.setSendTime(sdf.format(new Date()) );
        //이거 하면 msg에 chattingNo, messageContent, targetNo, sendMember, 시간이 들어가있게 된다
		
		
		for(WebSocketSession s : sessions) {
			//하나씩 꺼내면 연결된 모든 클라이언트에 하나씩 순차접근
			
			//회원 정보는 세션에 있다
			HttpSession clientSession 
			= (HttpSession) s.getAttributes().get("session");
			//세션 handshakeInterceptor가 attributes에 세션을 담아놨다 -> getAttributes하면 거기서 세션을 꺼낼 수 있다.
			//근데 그럼 Object타입이어서 다운캐스팅 해줘야 한다
			int clientMemberNo
			= ((Member)clientSession.getAttribute("loginMember")).getMemberNo();
			
			//연결된 클라이언트 중 
			//전달받은 Message의 targetNo와 
			//회원 번호가 같을 경우에만 그 메시지 보내주기
			if(msg.getTargetNo() == clientMemberNo
					||
					msg.getSenderNo() == clientMemberNo
					//msg 객체를 JSON으로 변경한 값을 
					//보낸 사람이랑 받는 사람에게 전달하겠다는 if문
					
					//두 명에게 다 보낸다
					) {
				//Message DTO로 바꿨던 msg를 JSON으로 바꿔서 보내보자
				TextMessage textMessage 
				= new TextMessage(objectMapper.writeValueAsString(msg));
				//값을 String(문자열)으로 쓰겠다
				//근데 그 문자열이 msg DTO임
				//객체를 String으로 바꾸면 JSON이 됨
				// -> 결국 DTO를 JSON으로 변환하는 구문이 된다
				
				s.sendMessage(textMessage); //JSON으로 변환된 데이터를 지정된 클라이언트에게만 보내주기
			}
		}
	}
	
	
}
