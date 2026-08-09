package com.pick_me_not.common.config;

import com.pick_me_not.common.websocket.WebSocketChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private static final long[] HEARTBEAT_INTERVAL = {10_000, 10_000};

	private final WebSocketChannelInterceptor webSocketChannelInterceptor;
	private final WebSocketProperties webSocketProperties;
	private final ThreadPoolTaskScheduler webSocketTaskScheduler;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic")
				.setHeartbeatValue(HEARTBEAT_INTERVAL)
				.setTaskScheduler(webSocketTaskScheduler);
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(webSocketChannelInterceptor);
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOriginPatterns(webSocketProperties.getAllowedOriginPatterns().toArray(String[]::new));
	}
}
