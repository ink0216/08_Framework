package com.kh.test.customer.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.kh.test.customer.model.dto.Customer;

@Mapper
public interface CustomerMapper {

	/**고객 추가
	 * @param customer
	 * @return
	 */
	int add(Customer customer);

}
