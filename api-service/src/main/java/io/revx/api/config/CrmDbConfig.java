package io.revx.api.config;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({"classpath:crmdb.properties"})
@EnableJpaRepositories(basePackages = {"io.revx.api.mysql.crmdb.repo"},
    entityManagerFactoryRef = "crmDbEntityManager", transactionManagerRef = "crmDbTransactionManager")
public class CrmDbConfig {
  @Autowired
  Environment env;

  @Bean
  public LocalContainerEntityManagerFactoryBean crmDbEntityManager() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(crmDbDataSource());
    em.setPackagesToScan(new String[] {"io.revx.api.mysql.crmdb.entity"});
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    HashMap<String, Object> properties = new HashMap<>();
    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    em.setJpaPropertyMap(properties);
    return em;
  }

  @Bean
  public DataSource crmDbDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getProperty("crmdb.datasource.driver-class-name"));
    dataSource.setUrl(env.getProperty("crmdb.datasource.url"));
    dataSource.setUsername(env.getProperty("crmdb.datasource.username"));
    dataSource.setPassword(env.getProperty("crmdb.datasource.password"));
    return dataSource;
  }
  
  @Bean
  public PlatformTransactionManager crmDbTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(crmDbEntityManager().getObject());
    return transactionManager;
  }
}
