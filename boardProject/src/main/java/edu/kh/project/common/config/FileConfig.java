package edu.kh.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration //설정 클래스니까 서버 시작 시 이거 다 읽어
@PropertySource("classpath:/config.properties") //DBConfig에서 썼었다!
//@PropertySource : config.properties에 작성된 내용을 현재 클래스에서 사용하겠다는 어노테이션
public class FileConfig implements WebMvcConfigurer{ 
	//WebMvcConfigurer : 파일 업로드 시 기본 구성이 있는데 사용자가 다시 구성하겠다 하는 것
	//얘는 인터페이스인데 오버라이딩 강제되지 않아서 우리가 골라서 몇 개만 오버라이딩 할 수 있다
	
	
	
	
	// config.properties에 작성된 파일 업로드 임계값 얻어와 필드에 대입
	@Value("${spring.servlet.multipart.file-size-threshold}") //해당 키 가지는 값 가져와서 대입
	private long fileSizeThreshold;
	
	@Value("${spring.servlet.multipart.max-request-size}") //해당 키 가지는 값 가져와서 대입
	private long maxRequestSize; //요청당 파일 최대 크기
	
	@Value("${spring.servlet.multipart.max-file-size}") //해당 키 가지는 값 가져와서 대입
	private long maxFileSize; //개별 파일 당 최대 크기
	
	@Value("${spring.servlet.multipart.location}") //해당 키 가지는 값 가져와서 대입
	private String location; //임계값 초과시 임시 저장 폴더 경로
	
	//WebMvcConfigurer 오버라이딩 1
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//요청 주소에 따라 서버 컴퓨터의 어떤 경로에 접근할 지 설정
		/*localhost/fileTest/~~~
		 * fileTest 요청 왔을 때 내 컴퓨터의 이 폴더에 접근할 수 있게 하겠다
		 * 외부 요청과 내 컴퓨터의 경로를 연결할 수 있다
		 * 이런거를 할 수 있다*/
		registry
		.addResourceHandler("/myPage/file/**") //클라이언트 요청 주소 패턴을 여기에 작성
		// /myPage/file/로 시작하는 요청이 오면 밑의 경로랑 연결해줄거야
		.addResourceLocations("file:///C:\\uploadFiles\\test\\");
		//서버 컴퓨터랑 파일 통신을 할거라는 프로토콜 == file:///
	}
	
	/*MultipartResolver 관련 설정*/
	@Bean //반환되는 MultipartConfigElement를 빈으로 등록
	public MultipartConfigElement configElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));
		//이 숫자를 바이트 단위로 해서 넣어라
		
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));
		factory.setLocation(location);
		
		return factory.createMultipartConfig(); 
		//만들어서 반환할건데 공장이 있어야 만든다(속성 설정 가능)
		//공장을 이용해서 MultipartConfig를 만들어서 반환한다
	}
	@Bean
	public MultipartResolver multipartResolver() {
		//MultipartResolver 객체를 Bean으로 추가
		//	-> 추가될 때 위에서 만든 MultipartConfig를 자동으로 이용하게 돼 있다
		StandardServletMultipartResolver multipartResolver 
		= new StandardServletMultipartResolver();
		
		return multipartResolver;
	}
	
}
