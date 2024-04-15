package com.kh.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.test.model.dto.User;
import com.kh.test.model.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	private final UserService service;
	@GetMapping("select")
	public String select(
			@RequestParam("selectNo") int memberNo,
			Model model
			) {
		User user = service.select(memberNo);
		String path = null;
		if(user !=null) {
			
			path="searchSuccess";
			model.addAttribute("user", user);
		}else {
			path="searchFail";
		}
		return path;
	}
}
