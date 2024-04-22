package edu.kh.travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude=SecurityAutoConfiguration.class)
//자동설정을 포함하지 않겠다
public class TravelBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelBoardApplication.class, args);
	}

}
