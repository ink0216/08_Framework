package com.kh.test.customer.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.kh.test.customer.model.dto.Customer;

@Mapper
public interface CustomerMapper {

	/**고객 번호로 고객 검색
	 * @param customerNo
	 * @return
	 */
	int search(int customerNo);

	/**적은 정보로 고객 정보 수정
	 * @param customer
	 * @return
	 */
	int edit(Customer customer);

}
