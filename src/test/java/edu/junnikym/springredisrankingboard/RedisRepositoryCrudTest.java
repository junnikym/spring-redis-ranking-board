package edu.junnikym.springredisrankingboard;

import edu.junnikym.springredisrankingboard.user.entity.User;
import edu.junnikym.springredisrankingboard.user.repository.UserRedisRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class RedisRepositoryCrudTest {

	@Autowired
	private UserRedisRepository userRedisRepository;

	@Nested
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
	class 단순_기능_테스트 {

		private User user;

		@BeforeAll
		void init() {
			this.user = new User("junnikym");
		}

		@Test
		@Order(1)
		void create_테스트 () {
			userRedisRepository.save(this.user);
		}

		@Test
		@Order(2)
		void read_테스트 () {
			final User result = userRedisRepository
					.findById(this.user.getId())
					.orElseThrow(()-> new RuntimeException("Can not find user"));

			System.out.println ("found "+result.getName()+" user");
			System.out.println (" and id is "+result.getId());
		}

		@Test
		@Order(3)
		void update_테스트 () {
			user.setName("modified name");
			userRedisRepository.save(user);

			final User result = userRedisRepository
					.findById(this.user.getId())
					.orElseThrow(()-> new RuntimeException("Can not find user"));

			System.out.println ("update "+result.getName()+" user");
		}

		@Test
		@Order(3)
		void delete_테스트 () {
			userRedisRepository.delete(user);

			Assertions.assertThrows(RuntimeException.class, ()-> {
				userRedisRepository
						.findById(this.user.getId())
						.orElseThrow(() -> new RuntimeException("Can not find user"));
			});
		}

	}
}
