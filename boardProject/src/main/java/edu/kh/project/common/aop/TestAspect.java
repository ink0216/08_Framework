package edu.kh.project.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

//@Component // bean으로 등록해서 컨트롤러에서 서비스로 갈 때 자동으로 동작하게 스프링에게 넘기기
//@Aspect //공통 관심사가 작성된 클래스임을 명시(AOP 동작용 클래스이다) (중간에 끼워넣을 코드를 작성한 클래스이다)
// -> 이 위의 두 어노테이션 지우면 이 코드가 동작하지 않는다!!

@Slf4j //로그 찍기용 // log를 찍을 수 있는 객체 생성 코드를 추가하는 어노테이션
// lombok : 자주 쓰는 코드를 자동완성해주는 라이브러리
public class TestAspect {
	// Pointcut : 실제 Advice를 적용할 JoinPoint(지점)
	//advice == 끼워 넣을 코드(메서드 단위로 작성)
	// <@Pointcut() 작성 방법>
	// execution( [접근제한자(생략가능)] 리턴타입 클래스명 메소드명 ([파라미터]) )
		
//	@Pointcut() //이 advice를 어떤 지점에 끼워넣을 지 지정(Pointcut)
	//Pointcut작성법이 어려운데 가장 쉬운 작성법 하나만 알고 있어도 다 할 수 있다
//	@Before(Pointcut)
	@Before("execution(* edu.kh.project..*Controller*.*(..))") //접근제한자와 같은 것만 실행할거야(public만,,근데 생략 많이 하고, 리턴 타입은 전체로 ->*)
	//MemberContoller, BoardController등 이름의 공통점은 ~~~Controller로 끝난다는 것!
	// 클래스명은 패키지명부터 다 작성하는 거다!!!
	// .. : project "이하"의 모든 컨트롤러라는 뜻이 된다!!!
	// * : 모든
	// edu.kh.project.. : edu.kh.project 이하 패키지
	// Controller뒤에 단어 더 있을 수도 있고 메서드명도 상관없어
	// 메서드명(..)에서 매개변수의 .. : 매개변수 개수 상관 없다는 뜻 <-매개변수는 전체 상관 없다는 뜻으로 * 안쓰고 ..쓴다!!!
	// 모든 메서드가 동작하기 전에 수행하겠다
	public void testAdvice() {
		log.info("----------------------------- testAdvice() 수행됨------------------------------------------");
	}
	
	//컨트롤러가 끝났을 때 어떤 컨트롤러가 끝났는 지 출력해보기
	@After("execution(* edu.kh.project..*Controller*.*(..))") //타겟의 동작이 끝난 후에 실행되는 것!
	public void controllerEnd(JoinPoint jp //인터페이스여서 이걸 상속받은 객체가 이 자리에 들어오게 된다!!!!
//			@Before든 @After든 다 JoinPoint를 매개변수로 사용할 수 있다
			//JoinPoint 객체 : AOP 부가 기능이 적용된 대상
			// 이것의 제공 메서드 이용해보기
			) {
		//AOP가 적용된 클래스 이름 얻어오기
		String className = jp.getTarget().getClass().getSimpleName(); //프록시로 감싸져있던 Target Object
		//여기서는 모든 컨트롤러를 Target Object로 설정함
		//해당 타겟의 클래스 정보중에서 이름을 얻어오면 어떤 타겟이 수행됐는지 알 수 있다
		//getName하면 패키지까지 다나오고, getSimpleName하면 패키지는 빠지고 클래스명만 나옴!
		
		//메서드 이름 얻어오기
		//signature == 메서드 선언부
		//jp.getSignature().getName() : 실행된 컨트롤러 메서드의 이름 얻어오기
		String methodName = jp.getSignature().getName();
		
		log.info("-------------- {}.{} 수행 완료 --------------", className, methodName);
	}
	
}
