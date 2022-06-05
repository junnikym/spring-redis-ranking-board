package edu.junnikym.springredisrankingboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@SpringBootTest
public class RedisTransactionTest {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	final boolean exceptionOccurred = true;

	@Test
	void sessionTransactionTest() {
		Assertions.assertThrows(RuntimeException.class, this::sessionTransactionServiceFunc);
		Assertions.assertEquals(redisTemplate.opsForValue().get("tx_test_session"), null);
	}

	@Test
	void annotationTransactionTest() {
		Assertions.assertThrows(RuntimeException.class, this::sessionTransactionServiceFunc);
		Assertions.assertEquals(redisTemplate.opsForValue().get("tx_test_annotation"), null);
	}

	void sessionTransactionServiceFunc() {

		List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
			public List<Object> execute(RedisOperations operations) throws DataAccessException {
				operations.multi(); 		// redis transaction 시작

				operations.opsForValue().set("tx_test_session", "1");

				if (exceptionOccurred) {
					throw new RuntimeException("Exception Occurred");
				}

				return operations.exec();	// redis transaction 적용
			}
		});
	}

	@Transactional	// redis transaction 시작
	void serviceFunc() {
		redisTemplate.opsForValue().set("tx_test_annotation", "1");

		if (exceptionOccurred)
			throw new RuntimeException("exception occur");
	}

}
