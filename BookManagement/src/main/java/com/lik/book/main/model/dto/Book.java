package com.lik.book.main.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter//Spring EL 구문 사용 시 getter를 호출 + Mybatis에서 DTO가 전달한 값 꺼내서 쓸 때 필요
@Setter //커맨드 객체(파라미터와 필드가 같으면 자동으로 세팅해준다 ->setter호출)
//private 캡슐화 때문에 간접접근 필요 +
@NoArgsConstructor //기본생성자(이것도 커맨드 객체 만들 때 필요)
//(객체를 자동으로 만들어서 그 안에 필드를 세팅해준다)
@ToString
@Builder //Build Pattern 이용
@AllArgsConstructor //이거 있어야 Build Pattern 이용가능
public class Book {
	private int bookNo;
	private String bookTitle;
	private String bookWriter;
	private int bookPrice;
	private String regDate;
}
