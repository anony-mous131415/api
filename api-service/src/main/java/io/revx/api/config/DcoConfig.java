package io.revx.api.config;

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

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@PropertySource({"classpath:dcoDb.properties"})
@EnableJpaRepositories(basePackages = {"io.revx.api.mysql.dco.repo"},
    entityManagerFactoryRef = "dcoEntityManager", transactionManagerRef = "dcoTransactionManager")
public class DcoConfig {
  @Autowired
  Environment env;

  @Bean
  public LocalContainerEntityManagerFactoryBean dcoEntityManager() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dcoDataSource());
    em.setPackagesToScan(new String[] {"io.revx.api.mysql.dco.entity"});
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    HashMap<String, Object> properties = new HashMap<>();
    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    em.setJpaPropertyMap(properties);
    return em;
  }

  @Bean
  public DataSource dcoDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getProperty("dco.datasource.driver-class-name"));
    dataSource.setUrl(env.getProperty("dco.datasource.url"));
    dataSource.setUsername(env.getProperty("dco.datasource.username"));
    dataSource.setPassword(env.getProperty("dco.datasource.pass-secret"));
    return dataSource;
  }
  
  @Bean
  public PlatformTransactionManager dcoTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(dcoEntityManager().getObject());
    return transactionManager;
  }
}
