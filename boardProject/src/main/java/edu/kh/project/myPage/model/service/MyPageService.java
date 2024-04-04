package edu.kh.project.myPage.model.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

	/**회원 정보 수정
	 * @param inputMember
	 * @param memberAddress
	 * @return result
	 */
	int updateInfo(Member inputMember, String[] memberAddress);

	/**비밀번호 변경
	 * @param memberNo
	 * @param currentPw
	 * @param newPw
	 * @return result
	 */
	int changePw(int memberNo, Map<String, Object> paramMap);

	/**회원 탈퇴
	 * @param inputPw
	 * @param memberNo
	 * @return result
	 */
	int secession(String inputPw, int memberNo);

	/**파일 업로드 테스트 1
	 * @param uploadFile
	 * @return path(경로)
	 */
	String fileUpload1(MultipartFile uploadFile)throws IllegalStateException, IOException;

}
