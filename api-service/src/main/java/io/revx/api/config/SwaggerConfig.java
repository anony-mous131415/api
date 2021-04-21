package io.revx.api.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
  @Bean
  public Docket api() {
    List<Parameter> operationParameters = new ArrayList<Parameter>();
    Parameter p = new ParameterBuilder().name("token").description("Auth Token")
        .modelRef(new ModelRef("string")).parameterType("header").required(false)
        .defaultValue("gd.dgaf.geur4364").build();
    Parameter reqIdParam = new ParameterBuilder().name("reqId").description("request id")
        .modelRef(new ModelRef("string")).parameterType("header").required(false)
        .defaultValue("uweobtlfim94d9jxov0w").build();
    operationParameters.add(p);
    operationParameters.add(reqIdParam);
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage("io.revx.api.controller"))
        .paths(PathSelectors.any()).build().globalOperationParameters(operationParameters);
  }
}
