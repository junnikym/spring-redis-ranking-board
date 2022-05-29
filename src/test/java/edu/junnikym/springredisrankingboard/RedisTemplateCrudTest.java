package edu.junnikym.springredisrankingboard;

import edu.junnikym.springredisrankingboard.user.repository.UserRedisRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@SpringBootTest
public class RedisTemplateCrudTest {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Nested
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
	class String_테스트 {

		final String key = "RedisTemplateTest_String";

		final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		@Test
		@Order(1)
		void create_테스트() {
			valueOperations.set(key, "1");

			// 다음과 같이 key에 관한 만료시간을 지정해 줄 수 있다.
			redisTemplate.expire(key, Duration.ofSeconds(10));
		}

		@Test
		@Order(2)
		void read_테스트() {
			final String item = valueOperations.get(key);
			System.out.println(key+"'s value is "+item);
		}

		@Test
		@Order(3)
		void update_테스트() {
			valueOperations.increment(key);
			final String afterIncItem = valueOperations.get(key);

			System.out.println("after increment : " + afterIncItem);

			valueOperations.decrement(key, 10);
			final String afterDecItem = valueOperations.get(key);

			System.out.println("after decrement : " + afterDecItem);

			valueOperations.set(key, "40");
			final String afterSetItem = valueOperations.get(key);

			System.out.println("after set : " + afterSetItem);
		}

		@Test
		@Order(4)
		void delete_테스트() {
			redisTemplate.delete(key);

			// 다음과 같이 할 경우 value 값 조회와 동시에 item을 삭제한다.
			// final String itme = valueOperations.getAndDelete(key);
		}

	}

	@Nested
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
	class list_테스트 {

		private final String key = "RedisTemplateTest_List";

		private final ListOperations<String, String> listOperations = redisTemplate.opsForList();

		@Test
		@Order(1)
		void create_테스트() {
			listOperations.rightPush(key, "r");
			listOperations.rightPush(key, "e");
			listOperations.rightPush(key, "d");
			listOperations.rightPush(key, "i");
			listOperations.rightPush(key, "s");

			listOperations.rightPushAll(key, " ", "t", "e", "m", "p", "l", "a", "t", "e");

			// 다음과 같이 key에 관한 만료시간을 지정해 줄 수 있다.
			redisTemplate.expire(key, Duration.ofSeconds(10));
		}

		@Test
		@Order(2)
		void read_테스트() {
			final long idxForGet = 4;
			final String nthChar = listOperations.index(key, idxForGet);
			System.out.println(idxForGet + "th character is " + nthChar);

			final Long size = listOperations.size(key);
			System.out.println("size is " + size);

			final List<String> allOfItem = listOperations.range(key, 0, -1);
			System.out.print("all of list value : ");
			for(String it : allOfItem) {
				System.out.print(it);
			}
			System.out.println();
		}

		@Test
		@Order(3)
		void update_테스트() {

			final Consumer<String> printAll = (comment)-> {
				final List<String> allOfItem = listOperations.range(key, 0, -1);
				System.out.print("all of list value - "+comment+" : ");
				for(String it : allOfItem) {
					System.out.print(it);
				}
				System.out.println();
			};

			listOperations.leftPush(key, " ");
			listOperations.leftPushAll(key, "s", "i", "h", "t");
			printAll.accept("after left push");

			listOperations.rightPop(key, 9);
			printAll.accept("after right pop");
		}

		@Test
		@Order(4)
		void delete_테스트() {
			redisTemplate.delete(key);
		}

	}

}
