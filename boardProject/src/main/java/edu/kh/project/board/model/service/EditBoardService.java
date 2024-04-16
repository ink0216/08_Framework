package edu.kh.project.board.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;

public interface EditBoardService {

	/**게시글 작성
	 * @param inputBoard
	 * @param images
	 * @return boardNo
	 */
	int boardInsert(Board inputBoard, List<MultipartFile> images)throws IllegalStateException, IOException;

	/**게시글 삭제
	 * @param boardCode
	 * @param boardNo
	 * @return result
	 */
	int delete(int boardCode, int boardNo);

}
