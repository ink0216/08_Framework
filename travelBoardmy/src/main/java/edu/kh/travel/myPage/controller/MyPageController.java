package edu.kh.travel.myPage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.travel.myPage.model.service.MyPageService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("myPage")
public class MyPageController {
	
	private final MyPageService service;
	
	
	@GetMapping("info")
	public String updateInfo() {
		
		return null;
	}

}
