package edu.kh.demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.demo.model.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller // 요청/응답 제어 역할 명시 + Bean으로 등록
@RequestMapping("param") //클래스 위에 쓰면 -> 공통주소를 매핑
// /param으로 시작하는 모든 요청을 현재 클래스로 매핑시키겠다!!!!!!!!!!!!!!!!!!!!!!!!!!!!
@Slf4j //log를 이용한 메시지 출력 시 사용(Lombok이 제공해줌)
//이번에는 어노테이션 3개 씀!
//Bean : 스프링이 만들고 관리하는 객체
public class ParameterController {
	@GetMapping("main") // /param/main GET방식 요청을 여기에 매핑
	public String paramMain() {
		return "param/param-main"; //forward할 html 경로
		//"/src/main/resources/templates/param/param-main.html"
		//접두사 : /src/main/resources/templates/
		//접미사 : .html
		//접두사 접미사 부분은 생략하고 쓴다
	}
	/*1. HttpServletRequest.getParameter("key") 이용해서 파라미터 제출하기*/
	//HttpServletRequest : doGet 메서드 오버라이딩 시 요청객체 req 였음
	//HttpServletRequest 
	// - 요청하는 클라이언트의 정보, 제출된 파라미터 등을 저장하고 있는 객체
	// - 클라이언트 요청 시 생성됨
	
	/*Spring의 Controller 메서드 작성 시 
	 * 매개변수에 원하는 객체를 작성하면
	 * 존재한다면 객체를 바인딩(연결)해주거나
	 * 없으면 생성해서 바인딩(연결)해준다
	 * -->ArgumentResolver(전달인자 해결사)가 해결해줌
	 * 스프링은 IOC가 기본으로 되어있다
	 * */
	@PostMapping("test1") // /param/test1 POST 방식 요청을 이 메서드에 매핑!
	public String paramTest1(HttpServletRequest req) {
		//요청 보내서 HttpServletRequest 객체 생겼는데 서블릿에서는 눈에 보였는데 스프링에서는 안보임
		//매개변수 자리에 HttpServletRequest req 이렇게 쓰면 알아서 들어옴
		// 이렇게 쓰면 ArgumentResolver가 작동해서 만들어서 쓰라고 줌
//https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/arguments.html
		String inputName = req.getParameter("inputName");
		String inputAddress = req.getParameter("inputAddress");
		int inputAge = Integer.parseInt(req.getParameter("inputAge"));
		// "1234" : String! 
		// 이렇게 문자열 안에 숫자만 있는 경우만 Integer.parse 가능!!
		
		//확인할 때 log를 이용해서 해보기 ->콘솔창에 나옴
		log.debug("inputName : "+inputName);
		log.debug("inputAddress : "+inputAddress);
		log.debug("inputAge : "+inputAge);
		//debug를 쓰기 위한 조건 : 
		//debug : 코드 오류 해결 ->코드 오류(컴파일 에러)는 없는데 정상 수행이 안되는 경우 사용
		//						->값이 잘못된 경우 -> 값을 추적하는 행위를 함!
		//빨간줄 = 컴파일에러 = 디버그로 찾지 않음
		
		/*Spring에서 Redirect(재요청)하는 방법!!!
		 *  - Controller 메서드 반환값에
		 *  	"redicect:요청주소"; 를 작성하면 된다!
		 *  */
		return "redirect:/param/main"; 
		//이 문자열이 dispatcher servlet에 오면 
		//dispatcher servlet이 앞의 redirect 보고 이제는 view resolver로 안보내고
		//다시 해당 요청이 매핑된 Controller로 요청을 다시 보냄
	}

