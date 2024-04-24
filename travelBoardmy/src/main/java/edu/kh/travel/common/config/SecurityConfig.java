package edu.kh.travel.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/*@Configuration 어노테이션
 * - 설정용 클래스임을 명시 
 * 		+ 객체로 생성해서 내부 코드를 서버 실행 시 모두 수행시킨다! 
 * 		(이 안에 코드 써넣으면 서버 실행 시 모든 메서드가 다 실행된다!)
 * 	->이 안에 설정 내용 적으면 다 설정이 적용된다
 * 
 * @Bean
 * - 개발자가 수동으로 생성한 객체의 관리를 스프링에게 넘기는 어노테이션
 * 	(Bean으로 등록)
 * */
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration 
public class SecurityConfig {
	@Bean //->memberServiceImpl에서 쓰기
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder(); //객체는 수동으로 만듦 -> 관리는 스프링이 해라 == @Bean
	}
}
