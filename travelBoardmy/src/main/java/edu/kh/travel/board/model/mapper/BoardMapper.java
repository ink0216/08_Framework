package edu.kh.travel.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

	/**인터셉터로 게시판 종류 얻어오기
	 * @return boardTypeList
	 */
	List<Map<String, Object>> selectBoardTypeList();

}
