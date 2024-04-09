package com.kh.test.customer.model.service;

import com.kh.test.customer.model.dto.Customer;

public interface CustomerService {

	/**고객 추가
	 * @param customer
	 * @return
	 */
	int add(Customer customer);

}
