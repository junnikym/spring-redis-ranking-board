# Spring & Redis - Ranking Board

## Redis on Java
java에서 사용하는 Redis Client는 크게 <code>Lettuce</code>와 <code>Jedis</code>이 있다. 

<code>Jedis</code>는 예전부터 Java의 표준 Redis Client로 많이 사용되어 왔다. 
하지만 <code>Lettuce</code>는 Netty 기반이기에 비동기로 요청을 처리하기 때문에 <code>Jedis</code>보다 고성능이며
멀티 쓰레드 불안정, Pool 한계 등.. 으로 인해 최근에는 <code>Jedis</code>보다 <code>Lettuce</code>를 선호하는 편이다.

이 외에도 잘 만들어진 문서, 깔끔하게 디자인된 코드 등.. 으로 인한 이유도 있다.

*ref : <https://jojoldu.tistory.com/418>

---

## Using Redis on Spring Boot

< Dependency >
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

< Property >
```yaml
spring:
    redis:
        host: (host name)
        port: (port number)
```

< Configuration Class >
```java
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }
}
```

* <code>redisConnectionFactory</code> Function : Redis Connection을 생성; 위에서는 <code>Lettuce</code>를 사용.
  *<code>Jedis</code>를 사용하고 싶다면 [여기](https://jojoldu.tistory.com/418) 의 1-1 부분의 코드를 참고.

---

## Redis Repository
Spring Data Redis의 Redis Repository를 사용하면 손쉽게 Domain Entity를 Redis Hash로 만들 수 있다.

*단 Redis Repository는 Transaction을 지원하지 않는다.

### Entity
```java
@RedisHash(value = "user", timeToLive = 30)     // timeToLive : 초 단위
public class User {
	
    @Id
    private UUID id;
	
    ... 생략
  
}
```

*ref : https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisHash.html

### Repository
```java
public interface UserRedisRepository extends CrudRepository<User, UUID> { }
```

---

## Insert Test
```java
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
        void ..._테스트 () {
            
            // Create
            userRedisRepository.save(this.user);
        
            // Read
            final User result = userRedisRepository
                    .findById(this.user.getId())
                    .orElseThrow(()-> new RuntimeException("Can not find user"));
        
            ... 생략
            
            // Update
            user.setName("modified name");
            userRedisRepository.save(user);
            
            ... 생략
                    
            // Delete
            userRedisRepository.delete(user);
            
        }
    
    }
}
```