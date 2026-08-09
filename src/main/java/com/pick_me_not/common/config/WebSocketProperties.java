package com.pick_me_not.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.websocket")
public class WebSocketProperties {

	private List<String> allowedOriginPatterns = List.of(
			"http://localhost:*",
			"http://127.0.0.1:*");
}
