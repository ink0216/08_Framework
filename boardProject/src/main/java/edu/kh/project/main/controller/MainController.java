package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller //bean(스프링이 관리하는 객체) 등록
public class MainController {
	@RequestMapping("/") // "/" 요청 매핑(method 가리지 않음(get이든 post든 뭐든 다 받음))
	public String mainPage() {
		return "common/main"; //common에 있는 main.html로 포워드하겠다
	}
}