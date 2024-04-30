package com.kh.test.common.config;

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

@Configuration
@PropertySource("classpath:/config.properties")
public class DBConfig {
	
	@Autowired // (DI, 의존성 주입)
	private ApplicationContext applicationContext; // 현재 프로젝트
	
	/////////// HikariCP 설정 /////////////
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariConfig hikariConfig() {
		return new HikariConfig(); 
	}
	
	@Bean
	public DataSource dataSource(HikariConfig config) {
		
		DataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}
	
	
	///////////  Mybatis 설정 /////////////
	@Bean
	public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception{
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		
		sessionFactoryBean.setDataSource(dataSource);
		
		sessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mappers/**.xml"));
		
		sessionFactoryBean.setTypeAliasesPackage("com.kh.test");
		
		sessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
		
		return sessionFactoryBean.getObject();
	}
	
	
	// DBCP(DataBase Connection Pool)
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {
		return new SqlSessionTemplate(factory);
	}
	
	// DataSourceTransactionManager : 트랜잭션 매니저(제어 처리)
	@Bean
	public DataSourceTransactionManager 
		dataSourceTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
}
