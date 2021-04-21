package io.revx.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.client.RestTemplate;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableEncryptableProperties
@Configuration
@ComponentScan({"io.revx.auth", "io.revx.core"})
public class AuthServiceMainApplication implements CommandLineRunner {

  private static Logger logger = LogManager.getLogger(AuthServiceMainApplication.class);



  public static void main(String[] args) {
    logger.info("Running Main Application:");
    SpringApplication.run(AuthServiceMainApplication.class, args);
  }

  @Override
  public void run(String... args) {
   

  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy("ROLE_SADMIN > ROLE_ADMIN > ROLE_RW > ROLE_RO > ROLE_DEMO");
    return roleHierarchy;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
