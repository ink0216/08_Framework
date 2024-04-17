package edu.kh.project.board.model.exception;
//이미지 삭제하다가 예외 발생했다
//사용자 정의 예외 만들거다
/*이미지 삭제 중 문제 발생 시 사용할 사용자 정의 예외
 * 
 * */
public class ImageDeleteException extends RuntimeException{
	public ImageDeleteException() {
		super("이미지 삭제 중 예외 발생");
	}
	public ImageDeleteException(String message) {
		super(message);
	}
}
