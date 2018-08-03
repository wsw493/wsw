//package com.personal.cloud.resdis.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//@EnabnableSwagger2
//public class Swagger2Config {
//	@Bean
//	public Docket createRestApi() {
//		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
//				.apis(RequestHandlerSelectors.basePackage("com.vortex.cloud.zt.controller")).paths(PathSelectors.any()).build();
//	}
//
//	private ApiInfo apiInfo() {
//		return new ApiInfoBuilder().title("西安渣土APIs")
//				.description("SpringBoot后端接口")
//				.contact("Vortex").version("1.0").build();
//	}
//}