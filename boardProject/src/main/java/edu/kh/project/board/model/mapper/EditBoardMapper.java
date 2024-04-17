package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;

@Mapper
public interface EditBoardMapper {

	/**게시글 글(제목/내용) 부분만 작성(insert)
	 * @param inputBoard
	 * @return result
	 */
	int boardInsert(Board inputBoard);

	/**게시글 이미지 여러 행 모두 삽입
	 * @param uploadList
	 * @return result (몇 행 삽입됐는지)
	 */
	int insertUploadList(List<BoardImg> uploadList);

	/** 게시글 삭제
	 * @param map
	 * @return result
	 */
	int boardDelete(Map<String, Integer> map);

	/**게시글 글(제목/내용) 부분만 작성(update)
	 * @param inputBoard
	 * @return
	 */
	int boardUpdate(Board inputBoard);

	/**게시글 이미지 수정 시 삭제된 이미지가 있는 경우 이미지 삭제
	 * @param map
	 * @return
	 */
	int deleteImage(Map<String, Object> map);

	/**게시글 이미지 새 이미지로 수정
	 * @param img
	 * @return
	 */
	int updateImage(BoardImg img);

	/**게시글에 새 이미지를 삽입(1행씩만)
	 * @param img
	 * @return
	 */
	int insertImage(BoardImg img);
}
