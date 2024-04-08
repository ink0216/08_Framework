package edu.kh.project.common.util;

import java.text.SimpleDateFormat;

//프로그램 전체적으로 사용될 유용한 기능들 모아둘거다
public class Utility {
	public static int seqNum = 1; //1~99999사이를 반복시키게 할거다 (한 번에 10만개는 못올려서)(N 역할)
	/**
	 * @param originalFileName
	 * @return
	 */
	public static String fileRename(String originalFileName) { //파일 rename하는 코드
		//static으로 안했으면 MyPageServiceImpl에서 fileRename을 호출하려면
		//new Utility().fileRename으로 객체를 하나 만들고 호출해야 함
		//근데 static 하면 static 메모리 영역
		//static 붙은 것은 프로그램이 실행되자마자 다 읽어들여서 static이라는 메모리 영역에 저장해놓아서
		//이 메서드도 저장되는데 다른 static 메서드에도 동명 있을 수 있어서 클래스명.메서드명으로 저장돼있다(static 써져있으면 객체 생성 없이도 호출할 수 있다)
		
		//사용하려면 저 이름 그대로 해야 함 ->그 이름 그대로 마이페이지서비스임플에서 쓰면 쓸 수 있어서!
		
		//어떻게든 파일명이 중복되지 않게 하면 된다
		//20240405100931_0000N.jpg  연월일시분초 N : 중복아닌 숫자
		
		//SimpleDateFormat : 시간을 원하는 형태의 문자열로 간단히 변경해주는 객체
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		// new java.util.Date() : 현재 시간을 저장한 자바 객체
		String date = sdf.format(new java.util.Date());  //앞부분 완성
		
		String number = String.format("%05d", seqNum); //5칸인데 빈칸에는 0을 넣겠다 %d에는 seqNum넣겠다
		seqNum++; //1 증가 -> 99999이면 10만되면 6자리 되니까 1로 바꾸게 하기
		if(seqNum==100000) seqNum=1;
		
		//확장자
		//전달 받은 originalFileName에서 얻어오기
		String ext 
		= originalFileName.substring(originalFileName.lastIndexOf("."));
		//문자열.subString(2) :2번인덱스부터 끝까지를 잘라옴 
		//문자열.subString(인덱스) : 문자열을 인덱스부터 끝까지 잘라낸 결과 반환
		//문자열.lastIndexOf(".") : 문자열에서 마지막 "."의 인덱스 반환
		//.jpg로 반환된다
		return date+"_"+number+ext;
	}
}
