package edu.junnikym.springredisrankingboard.user.config.redis;

import edu.junnikym.springredisrankingboard.user.service.ScoreProjectionSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RankMessageSubcriberConfig {

	@Bean
	RedisMessageListenerContainer rankRedisContainer(
			RedisConnectionFactory redisConnectionFactory,
			ScoreProjectionSubscriber scoreProjectionSubscriber,
			ChannelTopic rankTopic
	) {
		final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(scoreProjectionSubscriber);
		final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(messageListenerAdapter, rankTopic);
		return container;
	}

	@Bean
	ChannelTopic rankTopic() {
		return new ChannelTopic("rank-event");
	}

}
