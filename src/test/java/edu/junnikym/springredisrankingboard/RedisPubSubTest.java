package edu.junnikym.springredisrankingboard;

import edu.junnikym.springredisrankingboard.redis.service.RedisMessagePublisher;
import edu.junnikym.springredisrankingboard.user.entity.User;
import edu.junnikym.springredisrankingboard.user.repository.UserRedisRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisPubSubTest {

	@Autowired
	private RedisMessagePublisher<String> redisMessagePublisher;

	@Test
	void pubSubTest() {
		redisMessagePublisher.publish("published message");
	}
}
