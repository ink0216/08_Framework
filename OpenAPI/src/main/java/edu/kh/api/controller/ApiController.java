package edu.kh.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiController {
	@GetMapping("ex1") 
	public String ex1() {
		return "ex1"; //templates폴더의 ex1.html로 포워드
	}
}
