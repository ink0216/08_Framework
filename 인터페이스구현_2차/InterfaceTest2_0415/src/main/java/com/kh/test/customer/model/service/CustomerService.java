package com.kh.test.customer.model.service;

import com.kh.test.customer.model.dto.Customer;

public interface CustomerService {

	/**고객 번호로 고객 검색
	 * @param customerNo
	 * @return
	 */
	int search(int customerNo);

	/**적은 값으로 회원 정보 수정
	 * @param customer
	 * @return
	 */
	int edit(Customer customer);

}
