package edu.kh.project.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

//@ControllerAdvice 어노테이션
// - 전역적 예외 처리 (한 프로젝트 전체에 대한 예외 처리) ***이걸 가장 많이 사용
// - 리소스 바인딩(자원 연결)도 가능
// - 메시지 컨버젼(파라미터를 특정 형태로 바꾸는 것, Message Converter)
@ControllerAdvice
public class ExceptionController {
	// 404 발생 시 처리
	@ExceptionHandler(NoResourceFoundException.class)
	// 이게 404 뜨는 경우의 예외임
	public String notFound() {
		return "error/404";
	}

	
	//프로젝트에서 발생하는 해당 종류의 예외를 처리
	//					예외 종류
	@ExceptionHandler(Exception.class)
		//모든 예외를 잡아서 처리하겠다
		public String allExceptionHandler(Exception e , Model model) {
			//e.printStackTrace();
			model.addAttribute("e", e);
			return "error/500";
		}
	}

