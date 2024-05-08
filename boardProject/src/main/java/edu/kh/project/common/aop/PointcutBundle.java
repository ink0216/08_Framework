package edu.kh.project.common.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Pointcut;

//Pointcut을 모아놓는 클래스
public class PointcutBundle {
	//Pointcut == 실제 Advice가 적용될 지점
	
	@Pointcut("execution(* edu.kh.project..*Controller*.*(..))")
	//이건 중간중간에 동작하는 것이 아닌,
	//작성하기 어려운 Pointcut을 미리 여기에 작성해서
	//필요한 곳에서 클래스명.메서드명으로 호출해서 사용 가능하게 만들어 둔 것!
	// 소괄호 내의 작성법이 복잡해서!
	//다른 곳에서 @Before("execution(* edu.kh.project..*Controller*.*(..))")대신
	//			== @Before("PointcutBundle.controllerPointCut()") 이렇게 호출해서 쓰도록
	//여기에 미리 써놓고 필요할 떄 불러서 쓰도록 만듦 
	public void controllerPointCut() {}
	
	@Pointcut("execution(* edu.kh.project..*ServiceImpl*.*(..))")
	public void serviceImplPointCut() {}
}
