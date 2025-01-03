package app.egs.shop.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfo(
            "EGS",
            "EGS SHOPPING REST API.",
            "1.0",
            "Terms of service",
            "Ebrahim Khosravani",
            "Apache License Version 2.0",
            "https://www.apache.org/licenses/LICENSE-2.0");
    }
}
