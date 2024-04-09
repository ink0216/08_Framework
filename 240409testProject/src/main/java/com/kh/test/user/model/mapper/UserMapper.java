package com.kh.test.user.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.test.user.model.dto.User;

@Mapper
public interface UserMapper {

	/**아이디 검색
	 * @param searchId
	 * @return
	 */
	List<User> search(String searchId);

}
