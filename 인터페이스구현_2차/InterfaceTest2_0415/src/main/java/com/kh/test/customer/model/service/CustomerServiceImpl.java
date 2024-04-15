package com.kh.test.customer.model.service;

import org.springframework.stereotype.Service;

import com.kh.test.customer.model.dto.Customer;
import com.kh.test.customer.model.mapper.CustomerMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
	private final CustomerMapper mapper;
	//고객 번호로 고객 검색
	@Override
	public int search(int customerNo) {
		return mapper.search(customerNo);
	}
	
	//적은 정보로 고객 정보 수정
	@Override
	public int edit(Customer customer) {
		return mapper.edit(customer);
	}
}
