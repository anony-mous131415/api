package io.revx.api.config;

import java.util.HashMap;
import javax.persistence.PersistenceContext;
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
@PropertySource({"classpath:postgres.properties"})
@EnableJpaRepositories(basePackages = "io.revx.api.postgres.repo",
    entityManagerFactoryRef = "postgresEntityManager",
    transactionManagerRef = "postgresTransactionManager")
public class PostgresConfig {
  @Autowired
  Environment env;

  @PersistenceContext(unitName = "postgresEntityManager")
  @Bean(name = "postgresEntityManager")
  public LocalContainerEntityManagerFactoryBean postgresEntityManager() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(postgresDataSource());
    em.setPackagesToScan(new String[] {"io.revx.api.postgres.entity"});

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    HashMap<String, Object> properties = new HashMap<>();

    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    em.setJpaPropertyMap(properties);
    return em;
  }

  @Bean
  public DataSource postgresDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getProperty("postgres.datasource.driver-class-name"));
    dataSource.setUrl(env.getProperty("postgres.datasource.url"));
    dataSource.setUsername(env.getProperty("postgres.datasource.username"));
    dataSource.setPassword(env.getProperty("postgres.datasource.password"));

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager postgresTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(postgresEntityManager().getObject());
    return transactionManager;
  }
}
