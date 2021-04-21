package io.revx.auth.config;

import java.util.ArrayList;
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

    ParameterBuilder aParameterBuilder = new ParameterBuilder();
    aParameterBuilder.name("token").modelRef(new ModelRef("string")).parameterType("header")
        .defaultValue("eyJzdWIiOiJTQWRtaW4iLCJzY29wZXMiOiJST0xFX0FET").required(true).build();
    java.util.List<Parameter> aParameters = new ArrayList<>();
    aParameters.add(aParameterBuilder.build());
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage("io.revx.auth.controller"))
        .paths(PathSelectors.any()).build();// .globalOperationParameters(aParameters);
  }
}
