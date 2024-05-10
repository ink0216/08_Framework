package edu.kh.project.board.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional //Unchecked exception 발생 시 롤백
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
	private final CommentMapper mapper;
	
	//댓글 목록 조회
	@Override
	public List<Comment> select(int boardNo) {
		return mapper.select(boardNo);
	}
	
	//댓글 등록
	@Override
	public int insert(Comment comment) {
		int result = mapper.insert(comment);
		if(result>0) return comment.getCommentNo(); //등록 성공 시 댓글 번호를 반환 (얕은 복사여서 여기서 리턴하는게 새로 등록된 댓글의 번호가 된다)
		return 0; //등록 실패 시 0을 반환
		
	}
	
	//댓글 삭제
	@Override
	public int delete(int commentNo) {
		return mapper.delete(commentNo);
	}
	
	//댓글 수정
	@Override
	public int update(Comment comment) {
		return mapper.update(comment);
	}
}
