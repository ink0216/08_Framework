package edu.kh.project.myPage.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;

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

	/**파일 업로드 테스트 2(+DB)
	 * @param uploadFile
	 * @param memberNo
	 * @return result
	 */
	int fileUpload2(MultipartFile uploadFile, int memberNo)throws IllegalStateException, IOException;
//transferTo하면 예외가 생긴다 -> 던지기
	/**업로드한 파일 목록 조회
	 * @return
	 */
	List<UploadFile> fileList();

	/**한 번에 여러 파일 업로드
	 * @param aaaList
	 * @param bbbList
	 * @param memberNo
	 * @return
	 */
	int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo)
			throws IllegalStateException, IOException;

	/**프로필 이미지 변경하기
	 * @param profileImg
	 * @param loginMember
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int profile(MultipartFile profileImg, Member loginMember)throws IllegalStateException, IOException;

}
