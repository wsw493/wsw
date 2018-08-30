package com.vortex.cloud.ums.config.ds2;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class HibernateConfig2 {
	@Value("${hibernate.hbm2ddl.auto}")
	private String hbm2ddl;
	@Value("${hibernate.dialect}")
	private String dialect;
	@Value("${hibernate.show_sql}")
	private String showSql;
	@Value("${hibernate.format_sql}")
	private String formatSql;
	@Value("${hibernate.current_session_context_class}")
	private String sessionContextClass;
	@Value("${hibernate.packagesToScan}")
	private String packagesToScan;

	// 其中 dataSource 框架会自动为我们注入
	@Bean(name = "transactionManager2")
	public HibernateTransactionManager transactionManager2(@Qualifier("sessionFactory2") SessionFactory sessionFactory) {
		return new HibernateTransactionManager(sessionFactory);
	}

	@Bean(name = "sessionFactory2")
	public LocalSessionFactoryBean sessionFactory2(@Qualifier("dataSource2") DataSource dataSource) {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setHibernateProperties(buildHibernateProperties());
		sessionFactory.setPackagesToScan(packagesToScan);
		return sessionFactory;
	}

	@Bean("jdbcTemplate2")
	public JdbcTemplate jdbcTemplate2(@Qualifier("dataSource2") DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate;
	}

	protected Properties buildHibernateProperties() {
		Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty("hibernate.dialect", dialect);
		hibernateProperties.setProperty("hibernate.show_sql", showSql);
		hibernateProperties.setProperty("hibernate.format_sql", formatSql);
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
		hibernateProperties.setProperty("hibernate.current_session_context_class", sessionContextClass);

		return hibernateProperties;
	}

}
