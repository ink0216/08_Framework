package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	/**게시판 종류 목록 조회
	 * @return
	 */
	List<Map<String, Object>> selectBoardTypeList();

	/**삭제되지 않은 게시글 수 조회하기
	 * @param boardCode
	 * @return
	 */
	int getListCount(int boardCode);

	/**특정 게시판의 지정된 페이지 목록 조회(공지 게시판의 2페이지 볼거다)
	 * @param boardCode
	 * @param rowBounds
	 * @return boardList
	 */
	List<Board> selectBoardList(int boardCode, RowBounds rowBounds);

	/**
	 * @param map
	 * @return
	 */
	Board selectOne(Map<String, Integer> map);

}
