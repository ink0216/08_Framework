package edu.kh.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoProject1Application {

	public static void main(String[] args) {
		//메인메서드 -> 이거 실행하면 SpringApplication을 RUN한다
		//자바 실행 : CTRL F11
		//실행하는 것 java project 누르면됨(run on server아님!)
		SpringApplication.run(DemoProject1Application.class, args);
	}

}
