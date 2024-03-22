package edu.kh.todo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter //getter 자동완성
@Setter //setter 자동완성
@ToString //toString 자동완성
@NoArgsConstructor //기본생성자 자동완성
@AllArgsConstructor //전체 매개변수 생성자 자동완성
public class Todo {
//할 일을 저장하는 Todo 객체
	private int todoNo; //할 일 번호
	private String todoTitle; //할 일 제목
	private String todoContent; //할 일 내용
	private String complete; //할 일 완료 여부("Y", "N")
	//DB에서 CHAR == 문자열 == Java에서의 String
	//Java에서의 char = 문자
	private String regDate; //할 일 등록일(DB에서 String으로 바꿔서 가져올거임)
}