	/* 2. @RequestParam 어노테이션 - 낱개(한 개, 단 수)개 파라미터 얻어오기
	 * 
	 * - request객체를 이용한 파라미터 전달 어노테이션 
	 * - 매개 변수 앞에 해당 어노테이션을 작성하면, 매개변수에 값이 주입됨.
	 * - 주입되는 데이터는 매개 변수의 타입이 맞게 형변환/파싱이 자동으로 수행됨!
	 * 		
	 * 
	 * [기본 작성법]
	 * @RequestParam("key") 자료형 매개변수명
	 * 
	 * 
	 * [속성 추가 작성법]
	 * @RequestParam(value="name", required="fasle", defaultValue="1") 세 개를 쓸 수 있다
	 * 
	 * value : 전달 받은 input 태그의 name 속성값
	 * 
	 * required : 입력된 name 속성값 파라미터 필수 여부 지정(기본값 true) 
	 * 	-> required = true인 파라미터가 존재하지 않는다면 400 Bad Request 에러 발생(필수로 제출되어야 하는 것이 돼서) 
	 * 	-> required = true인 파라미터가 null인 경우에도 400 Bad Request
	 * 		(true이면 존재하지 않거나 null이면 다 에러 발생)
	 * defaultValue : 파라미터 중 일치하는 name 속성 값이 없을 경우에 대입할 값 지정. 
	 * 	-> required = false인 경우 사용(필수가 아닐 때에만 기본값을 지정할 수 있다!)(필수는 반드시 값을 넣기 떄문)
	 */
	
	
	//400 Badn Request(잘못된 요청) 에러
	// - 매개변수를 네 개를 받기로 했는데 세개만 받았다
	// - 파라미터 불충분 상황
	@PostMapping("test2")
	public String paramTest2(@RequestParam("title") String title,
							@RequestParam("writer") String writer,
							@RequestParam("price") int price,
							//html에서 넘어오는 것 다 String인데 이 떄는 자동으로 int로 parsing도 해줌
							@RequestParam(value="publisher",required = false,defaultValue = "교보문고" ) String publisher
							//defaultValue 속성 : 입력 안한 경우 기본값을 어떤 것으로 할 지 지정
							
							//이 값이 꼭 없어도 된다
							//이 매개변수가 꼭 있을 필요는 없어(required 아님)
							//publisher : 출판 배포
							) {
		//이 메서드는 파라미터 네개를 받아온다
		//요청할 때 넘어온 파라미터 중에서 title을 String타입 title 변수에 넣어라
		log.debug("title : "+title); //title 작성되는지 확인해보기 ->콘솔에 나옴
		log.debug("writer : "+writer); //title 작성되는지 확인해보기 ->콘솔에 나옴
		log.debug("price : "+price); //title 작성되는지 확인해보기 ->콘솔에 나옴
		log.debug("publisher : "+publisher); //title 작성되는지 확인해보기 ->콘솔에 나옴
		//String은 기본 자료형 아닌 참조형이라 입력 안된 경우의 기본값=null
		return "redirect:/param/main";
	}
	/*3. @RequestParam 여러 개(복수) 파라미터*/
	//String[] 
	//List<자료형> 
	//Map<String, Object>
	//이렇게 세 방법으로 여러 개의 파라미터 얻어올 수 있다
	@PostMapping("test3")
	public String paramTest3(
			@RequestParam(value="color", required=false) String[] colorArr, //체크한 것들만 이 안에 다 담겨서옴
			@RequestParam(value="fruit", required=false) List<String> fruitList,
			@RequestParam Map<String, Object> paramMap
			//Map의 key는 무조건 String타입!
			) { //여러 개의 파라미터 얻어오는 경우 defaultValue 속성은 사용 불가!!!!!!
		log.debug("colorArr : "+Arrays.toString(colorArr));
		log.debug("fruitList : "+fruitList); //얘는 toString 오버라이딩 돼있어서 이렇게 써도된다
		
		//@RequestParam Map<String,Object>
		//	->제출된 모든 파라미터(color, fruit, 나머지 두개도 모두!)가 Map에 저장된다!!
		//	-> 문제점 : key(name 속성값)가 중복되면 덮어쓰기가 된다!
		//	-> 같은 name 속성 파라미터가 String[], List로 저장 X
		log.debug("paramMap : "+paramMap); 
		//Map의 특징 : 데이터를 key:value형태로 저장
		//				map.put("a", "김길동")
		//				map.put("a", "박길동") -> key값이 중복되면 value값이 나중 값으로 덮여씌워진다!
		//color : red 들어있다가 color : green 또 들어오면 green으로 덮어씌워짐
		//Map에 저장할 때에는 key값이 같은 여러 값을 저장할 수 없다
		return "redirect:/param/main";
	}
	/* 4. @ModelAttribute를 이용한 파라미터 얻어오기*/

	// @ModelAttribute
	
	// - DTO(또는 VO)와 같이 사용하는 어노테이션
	
	// - 전달 받은 파라미터의 name 속성 값이
	//   같이 사용되는 DTO의 필드명과 같다면
	//   자동으로 setter를 호출해서 필드에 값을 세팅해줌
	
	// *** @ModelAttribute 사용 시 주의사항 ***
	// - DTO에 기본 생성자가 필수로 존재해야 한다!!!!!!!!!!!!!!!!!!!!
	// - DTO에 setter가 필수로 존재해야 한다!!!!!!!!!!!!!!!!!!!!!!!
	
	// *** @ModelAttribute 어노테이션은 생략이 가능하다! ***
	
	// *** @ModelAttribute를 이용해 값이 필드에 세팅된 객체를
	//		"커맨드 객체" 라고 한다 (아래의 MemberDTO 객체도) ***
	
	
	@PostMapping("test4")
	public String paramTest4( /* @ModelAttribute */ MemberDTO inputMember ) {
		//lombok테스트
		MemberDTO mem = new MemberDTO();
		mem.getMemberAge(); //getter
		mem.setMemberAge(0); //setter
		//안만들었는데 호출이 가능하다
		log.debug("inputMember : "+inputMember.toString());
		return "redirect:/param/main";
	}
}
