package com.pick_me_not.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	public static final String PARTICIPANT_TOKEN_SCHEME = "participantToken";

	@Bean
	public OpenAPI pickMeNotOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Pick Me Not API")
						.description("Pick Me Not 실시간 룰렛 백엔드 API")
						.version("v1"))
				.components(new Components()
						.addSecuritySchemes(PARTICIPANT_TOKEN_SCHEME, new SecurityScheme()
								.type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.HEADER)
								.name("X-Participant-Token")));
	}
}
