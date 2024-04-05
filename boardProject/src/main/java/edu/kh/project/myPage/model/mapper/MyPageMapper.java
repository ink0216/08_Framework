package edu.kh.project.myPage.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;

@Mapper //mapper.xml의 SQL을 실행하는 역할
public interface MyPageMapper {

	/**회원 정보 수정
	 * @param inputMember
	 * @return result
	 */
	int updateInfo(Member inputMember);

	/**저장돼있던 비밀번호 조회
	 * @param memberNo
	 * @return 암호화 돼서 저장돼있던 비밀번호
	 */
	String selectPw(int memberNo);

	/**비밀번호 변경
	 * @param map
	 * @return result
	 */
	int changePw(Map<String, Object> paramMap);

	/**회원 탈퇴
	 * @param memberNo
	 * @return result
	 */
	int secession(int memberNo);

	/**파일이 아닌, 파일의 정보를 DB에 삽입 
	 * @param uf
	 * @return result
	 */
	int insertUploadFile(UploadFile uf);

	/**업로드한 파일 목록 조회
	 * @return
	 */
	List<UploadFile> fileList();

	/**프로필 이미지 변경
	 * @param mem
	 * @return result
	 */
	int profile(Member mem);

	
}
