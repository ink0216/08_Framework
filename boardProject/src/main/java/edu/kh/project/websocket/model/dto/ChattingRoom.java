package edu.kh.project.websocket.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChattingRoom {
    private int chattingNo;
    private String lastMessage;
    private String sendTime;
    private int targetNo;
    private String targetNickname;
    private String targetProfile; //프로필 이미지
    private int notReadCount;
    private int maxMessageNo; //마지막 메시지 번호
}
