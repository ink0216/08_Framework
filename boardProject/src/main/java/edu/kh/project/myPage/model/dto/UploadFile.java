package edu.kh.project.myPage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/*@Builder : 빌더 패턴을 이용해 객체 생성 및 초기화를 쉽게 진행할 수 있게 해줌
 * ->단점 : 기본 생성자가 생성이 안된다 -> Mybatis에서 조회 결과를 담을 때 필요한 객체 생성 구문 오류 발생
 * 		(Mybatis는 기본생성자로 객체 만들어서 조회 결과를 담는다)
 * ->기본 생성자 만들어야 한다
 * */
@Getter //lombok
@Setter 
@Builder 
@NoArgsConstructor //기본생성자
@AllArgsConstructor //매개변수 생성자
public class UploadFile {
	private int fileNo; //DB테이블이랑 똑같이 만들기
	private String filePath;
	private String fileOriginalName;
	private String fileRename;
	private String  fileUploadDate;//DATE타입 조회할 때 TO_CHAR 이용해서 바꿔서 가져올거다
	private int memberNo; //이 컬럼을 이용하면 조인이 된다 -> 그 회원의 이름,닉네임 등 가져올 수 있다
	private String memberNickname; //조인해서 가져올 컬럼
	
}
