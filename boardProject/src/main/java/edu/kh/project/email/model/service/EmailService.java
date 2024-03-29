package edu.kh.project.email.model.service;

import org.springframework.stereotype.Service;

@Service //Bean 등록 + Service 역할 명시
public interface EmailService {

	/**이메일 보내기
	 * @param string
	 * @param email
	 * @return authKey
	 */
	String sendEmail(String string, String email);

}
