package com.vortex.cloud.ums.config.ds2;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;

@Configuration
public class DruidDBConfig2 {
	private static Logger logger = LoggerFactory.getLogger(DruidDBConfig2.class);

	@Value("${spring.datasource2.url}")
	private String dbUrl;

	@Value("${spring.datasource2.username}")
	private String username;

	@Value("${spring.datasource2.password}")
	private String password;

	@Value("${spring.datasource2.driverClassName}")
	private String driverClassName;

	@Value("${spring.datasource2.initialSize}")
	private int initialSize;

	@Value("${spring.datasource2.minIdle}")
	private int minIdle;

	@Value("${spring.datasource2.maxActive}")
	private int maxActive;

	@Value("${spring.datasource2.maxWait}")
	private int maxWait;

	@Value("${spring.datasource2.timeBetweenEvictionRunsMillis}")
	private int timeBetweenEvictionRunsMillis;

	@Value("${spring.datasource2.minEvictableIdleTimeMillis}")
	private int minEvictableIdleTimeMillis;

	@Value("${spring.datasource2.validationQuery}")
	private String validationQuery;

	@Value("${spring.datasource2.testWhileIdle}")
	private boolean testWhileIdle;

	@Value("${spring.datasource2.testOnBorrow}")
	private boolean testOnBorrow;

	@Value("${spring.datasource2.testOnReturn}")
	private boolean testOnReturn;

	@Value("${spring.datasource2.poolPreparedStatements}")
	private boolean poolPreparedStatements;

	@Value("${spring.datasource2.maxPoolPreparedStatementPerConnectionSize}")
	private int maxPoolPreparedStatementPerConnectionSize;

	@Value("${spring.datasource2.filters}")
	private String filters;

	@Value("{spring.datasource2.connectionProperties}")
	private String connectionProperties;

	@Bean(name = "dataSource2") // 声明其为Bean实例
	public DataSource dataSource2() {

		DruidDataSource datasource = new DruidDataSource();

		datasource.setUrl(dbUrl);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setDriverClassName(driverClassName);

		// configuration
		datasource.setInitialSize(initialSize);
		datasource.setMinIdle(minIdle);
		datasource.setMaxActive(maxActive);
		datasource.setMaxWait(maxWait);
		datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		datasource.setValidationQuery(validationQuery);
		datasource.setTestWhileIdle(testWhileIdle);
		datasource.setTestOnBorrow(testOnBorrow);
		datasource.setTestOnReturn(testOnReturn);
		datasource.setPoolPreparedStatements(poolPreparedStatements);
		datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		try {
			datasource.setFilters(filters);
		} catch (SQLException e) {
			logger.error("druid configuration initialization filter", e);
		}
		datasource.setConnectionProperties(connectionProperties);
		return new DataSourceSpy(datasource);
	}
}
