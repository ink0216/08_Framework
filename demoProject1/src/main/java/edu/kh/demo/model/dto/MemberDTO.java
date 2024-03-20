package edu.kh.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//Lombok 라이브러리 이용
//Lombok : 자주 사용하는 코드를
//			컴파일 시 자동완성 해주는 라이브러리!!!
//			DTO(기본 생성자, getter/setter, toString), Log

//기본생성자는 전달받는 전달인자 없음

@NoArgsConstructor  //기본생성자 자동완성 -> 이 한줄 쓰면 컴파일할 때 기본생성자 자동으로 만들어짐
@Getter //getter 자동완성
@Setter //setter 자동완성
@ToString //toString 오버라이딩 자동완성
public class MemberDTO {
//한번에 다량의 데이터 받을 때 좋다
	//필드의 변수명을 name속성과 똑같이 만들기
	private String memberId;
	private String memberPw;
	private String memberName;
	private int memberAge;
	
	
	
	
	
	
}
