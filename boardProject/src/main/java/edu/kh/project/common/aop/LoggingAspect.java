package edu.kh.project.common.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

// log를 찍기 위한 Aspect
@Component
@Aspect //AOP 적용, 관점 지향 프로그래밍
@Slf4j
public class LoggingAspect {
	/**컨트롤러 수행 전 로그 출력
	 * @param jp
	 */
	@Before("PointcutBundle.controllerPointCut()")
	//컨트롤러가 동작하기 전에
	public void beforeController(JoinPoint jp) {
		//클래스명, 메서드명 얻어오기
		
		//클래스명
		String className = jp.getTarget().getClass().getSimpleName();
		
		//메서드명
		String methodName = jp.getSignature().getName()+"()"; //메서드인거 알려주려고 소괄호 붙임
		
		//요청한 클라이언트의 HttpServletRequest 객체 얻어오는 방법
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		//서블릿이나 컨트롤러에서 HttpServletRequest 얻어올 때 이 코드가 자동완성됐었던것!
		
		//클라이언트의 ip 얻어오기
		String ip = getRemoteAddr(req);
		
		StringBuilder sb = new StringBuilder();
		//StringBuilder == 
		//자바에서 String은 문자열인데 String의 큰 문제가 있었다
		//String의 불변성떄문에 문제 생김!
		//값을 한 번 바꿀 떄 마다 새 객체가 계속 생성된다 -> 그래서 메모리를 계속 차지해서 메모리 효율이 안좋다
		//그래서 그 단점을 해결하기 위해서 StringBuilder를 사용한다
		

		sb.append(String.format("[%s.%s] 요청 / ip : %s", className, methodName, ip));

		
		if(req.getSession().getAttribute("loginMember") !=null) {
			//로그인 상태인 경우
			//세션에서 로그인멤버가 존재할 때
			String memberEmail = 
	((Member)req.getSession().getAttribute("loginMember")).getMemberEmail();
			
			sb.append(String.format(", 요청 회원 : %s", memberEmail)); //어떤 ip의 어떤 회원이 로그인했는지
		}
		log.info(sb.toString());
	}
	
	/**접속자의 ip를 얻어오는 메서드
	 * @param request
	 * @return ip
	 */
	private String getRemoteAddr(HttpServletRequest request
			) {

        String ip = null;

        ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("Proxy-Client-IP"); 
        } 

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("WL-Proxy-Client-IP"); 
        } 

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("HTTP_CLIENT_IP"); 
        } 

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("X-Real-IP"); 
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("X-RealIP"); 
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("REMOTE_ADDR");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getRemoteAddr(); 
        }

		return ip;
	}
	
	//-------------------------------------------------------------------------------------------------------------
	//서비스 관련
	//Object타입을 반환하겠다
	//around만 쓰는 방법이 특별하다
	// around에 들어가는 파라미터 특이
	// before나 after은 JoinPoint가 들어가는데
	// around는 JoinPoint를 상속받은 ProceedingJoinPoint가 들어간다
	
	// ProceedingJoinPoint
	// - JoinPoint를 상속받은 자식 객체
	// - @Around에서만 사용 가능
	// - JoinPoint와 유일하게 다른 점 : proceed()메서드 제공 
	//								-> proceed()메서드 호출 전/후로 Before/After가 구분되어짐
	
	//proceed가 호출되기 전에는 서비스가 호출되기 전에 메시지가 나올거고
	//proceed가 호출된 후에는 서비스가 호출된 후에 메시지가 나오는 원리
	
	// ** 주의할 점!**
	// 1) @Around 사용 시 반환형은 Object타입으로 지정해야 한다
	// 2) @Around 메서드 종료 시 proceed() 반환값을 return 해야 한다
	
	/**서비스 수행 전/후 동작하는 코드(advice)
	 * @param pjp : 
	 * @return
	 * @throws Throwable 
	 */
	@Around("PointcutBundle.serviceImplPointCut()")
	public Object aroundServiceImpl(
			ProceedingJoinPoint pjp
			) throws Throwable {
		// 이 영역이 @Before 부분이 됨
	
		//클래스명, 메서드명 얻어오기
		
		//클래스명
		String className = pjp.getTarget().getClass().getSimpleName();
		
		//메서드명
		String methodName = pjp.getSignature().getName()+"()"; //메서드인거 알려주려고 소괄호 붙임
		
		log.info("===== {}.{} 서비스 호출됨 =====", className, methodName);
		
		//서비스 호출 시 전달된 파라미터 얻어오기
		log.info("Parameter : {}", Arrays.toString(pjp.getArgs())); //배열을 문자열로 바꿔주는 Arrays.toString()
		
		//서비스 코드 실행 시작 시간을 기록
		long startMs = System.currentTimeMillis();
		
		// 1000ms == 1s
		//자바는 1070.1.1 09:00를 0ms로 삼아서 현재 시간까지 몇 ms가 지났는지가  System.currentTimeMillis();로 나온다
		//그떄부터 54년 4개월 7일 2시간 25분정도 지났다
		
		
		
		//---------------------------------------------------------------------
		Object obj = pjp.proceed(); //여기가 전/후를 나누는 기준점이 된다
		//---------------------------------------------------------------------
		// 이 영역이 @After 부분이 됨
		log.info("=============================================================================================");
		
		//서비스 코드 실행 종료 시간을 기록
		long endMs = System.currentTimeMillis();
		
		//서비스 실행 소요 시간이 기록된다
		log.info("RunningTime : {}ms 소요", endMs-startMs);
		return obj;
	}
	//-------------------------------------------------------------------------------------------------------------
	/** Transactional 어노테이션이 롤백 동작 후 수행되는 코드
	 * 사용 조건 : 서비스 메서드 레벨로 Transacional 어노테이션이 작성되어야 동작함
	 *  			->클래스 레벨로 작성하고 싶으면?
	 *  				
	 * @param jp
	 * @param ex
	 */
	@AfterThrowing(pointcut = "@annotation(org.springframework.transaction.annotation.Transactional)", 
			// pointcut 쌍따옴표 안에 @Around("PointcutBundle.serviceImplPointCut()")라고 쓰면
			//클래스 단위로, 해당 클래스 내에서 모든 종류의 예외 발생 시 코드가 수행되도록 만들 수 있다
			
			//이 어노테이션이 동작한 후 라는 의미가 된다!
//pointcut : advice를 끼워넣는 시점인데
// 이 어노테이션은 예외 발생 시 수행됨
							throwing = "ex") //AfterReturning은 별로 쓸 데 없을 것 같다
//	@AfterThrowing : 예외 발생 후 수행되는 코드. 작성법 독특함
	public void transactionRollback(JoinPoint jp , Throwable ex) {
		//뭔가가 실패해서 트랜잭션에서 롤백된 경우
		//속성 두 개 써야 한다
//		Exception의 형제로 Error가 있는데 얘네 둘의 부모가 Throwable이다
		//예외가 발생한 후인데 그 예외를 여기다 가져다 쓰겠다
		log.info("******** 트랜잭션이 롤백됨 {} **********", jp.getSignature().getName());
		log.error("[롤백 원인] : {}", ex.getMessage()); //에러 내용이 중괄호 안에 나옴
	}
}
