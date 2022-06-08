package edu.junnikym.springredisrankingboard.config;

import edu.junnikym.springredisrankingboard.redis.service.RedisMessageSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement    // Transaction 활성화
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {

		// RedisTemplate 객체 생성
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();

		// Connection 생성 및 등록
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		// Transaction 활성화
		redisTemplate.setEnableTransactionSupport(true);

		return redisTemplate;
	}

	// Transaction 활성화를 위한 PlatformTransactionManager baens
	@Bean
	public PlatformTransactionManager transactionManager() throws SQLException {
//		return new DataSourceTransactionManager(dataSource());
		return new JpaTransactionManager();
	}

//	@Bean
//	public DataSource dataSource() throws SQLException {
//		return DataSourceBuilder.create().build();
//	}

	@Bean
	MessageListenerAdapter messageListener(RedisMessageSubscriber redisMessageSubscriber) {
		return new MessageListenerAdapter(redisMessageSubscriber);
	}

	@Bean
	RedisMessageListenerContainer redisContainer(
			RedisConnectionFactory redisConnectionFactory,
			MessageListenerAdapter messageListenerAdapter,
			ChannelTopic topic
	) {
		final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(messageListenerAdapter, topic);
		return container;
	}

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("event");
	}

}
