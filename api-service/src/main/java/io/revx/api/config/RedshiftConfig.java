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
@PropertySource({"classpath:redshift.properties"})
@EnableJpaRepositories(basePackages = "io.revx.api.redshift.repo",
    entityManagerFactoryRef = "redshiftEntityManager",
    transactionManagerRef = "redshiftTransactionManager")
public class RedshiftConfig {
  @Autowired
  Environment env;

  @Bean
  public LocalContainerEntityManagerFactoryBean redshiftEntityManager() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(redshiftDataSource());
    em.setPackagesToScan(new String[] {"io.revx.api.redshift.entity"});

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    HashMap<String, Object> properties = new HashMap<>();

    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    em.setJpaPropertyMap(properties);
    return em;
  }

  @Bean
  public DataSource redshiftDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getProperty("redshift.datasource.driver-class-name"));
    dataSource.setUrl(env.getProperty("redshift.datasource.url"));
    dataSource.setUsername(env.getProperty("redshift.datasource.username"));
    dataSource.setPassword(env.getProperty("redshift.datasource.password"));

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager redshiftTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(redshiftEntityManager().getObject());
    return transactionManager;
  }
}
