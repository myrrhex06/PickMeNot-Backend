package com.pick_me_not.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class WebSocketTaskSchedulerConfig {

	@Bean
	public ThreadPoolTaskScheduler webSocketTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadNamePrefix("websocket-heartbeat-");
		scheduler.setRemoveOnCancelPolicy(true);
		return scheduler;
	}
}
