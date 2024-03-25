package edu.kh.todo.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.todo.model.dto.Todo;
//ibatis ==Mybatis의 옛날 이름
//얘가 DAO를 대체한다
/*@Mapper
 * - Mybatis 제공 어노테이션(스프링이 제공하는 것 아님!)
 * - 해당 어노테이션이 작성된 인터페이스(접점)는
 * 		namespace에 해당 인터페이스가 작성된
 * 		mapper.xml 파일과 연결되어 SQL을 호출/수행/결과 반환이 가능하도록 만든다
 * - TodoMapper는 인터페이스여서 인터페이스 자체는 객체가 될 수 없지만
 * - Mybatis에서 제공하는 Mapper 상속 객체가 bean으로 자동으로 등록됨
 * 	-->인터페이스를 만들기만 하면 TodoMapper를 상속받은 것을 이용해서 스프링이 일해줘서 Bean 객체로 만들어준다
 * 
 * 우리가 만든 인터페이스를 상속한 클래스를 Mybatis가 만들고 그걸 이용해서 Bean으로 등록한다
 * */
@Mapper
public interface TodoMapper {
	/* Mapper의 메서드명 == mapper.xml 파일 내 태그의 id랑 같다
	 * (메서드명과 id가 같은 태그가 서로 연결된다!!!)
	 * */

	/**할 일 목록 조회하기
	 * @return todoList
	 */
	List<Todo> selectAll();
	//인터페이스에는 추상메서드만!

	/**완료된 할 일 개수 조회
	 * @return completeCount
	 */
	int getCompleteCount(); //todo-mapper.xml과 붙어있는데 아이디와 메서드명이 같으면 호출된다

	/**할 일 추가
	 * @param todo 
	 * @return result(행의 개수) //같은 아이디 가지는 todo-mapper.xml의 sql이 실행된다
	 */
	int addTodo(Todo todo);

	/**할 일 상세 조회
	 * @param todoNo
	 * @return todo
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

	/**전체 할 일 개수 조회
	 * @return totalCount
	 */
	int getTotalCount(); 
}
