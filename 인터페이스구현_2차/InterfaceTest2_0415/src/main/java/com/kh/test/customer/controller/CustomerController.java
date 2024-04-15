package com.kh.test.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.kh.test.customer.model.dto.Customer;
import com.kh.test.customer.model.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustomerController {
	private final CustomerService service;
	@PostMapping("edit")
	public String edit(
			/*@ModelAttribute*/ Customer customer,
			Model model
			) {
		int customerNo = customer.getCustomerNo();
		int result = service.search(customerNo);
		if(result>0) {
			//그 번호의 회원이 있는 경우
			int result1 = service.edit(customer);
			model.addAttribute("message", "수정 성공!!!");
		}
		return "result";
	}
}
