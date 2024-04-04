package edu.kh.project.main.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MainMapper {

	/**비밀번호 초기화
	 * @param map
	 * @return result
	 */
	int resetPw(Map<String, Object> map);

	/**탈퇴한 회원인 지 확인
	 * @param outResetMemberNo
	 * @return result1
	 */
	int checkOut(int outResetMemberNo);

	/**해당 회원 탈퇴 복구시키기
	 * @param outResetMemberNo
	 * @return result
	 */
	int outReset(int outResetMemberNo);

	/**등록된 회원인지 확인
	 * @param outResetMemberNo
	 * @return
	 */
	int select(int outResetMemberNo);

}
