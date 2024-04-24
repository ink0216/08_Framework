package edu.kh.travel.board.model.service;

import java.util.List;
import java.util.Map;

public interface BoardService {

	/**인터셉터로 게시판 종류 얻어오기
	 * @return boardTypeList
	 */
	List<Map<String, Object>> selectBoardTypeList();

}
