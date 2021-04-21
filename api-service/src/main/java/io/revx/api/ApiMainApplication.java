
package io.revx.api;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.revx.api.audit.AuditServiceListener;
import io.revx.api.service.strategy.StrategyService;
import io.revx.core.RestTemplateErrorHandler;
import io.revx.core.event.EventBusManager;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.response.UserInfo;



@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableEncryptableProperties
@EnableScheduling
@Configuration
@ComponentScan({"io.revx.api", "io.revx.core"})
public class ApiMainApplication implements CommandLineRunner {
  static Logger logger = LogManager.getLogger(ApiMainApplication.class);


  @Autowired  
  public RestTemplateErrorHandler errorHandler;

  @Autowired
  PrintAllSigtonService service;

  @Autowired
  StrategyService StrategyService;

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(ApiMainApplication.class, args);
    EventBusManager.eventBus.register(new AuditServiceListener());
    logger.debug(" Loaded All the context and Have done the Event Bus Subscribed");
    // context.getBean(ApiMainApplication.class).runSome();
  }

  public void runSomeThingTest() {
    // service.runSomeThing()

    UserInfo ui = new UserInfo();
    ui.setUserId(123l);
    List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
    list.add(new SimpleGrantedAuthority("ADMIN"));
    UsernamePasswordAuthenticationToken upa = new UsernamePasswordAuthenticationToken(ui, "", list);
    SecurityContextHolder.getContext().setAuthentication(upa);
    logger.debug("  StrategyService {} ", StrategyService);
    for (int i = 0; i < 10; i++) {
      logger.debug("  Testing Audit Events ");
      try {
        Thread.sleep(10000);
        StrategyDTO old = new StrategyDTO();
        old.setId(1234l);
        old.setName("My Old Name Of strategy " + i);
        StrategyDTO newObj = new StrategyDTO();
        newObj.setName("My New Name Of strategy " + i);
        newObj.setId(1234l);
        StrategyService.postAuditEvent(old, newObj);
        logger.debug("  Testing Audit Events ");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }


  }


  @Override
  public void run(String... args) {}


  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy("ROLE_SADMIN > ROLE_ADMIN > ROLE_RW > ROLE_RO > ROLE_DEMO");
    return roleHierarchy;
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate rt = new RestTemplate();
    rt.setErrorHandler(errorHandler);
    return rt;
  }
}
