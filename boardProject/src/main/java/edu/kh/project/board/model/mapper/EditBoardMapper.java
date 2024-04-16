package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;

@Mapper
public interface EditBoardMapper {

	/**게시글 글 부분만 작성(insert)
	 * @param inputBoard
	 * @return result
	 */
	int boardInsert(Board inputBoard);

	/**게시글 이미지 모두 삽입
	 * @param uploadList
	 * @return result (몇 행 삽입됐는지)
	 */
	int insertUploadList(List<BoardImg> uploadList);

	/**게시글 삭제
	 * @param map
	 * @return result
	 */
	int delete(Map<String, Integer> map);

}
