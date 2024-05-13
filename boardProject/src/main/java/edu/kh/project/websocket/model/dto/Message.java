package edu.kh.project.websocket.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Message {
	private int messageNo;
    private String messageContent; //메시지의 내용
    private String readFl;
    private int senderNo;
    private int targetNo; //누가 이 메시지를 받을 것인지
    private int chattingNo;
    private String sendTime;
}
