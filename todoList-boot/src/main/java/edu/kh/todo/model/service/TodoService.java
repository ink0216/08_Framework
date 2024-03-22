package edu.kh.todo.model.service;

import java.util.Map;

public interface TodoService {

	/**할 일 목록 + 완료된 할 일 개수 조회하기(메인페이지 열리자마자 나오도록)
	 * @return map
	 */
	Map<String, Object> selectAll();

	/**할 일 추가하기
	 * @param todoTitle
	 * @param todoContent
	 * @return result(행의 개수)
	 */
	int addTodo(String todoTitle, String todoContent);

}
