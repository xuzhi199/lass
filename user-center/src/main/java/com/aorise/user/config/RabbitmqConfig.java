package com.aorise.user.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aorise.model.user.constants.UserCenterMq;

/**
 * rabbitmq配置
 * 
 * @author blues199
 *
 */
@Configuration
public class RabbitmqConfig {

	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(UserCenterMq.MQ_EXCHANGE_USER);
	}
}
