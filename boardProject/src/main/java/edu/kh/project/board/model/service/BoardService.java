package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.Board;

public interface BoardService {
	//인터셉터 호출 코드
	//반환할 게 맵 타입이야 그 안에는 두 컬럼값이 들어갈거야
	/**게시판 종류 목록 조회
	 * @return
	 */
	List<Map<String , Object>> selectBoardTypeList();
	//Object는 모든 클래스의 최상위 부모여서 모든 것들을 다 담을 수 있다
	//map : 한 행
		// {"boardCode" : 1,
		//	"boardName" : "공지 게시판"}
		
		//map : 한 행
		// {"boardCode" : 2,
		//	"boardName" : "정보 게시판"}
		
		//map : 한 행
		// {"boardCode" : 3,
		//	"boardName" : "자유 게시판"}
		
		//위의 map들을 리스트로 묶어서 가져옴
	//------------------------------------------------------------
	
	/**특정 게시판의 지정된 페이지 목록 조회(공지 게시판의 2페이지 볼거다)
	 * @param boardCode
	 * @param cp
	 * @return map
	 */
	Map<String, Object> selectBoardTypeList(int boardCode, int cp);

	/**게시글 상세 조회
	 * @param map
	 * @return board
	 */
	Board selectOne(Map<String, Integer> map);
	
	
	
}
