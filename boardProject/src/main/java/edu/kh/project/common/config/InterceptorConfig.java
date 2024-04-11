package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.kh.project.common.interceptor.BoardTypeInterceptor;

//우리가 만든 BoardTypeInterceptor가 어떤 요청을 가로채서 동작을 할 지 지정하는 이 클래스를 만들어야 함!!!
@Configuration //서버가 켜지면 내부 메서드를 모두 수행해서 설정 적용함 (설정용 어노테이션)
public class InterceptorConfig implements WebMvcConfigurer{
	
	
	//BoardTypeInterceptor가 아직 bean등록이 안돼있어서 bean 등록해주기
	
	@Bean //개발자가 만들어서 반환하는 객체를 bean으로 등록(관리는 Spring Container에게 넘긴다)
	public BoardTypeInterceptor boardTypeInterceptor() {
		return new BoardTypeInterceptor();
		//이거 기본생성자로 BoardTypeInterceptor객체 만드는데 
		//BoardTypeInterceptor클래스에서 @RequiredArgsConstructor 사용하면 private final을 쓰면 
		// BoardTypeInterceptor 참고하기
	} 

	
	
	//WebMvcConfigurer : 각종 오버라이딩 메서드 제공해주는 인터페이스
	//alt shift s -> override unimplemented method -> 오버라이딩 된 안의 내용들 다 지우면 된다
	// addInterceptors : 동작할 인터셉터 객체를 추가하는 메서드
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(boardTypeInterceptor())
		//생성되고 bean 으로 등록된 boardTypeInterceptor를 얻어와서 매개변수로 전달
		.addPathPatterns("/**")
		//가로챌 요청 주소를 지정(어떤 요청이 오는 것들만 가로챌 지)
		//여러 주소 작성 시 "","" -> 콤마로 구분해서 여러 개 쓰면 된다
		// /** : /(최상위 주소)로 시작하는 모든 요청 주소!! -> 모든 요청을 가로채게 된다!
		.excludePathPatterns("/css/**","/js/**",
						"/images/**", "/favicon.ico"); 
		//화면 만들 때 필요한 css 요청도 하는데, css로 시작하는 이하 요청(**)은 가로채지 않을거야
		//모든 요청 가로챌 건데 얘네들은 빼고 가로챌거야
		//가로 채지 않을 예외 주소 지정
		//include <-> exclued(미포함)
		
		
		
	}
	
	 
}
