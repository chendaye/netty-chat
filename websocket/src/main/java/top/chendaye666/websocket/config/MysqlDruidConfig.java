package top.chendaye666.websocket.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Druid官方已提供启动器了，我们只要使用启动器，就能实现springboot的自动装配，就不需要再通过配置类去注入我们的bean了。
 * 注解代替xml
 * springboot 注解方式整合 mybatis
 */
@Configuration
@MapperScan(basePackages = MysqlDruidConfig.PACKAGE, sqlSessionFactoryRef = "mysqlSqlSessionFactory")
public class MysqlDruidConfig {
    // 精确到 dao 目录，以便跟其他数据源隔离
    static final String PACKAGE = "top.chendaye666.websocket.dao";
    static final String MAPPER_LOCATION = "classpath:mappers/*.xml";
    static final String CONFIG_LOCATION = "classpath:mybatis-config.xml";

    @Value("${spring.datasource.druid.url}")
    private String url;

    @Value("${spring.datasource.druid.username}")
    private String user;

    @Value("${spring.datasource.druid.password}")
    private String password;

    @Value("${spring.datasource.druid.driverClassName}")
    private String driverClass;

    /**
     * 创建数据源，并设置属性
     *
     * @return
     */
    @Bean(name = "mysqlDataSource")
    @Qualifier("mysqlDataSource")
    @Primary
    public DataSource mysqlDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setInitialSize(10);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(30);
        dataSource.setMaxWait(30000L);
        dataSource.setTimeBetweenEvictionRunsMillis(60000L);
        dataSource.setMinEvictableIdleTimeMillis(300000L);
        //dataSource.setValidationQuery("select 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        try {
            dataSource.setFilters("stat, wall");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    /**
     * 创建数据源事务管理的bean
     *
     * @return
     */
    @Bean(name = "mysqlTransactionManager")
    @Qualifier("mysqlTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(mysqlDataSource());
    }

    /**
     * 整合 mybatis 和 druid
     * 创建sqlSessionFactory bean (mybatis)
     *
     * @param mysqlDataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "mysqlSqlSessionFactory")
    @Qualifier("mysqlSqlSessionFactory")
    @Primary
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource mysqlDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        // datasource
        sessionFactory.setDataSource(mysqlDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MysqlDruidConfig.MAPPER_LOCATION));
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            sessionFactory.setConfigLocation(resolver.getResource(MysqlDruidConfig.CONFIG_LOCATION));
            // Springboot整合mybatis，配置驼峰命名转换
            sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
            return sessionFactory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}