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

	/**게시글 상세 조회
	 * @param map
	 * @return
	 */
	Board selectOne(Map<String, Integer> map);

	/**좋아요 해제(DELETE)
	 * @param map
	 * @return result
	 */
	int deleteBoardLike(Map<String, Integer> map);

	/**좋아요 체크(INSERT)
	 * @param map
	 * @return result
	 */
	int insertBoardLike(Map<String, Integer> map);

	/**게시글 좋아요 개수 조회
	 * @param temp
	 * @return count
	 */
	int selectLikeCount(int temp);

	/**조회수 1 증가
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);

	/**조회수를 조회
	 * @param boardNo
	 * @return
	 */
	int selectReadCount(int boardNo);

	/**검색 조건에 맞는 게시글 수 조회하기(카운트한 결과값이 반환된다)
	 * @param paramMap
	 * @return
	 */
	int getSearchCount(Map<String, Object> paramMap);

	/**지정된 페이지의 검색 결과 목록 조회
	 * @param paramMap
	 * @param rowBounds
	 * @return boardList
	 */
	List<Board> selectSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

}
