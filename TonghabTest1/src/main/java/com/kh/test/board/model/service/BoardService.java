package com.kh.test.board.model.service;

import java.util.List;

import com.kh.test.board.model.dto.Board;

public interface BoardService {

	/**제목으로 검색
	 * @param title
	 * @return boardList
	 */
	List<Board> search(String title);

}
