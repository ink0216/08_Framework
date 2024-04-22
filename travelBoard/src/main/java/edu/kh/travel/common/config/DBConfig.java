package edu.kh.travel.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//DB연결을 위한 설정 적는 클래스
/*@Configuration 어노테이션
 * - 설정용 클래스임을 명시 
 * 		+ 객체로 생성해서 내부 코드를 서버 실행 시 모두 수행시킨다! 
 * 		(이 안에 코드 써넣으면 서버 실행 시 모든 메서드가 다 실행된다!)
 * 
 * @PropertySource("경로")
 * - 지정된 경로의 properties 파일 내용을 읽어와 사용하겠다
 * - 사용할 properties 파일이 다수일 경우
 * 		해당 어노테이션을 연속해서 작성하면 된다!
 * 
 * @Bean
 * - 개발자가 수동으로 생성한 객체의 관리를 스프링에게 넘기는 어노테이션
 * 	(Bean으로 등록)
 * 
 * @ConfigurationProperties(prefix="spring.datasource.hikari")
 * - @PropertySource랑 같이 연계해서 쓰는 어노테이션이다
 * - @PropertySource로 읽어온 properties 파일의 내용 중
 * 		접두사(앞부분, prefix)가 일치하는 값만 읽어옴
 * - 읽어온 내용을 생성하려는 Bean에 자동으로 세팅해준다
 * 
 * DataSource : (Connection을 만들면서 Connection Pool을 지원하는) 객체를
 * 				참조하기 위한 Java 인터페이스 ->새로운 것이 나오면 이름만 바꾸면 된다
 * 				(DriverManager(Connection만들 때 사용하던 객체)의 대안이 DataSource!)
 * 				(DataSource는 Java JNDI 기술을 이용해서 만들어진다)
 * 
 * @Autowired 
 * - 등록된 Bean 중에서 
 * 		타입이 일치하거나 상속 관계에 있는 Bean을 
 * 		지정된 필드에 주입한다 
 * 		== 의존성 주입(DI, Dependency Injection) --IOC와 관련된 기술
 * 		== 스프링이 만들어서 필요한 곳에 객체가 들어오게 된다
 * IOC : 객체를 스프링이 만들고 관리하는 기술
 * classpath: == src/main/resources폴더를 지칭!
 * */

@Configuration
@PropertySource("classpath:/config.properties") //읽은 값 중
public class DBConfig { 
	//필드
	@Autowired //(DI, 의존성 주입)
	private ApplicationContext applicationContext; //현재 프로젝트
	//참조변수의 기본값은 null ->@Autowired 쓰면 스프링이 자동으로 만들어줌(null되지 않음)
	
	////////////HikariCP 설정 //////////////////////////
	
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.hikari")
	//이 접두사로 시작하는 것들만 읽어오겠다
	public HikariConfig hikariConfig() {//HikariConfig를 반환하겠다
		return new HikariConfig(); //하나 만들었다!
	//이 메서드의 역할 : HikariConfig 설정 객체 생성
		//이 객체는 config.properties 파일에서 읽어온
		//	spring.datasource.hikari로 시작하는 모든 값이
		//	알맞은 필드에 세팅된 상태가 된다!
	//HikariConfig CP를 이용하기 위해 HikariConfig에 필요한 설정 써있어서
	//그걸 여기에 옮겨적는다
	//근데 그걸 스프링에게 관리하도록 Bean으로 등록
	}
	
	@Bean
	public DataSource datasource(HikariConfig config) {
		//매개변수 HikariConfig config
		// -> 등록된 Bean 중 HikariConfig 타입의 Bean이 자동으로 여기에 주입된다
		//config에 DB연결 정보와 Connection Pool을 설정하는 정보 들어있음
		DataSource dataSource = new HikariDataSource(config);
		//위 메서드에서 만든 config가 이 메서드의 매개변수로 들어온다
		//설정파일을 이용해서 dataSource를 만들어서 그걸 반환해줌
		//둘이 부모자식관계(다형성 적용됨)
		return dataSource; //이 객체를 이용해서 SqlSession(MyBatis 이용가능하게 해줌)을 만들거다!
	}
	//위의 두 Bean은 DB 연결하는 HikariCP설정한거였다
	
	//이제부터는 만들어진 HikariCP에 MyBatis를 얹을거다
	
	///////////Mybatis 설정(Bean 세개 만듦!!)///////////////////
	
	@Bean
	public SqlSessionFactory sessionFactory(DataSource dataSource)throws Exception{
		//이걸 스프링이 호출 ->스프링에게 예외 던져서 스프링이 예외 처리함
		//여기서 반환되는 객체를 Bean으로 등록한다
		//공장(기계, 기계 설정 포함)을 반환하는데 그걸 Bean으로 등록
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		//공장 객체 생성
		
		sessionFactoryBean.setDataSource(dataSource); 
		
		//공장에 각종 기기,설정 세팅하기
		//mapper.xml(여기에 SQL을 작성할거임!) 파일이 모이는 경로 지정하기 
		//->Mybatis 코드 수행 시 mapper.xml을 읽을 수 있음
		//sessionFactoryBean.setMapperLocations(현재 프로젝트.resources.어떤파일);
		sessionFactoryBean.setMapperLocations(
		applicationContext.getResources("classpath:/mappers/**.xml"));
		//현재 프로젝트 안에서 특정 자원들을 얻어올건데 
		//resources폴더(classpath)에 mappers 폴더의 모든 xml을 읽어온다

	sessionFactoryBean.setTypeAliasesPackage("edu.kh.project");
	//해당 패키지 내 모든 클래스의 별칭을 등록한다
	// - Mybatis는 특정 클래스 지정 시 패키지명.클래스명을 모두 작성해야 함
	//	ex) 특정 클래스를 지칭하고 싶으면 edu.kh.todo.model.dto.Todo로 해야 Mybatis가 인식했는데
	//		->긴 이름을 짧게 부를 별칭을 설정하면 짧게 부를 수 있다! 
	// - setTypeAliasesPackage("패키지") 이요 시 
	//		클래스 파일명이 별칭으로 등록된다
	//(원본)edu.kh.todo.model.dto.Todo    -> Todo(별칭 등록) 이렇게 짧게 된다
	
	//마이바티스 설정 파일 경로를 지정한다(그거 읽어올 수 있게)
	sessionFactoryBean.setConfigLocation(
			applicationContext.getResource("classpath:mybatis-config.xml")); //설정 파일 위치(getResources랑 작성법 똑같다)
	
	//설정 내용이 모두 작성된 객체 반환
	return sessionFactoryBean.getObject(); //공장에서 상품(factory)만들어서 내보낸다
	}
	
	//SqlSessionTemplate : Connection+ DBCP(database connnection pool)+Mybatis+트랜잭션 제어처리(이거 할거면 밑의 DataSourceTransactionManager 필요)
	//DBCP(DB에 연결하기 위한 커넥션 모아놓은 것, Database Connection Pool)
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {
		//매개변수에는 바로 위에서 만든 것이 들어온다
		return new SqlSessionTemplate(factory); //factory : 공장에서 상품을 받아온 것
	}
	
	//DataSourceTransactionManager : 트랜잭션 매니저(제어처리)
	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(
			DataSource dataSource) { //커넥션 만들 때 썼던 dataSource를 이용
		return new DataSourceTransactionManager(dataSource);
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
}
