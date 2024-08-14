package br.com.gmsoft.userjwe.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    @Configuration
    public class OpenApiConfig {

        @Bean
        public OpenAPI infoOpenAPI() {
            return new OpenAPI()
                    .info(new Info().title("User Registration and Authentication REST API")
                            .description("A project example that uses Spring Security, JWE, TDD with JUnit and Spring MockMVC")
                            .version("1.0")
                            .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                            .contact(new Contact().name("Gabriel Manzoni")
                                    .url("http://www.github.com/gabrielmzoni")
                                    .email("gabrielmzoni@gmail.com")));

        }

        @Bean
        public GroupedOpenApi publicApi() {
            return GroupedOpenApi.builder()
                    .group("public")
                    .pathsToMatch("/**")
                    .build();
        }
    }

