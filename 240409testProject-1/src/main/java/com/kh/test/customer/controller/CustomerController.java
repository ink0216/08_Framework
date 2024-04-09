package com.kh.test.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.test.customer.model.dto.Customer;
import com.kh.test.customer.model.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class CustomerController {
	private final CustomerService service;
	
	@PostMapping("add")
	public String add(
			Customer customer,
			//@ModelAttribute 생략
			Model model
			) {
		String message=null;
		
		//이름 전화번호 주소

		int result = service.add(customer);
		model.addAttribute("customer", customer);
		return "/result";
		
	}
}
