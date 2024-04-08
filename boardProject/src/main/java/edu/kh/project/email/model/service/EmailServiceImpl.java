package edu.kh.project.email.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import edu.kh.project.email.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Transactional //: 이거 안쓰면 기본값은 commit이고, 이걸 쓰면 예외 발생하면 롤백할게 설정하는 것!(롤백하려고 쓰는 것!)
//Mybatis == JDBC(DBCP) -> SQL 실행 후 문제 없으면 connection이 닫히면서 자동으로 Commit된다!!
//Spring == Java
@Service //Bean 등록 + Service 역할 명시
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
	//EmailConfig 설정이 적용된 객체 -> 얘를 이용하면 메일을 보낼 수 있다
	private final JavaMailSender mailSender;
	
	//템플릿 엔진 == 화면을 자동으로 만들어줌(HTML , jsp를 Java코드를 바꿔주는 것)(이 중 하나가 타임리프임)
	//타임리프(템플릿 엔진)을 이용해서 html 코드를 java로 변환할 수 있다
	//보였으면 하는 화면을 html로 만들어놓으면 이 밑에서 얘가 java 코드로 변환할거다
	private final SpringTemplateEngine templateEngine;
	
	//Autowired 썼었는데 @RequiredArgsConstructor 쓰면 생성자를 이용한 Bean 의존성 주입
	//이거 쓰려면 필드를 private final로 해야 된다!!
	private final EmailMapper mapper; //Mapper 의존성 주입
	
	//이메일 보내기
	public String sendEmail(String htmlName, String email) { //어떤 html파일을 이용해서 화면을 만들것인지!
		//6자리 난수(인증코드) 생성하기
		String authKey = createAuthKey();
		//메일보내는 것 예외 처리 필요
		try {
			//메일 제목 지정
			String subject = null; 
			switch(htmlName) { //케이스 마다 용도 다름
			case "signup" : subject="[boardProject] 회원 가입 인증번호 입니다."; break; //회원 가입 시 사용
			}
			
			//인증 메일 보내기
			//전달받은 mailSender 이용
			//mimeMessage : 자바에서 진짜로 메일 보내는 역할을 하는 객체
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			//MimeMessageHelper : 자바가 아닌 스프링에서 제공하는 메일 발송 도우미(->간단하고 타임리프 사용 가능)
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); //이건 Bean으로 등록 안돼있어서 직접 만들기
			// 1번 매개변수 : MimeMessage
			// 2번 매개변수 : 파일 전송 사용 ? true / false
			// 3번 매개변수 : 문자 인코딩 지정
			
			helper.setTo(email); //to 누구에게(받는 사람 쓰기)
			//받는 사람 이메일 지정
			
			helper.setSubject(subject); //이메일 제목 지정
			
			//helper.setText(타임리프가 적용된 html파일을 내용으로 넣을 거다 ->타임리프가 자바코드로 바꿔서 여기에 넣는다); //이메일 내용 지정
			helper.setText(loadHtml(authKey, htmlName), true);
			//두 개를 보내주는데
			//true : HTML 코드 해석 여부 true로 함(innerHTML 해석, 태그는 태그로 해석해라)
			//true를 안하면 그냥 태그 모양 그대로 출력됨 <html> ~~
			//setText는 글만 첨부 가능
			
			//CID(Content-ID)를 이용해 메일에 이미지를 첨부하기
			//	(파일 첨부와는 다른, 이메일 내용에 사용할 이미지를 첨부하는 것이다!)
			helper.addInline("logo", new ClassPathResource("static/images/logo.jpg")); 
			//앞이 ID이고, 뒤는 해당 경로의 이미지를 메일 내용에 첨부하는데
			//	사용하고 싶으면 "logo"라는 id를 작성해라
			//해당 사진에게 "logo"라는 아이디를 붙여준거다!
			
			
			//메일 보내기
			mailSender.send(mimeMessage);
			//mimeMessage : helper에 의해서 값이 다 세팅됐다
			
			
			
			//html 내용 전체가 String타입의 덩어리가 돼서 매개변수로 들어올 거다!
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		//이메일 + 6자리 인증번호를 TB_AUTH_KEY 테이블에 저장하기
		// 둘을 맵으로 묶기
		Map<String, String> map = new HashMap<>();
		map.put("authKey", authKey);
		map.put("email", email);
		
		// 1) 해당 이메일이 DB에 존재하는 경우가 있을 수 있기 때문에
		//		해당 이메일에 대한 authKey 수정, update (같은 이메일로 인증번호를 여러 번 보낸 경우, 나중 것으로 인증번호 바꾸어 저장)
		//		->1 반환 == update 성공 == 해당 이메일에 부여됐던 인증번호가 이미 존재했었는데 새롭게 바꿨다
		//		  0 반환 == update 실패 == 해당 이메일이 TB_AUTH_KEY 테이블에 존재 X -> INSERT 시도하면 된다
		int result = mapper.updateAuthKey(map); //map 전달
		
		if(result ==0) {
			// 2) 1번 update 실패 시 insert 시도
			result = mapper.insertAuthKey(map);
		}
		
		//수정, 삭제 후에도 result값이 0이면 ==제대로 삽입도 수정도 안됐다
		if(result==0) return null;
		
		//성공
		return authKey; //오류 없이 전송 되면 authKey를 반환
	}
	public String loadHtml(String authKey,//인증번호랑
			String htmlName //어떤 용도의 html을 읽어올 지
			) {
		
		//html파일을 읽어와서 String으로 변환하는 함수 (타임리프가 적용돼있다)
		Context context = new Context(); //포워드 하는 용도가 아닌, 자바에서 타임리프 사용하고 싶을 때 사용하는 용도
		//타임리프 이용할 거니까 타임리프 써져있는 거 선택하기
		// org.thymeleaf.context 선택하기!!!
		context.setVariable("authKey", authKey);//타임리프가 적용된 HTML 에서 사용할 값 추가해서 보내주기
		//model.addAttribute("authKey", authKey) 하면 포워드 한 곳에서 th:text="${authKey}"하면 됐었는데 그것과 비슷!
		return templateEngine.process("email/"+htmlName, context); //html코드 전체가 문자열로 만들어져서 반환됨
		//forward경로처럼 적음
		//여기로 forward할 거야
		//근데 context전달
		//authKey 전달돼서 html넘어가서 해석 후
		//html 코드를 여기로 가져와서 String으로 변환해서 반환
		//templates/email 폴더에서 htmlName과 같은 .html 파일을 찾아서
		//그 내용을 읽어와 String으로 변환시킨다 ->이 메서드를 위에서 호출하기
	}
	
    /** 인증번호 생성 (영어 대문자 + 소문자 + 숫자 6자리)
     * @return authKey
     */
    public String createAuthKey() { //이 함수를 호출하면 랜덤한 6글자가 만들어져서 리턴된다(인증번호 생성 함수)
    	String key = "";
        for(int i=0 ; i< 6 ; i++) {
            
            int sel1 = (int)(Math.random() * 3); // 0:숫자 / 1,2:영어
            
            if(sel1 == 0) {
                
                int num = (int)(Math.random() * 10); // 0~9
                key += num;
                
            }else {
                
                char ch = (char)(Math.random() * 26 + 65); // A~Z
                
                int sel2 = (int)(Math.random() * 2); // 0:소문자 / 1:대문자
                
                if(sel2 == 0) {
                    ch = (char)(ch + ('a' - 'A')); // 대문자로 변경
                }
                
                key += ch;
            }
            
        }
        return key;
    }
    
    //이메일, 인증번호 확인
	@Override
	public int checkAuthKey(Map<String, Object> map) {
		return mapper.checkAuthKey(map);
	}
}
 /* Google SMTP를 이용한 이메일 전송하기
  * - SMTP(Simple Mail Transfer Protocol, 간단한 메일 전송 규약)
  * -->이메일 메시지를 보내고 받을 때 사용하는 규약,방법
  * 
  * - Google SMTP 동작 흐름 :
  * 	Java Mail Sender 이용 -> Google SMTP -> 목표하는 대상에게 이메일 전송
  * 
  * - Java Mail Sender에 Google SMTP 이용 설정 추가하기
  * 	1) config.properties에 내용 추가
  * 	2) 저 파일을 읽어서 설정을 적용하는 EmailConfig.java 파일도 만들거다
  * */
