package edu.kh.todo.model.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
//Beans :Bean들이 모인 곳 
//	그 안에 TodoServiceImpl이 객체로 생성돼서 들어가있음
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.mapper.TodoMapper;
//Servlet.JSP에서의 Service : 비즈니스 로직(데이터 가공, 트랜잭션 처리) 역할

//--------------------------------------------------
//@Transactional
//- 트랜잭션 처리를 수행하라고 지시하는 어노테이션
//(== 선언적 트랜잭션 처리)

//- 정상 코드 수행 시 서비스가 끝났을 때 무조건 COMMIT해줌

//- 기본값 : Service 내부 코드 수행 중 RuntimeException(스프링은 모든 예외를 RuntimeException로 만듦) 발생 시 rollback해줌

//- rollbackFor 속성 : 어떤 예외가 발생했을 때 rollback할지 지정
//---------------------------------------------------
//모든 종류의 예외 발생 시 바로 rollback 수행하라는 의미가 됨 ->그게 아니면 커밋해줌
@Transactional(rollbackFor=Exception.class)
@Service // 비즈니스 로직 역할 명시 + Bean으로 등록(스프링이 알아서 만들어주고 사용하고 해줌)
public class TodoServiceImpl implements TodoService{
	
	//이제 DAO가 다른 이름으로 바뀐다!
	@Autowired //DI(의존성 주입)해주면 된다 ->그럼 만들어져서 밑의 mapper에 들어올거다
	private TodoMapper mapper;
			
	//할 일 목록 + 완료된 할 일 개수 조회
	@Override
	public Map<String, Object> selectAll() {
		//여기서는 Connection만들 필요 없다
		//DBconfig가 SqlSessionTemplate을 만들어서 Bean으로 등록해서 이미 만들어져 있어서!!!
		//이미 존재하는 것을 코드만 쓰면 가져다줌
		
		
		//1. 할 일 목록 조회 
//		List<Todo> todoList = dao.selectAll(conn) : 옛날 코드
		List<Todo> todoList = mapper.selectAll(); //매퍼
		
		//2. 완료된 할 일 개수 조회하기
		int completeCount = mapper.getCompleteCount(); //mapper의 메서드 호출해서 결과받겠다
		
		//Map으로 묶어서 반환하기
		Map<String, Object> map = new HashMap<>();
		map.put("todoList", todoList);
		map.put("completeCount", completeCount); //->메인컨트롤러까지 돌아감!
		//DBCP를 이용해서 서비스 끝나면 커넥션 자동으로 반납처리가 돼서 close안해도됨
		return map;
	}
	
	//할 일 추가
	@Override
	public int addTodo(String todoTitle, String todoContent) {
		//Connection 생성/반환 안해도 된다
		//트랜잭션 제어는 @Transactional 어노테이션 써서 트랜잭션 매니저가 해줘서 여기다 안써도 된다
		
		//마이바티스에서 SQL에 전달할 수 있는 파라미터의 개수는
		//오직 1개이다!!!!!!!
		//근데 지금은 파라미터가 두개를 넘겨줘야함
		// -> todoTitle, todoContent를 Todo DTO로 묶어서 전달해보자(데이터 가공)
		Todo todo = new Todo(); //컨트롤러에서 해도 됨!
		todo.setTodoTitle(todoTitle);
		todo.setTodoContent(todoContent); //todo를 전달한다
		return mapper.addTodo(todo);
	}
}
