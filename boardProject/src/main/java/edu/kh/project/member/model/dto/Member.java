package edu.kh.project.member.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//DTO(Data Transfer Object) : 데이터 전달하는 객체
// - 데이터 전달용 객체
// - DB에서 조회된 결과 또는 SQL 구문에 사용할 값을 담아 전달하는 용도
// - 관련성 있는 데이터를 한 번에 묶어서 다룬다
@Getter //Spring EL 구문 사용 시 getter를 호출 + Mybatis에서 DTO가 전달한 값 꺼내서 쓸 때 필요
@Setter //커맨드 객체(파라미터와 ~가 같으면 자동으로 세팅해준다 ->setter호출)
//private 캡슐화 때문에 간접접근 필요 +
@NoArgsConstructor //기본생성자(이것도 커맨드 객체 만들 때 필요)
@ToString
public class Member {
	//MEMBER 테이블에 값 넣거나 꺼내올 때 사용
	   private int 		memberNo;
	   private String memberEmail;
	   private String memberPw;
	   private String memberNickname;
	   private String memberTel;
	   private String memberAddress;
	   private String profileImg;
	   private String enrollDate;
	   private String memberDelFl;
	   private int authority; 
	   //테이블에서는 _ 썼는데 여기서는 카멜표기법으로 바꿈->mybatis-config에 서로 컬럼-변수명이 연결되도록 설정돼있음
}
