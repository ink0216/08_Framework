package com.kh.test.user.model.service;

import java.util.List;

import com.kh.test.user.model.dto.User;

public interface UserService {

	/**아이디 검색
	 * @param searchId
	 * @return
	 */
	List<User> search(String searchId);

}
