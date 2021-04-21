package io.revx.api.config;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({"classpath:adxDb.properties"})
@EnableJpaRepositories(basePackages = {"io.revx.api.mysql.repo" },
    entityManagerFactoryRef = "adxEntityManager", transactionManagerRef = "adxTransactionManager")
@EnableTransactionManagement
public class AdxConfig {
  @Autowired
  Environment env;

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean adxEntityManager() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(adxDataSource());
    em.setPackagesToScan(new String[] {"io.revx.api.mysql.entity","io.revx.core.model.creative"});

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    HashMap<String, Object> properties = new HashMap<>();
    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    em.setJpaPropertyMap(properties);
    return em;
  }

  @Primary
  @Bean
  public DataSource adxDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getProperty("adx.datasource.driver-class-name"));
    dataSource.setUrl(env.getProperty("adx.datasource.url"));
    dataSource.setUsername(env.getProperty("adx.datasource.username"));
    dataSource.setPassword(env.getProperty("adx.datasource.password"));

    return dataSource;
  }

  @Primary
  @Bean
  public PlatformTransactionManager adxTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(adxEntityManager().getObject());
    return transactionManager;
  }
  
  
  
}
