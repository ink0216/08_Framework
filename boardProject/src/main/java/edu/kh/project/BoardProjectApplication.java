package edu.kh.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//SPRING SECURITY 사용하면 실행 후 LOCALHOST들어가면 로그인 화면 나옴->안되도록하기
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) //그거 제외하기
public class BoardProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardProjectApplication.class, args);
	}

}
