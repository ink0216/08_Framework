package edu.kh.todo.model.service;

import java.util.Map;

import edu.kh.todo.model.dto.Todo;

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

	/**할 일 상세 조회하기
	 * @param todoNo
	 * @return todo(조회 결과가 없으면 null이 반환될 수도 있다)
	 */
	Todo todoDetail(int todoNo);

	/**할 일 삭제하기
	 * @param todoNo
	 * @return result
	 */
	int todoDelete(int todoNo);

	/**할 일 수정하기
	 * @param todo
	 * @return result
	 */
	int todoUpdate(Todo todo);

	/**완료 여부 수정하기
	 * @param todo
	 * @return result
	 */
	int changeComplete(Todo todo);

	/**전체 할 일 개수 조회하기
	 * @return totalCount
	 */
	int getTotalCount();

	/**완료된 할 일 개수 조회
	 * @return completeCount
	 */
	int getCompleteCount();


}
