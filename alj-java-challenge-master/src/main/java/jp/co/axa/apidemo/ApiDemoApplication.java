package jp.co.axa.apidemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** Added annotations for enabling caching and spring security */
@EnableSwagger2
@SpringBootApplication
@EnableCaching
@EnableWebSecurity
public class ApiDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDemoApplication.class, args);
	}	
	
	/* Added below logic to hide basic error controller from swagger ui page */
	@Bean
	public Docket api() {
	  return new Docket(DocumentationType.SWAGGER_2)
			// @formatter:off
	      .select()
	      .apis(RequestHandlerSelectors.any())
	      .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
	      .paths(PathSelectors.any())
	      .build();
	  		// @formatter:off
	}		
	
}
