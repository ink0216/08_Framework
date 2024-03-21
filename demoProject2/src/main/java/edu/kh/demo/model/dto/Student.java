package edu.kh.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
//Spring EL같은 경우 getter가 꼭 필수로 작성돼있어야 함!!!!
//->원래대로라면 ${Student.getName()} ==>${Student.name}
//getter대신 필드명을 호출하는 형식으로 작성하는데
//자동으로 getter가 호출되기 때문에 getter가 꼭 있어야 한다!

@Getter //컴파일 시 getter코드가 자동 추가된다(우리가 안써도 됨)
@Setter //컴파일 시 setter코드가 자동 추가된다(우리가 안써도 됨)
@ToString 
//컴파일 시 toString()메서드가 자동 오버라이딩된다(우리가 안써도 됨)

//생성자 자동완성은 이 두개밖에 지원이 안돼서 몇개만 만드는 것은 직접 만들어야 함!
@NoArgsConstructor 
//매개변수가 없는 생성자(기본 생성자) 자동 생성하는 lombok어노테이션
@AllArgsConstructor //모든 필드를 초기화하는 용도의 매개변수 생성자 자동생성
public class Student {
	private String studentNo; //학번
	private String name; //이름
	private int age; //나이
	//public Student() {} //위에 lombok어노테이션과 중복돼서 오류생김!
}
