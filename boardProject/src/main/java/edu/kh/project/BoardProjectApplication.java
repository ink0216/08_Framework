package edu.kh.project;
//서버 start -> 각종 설정 읽어들임(서버 기본 설정, 자동 설정, 사용자 설정(DBCP등))
//				+ComponentScan(Bean 등록 구문)(@Component, @Controller, @Service, @Mapper)
//		->클라이언트 요청 대기 상태
//		->클라이언트 요청 오면 ->내부의 DispatcherServlet <->Controller <->Service호출 or 리다이렉트 or 포워드
//																<->DAO/Mapper	
//																<->DB
//DispatcherServlet에서 1) forward인 경우 -> ViewResolver(접두사+반환값+접미사)에게 넘김
//						2) redirect인 경우 -> 재요청한 Controller
//						3) 비동기 요청인 경우 -> 요청한 JS 코드로 돌려보냄
//						==>클라이언트에 응답
//Mybatis : Mapper
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
//SPRING SECURITY 사용하면 실행 후 LOCALHOST들어가면 로그인 화면 나옴->안되도록하기
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) //그거 제외하기
@EnableScheduling // 스프링 스케쥴러를 활성화하는 어노테이션
public class BoardProjectApplication {

	public static void main(String[] args) { //자바 프로젝트를 실행시키는 역할 하는 메인 메서드
		SpringApplication.run(BoardProjectApplication.class, args);
	}

}
