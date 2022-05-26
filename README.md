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

---

## Redis Template

<code>Redis Template</code>를 사용하면 
<code>Redis Repository</code>와 달리 Entity 외 <code>String</code>, <code>List</code>, <code>Set</code> 등... 
원하는 타입의 값을 넣을 수 있다.

우선, <code>Redis Template</code>를 사용하기 위해 Configuration Class에 다음의 Bean를 선언해주어야한다.
```java
@Configuration
public class RedisConfig {
	
    ... 생략
	
	@Bean
	public RedisTemplate<?, ?> redisTemplate() {

		// RedisTemplate 객체 생성
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();

		// Connection 생성 및 등록
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		
		return redisTemplate;
	}
}
```

### Operations For ...

<code>ValueOperation</code>, <code>ListOperation</code>, <code>SetOperation</code> 등... 
..Operations Interface를 사용하여 데이터를 Serialize 또는 Deserialize 할 수 있다.

Operations Interface는 <code>Redis Template</code>의 opsFor.. Method를 통해 객체를 가져올 수 있다. 

( <code>opsForValue</code>, <code>opsForList</code>, <code>opsForSet</code>, ... )

```java

@Autowired
SpringRedisTemplate redisTemplate;

        ...

final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		
valueOperations.set(key, val);  // val를 key에 저장
valueOperations.get(key);       // key에 해당하는 값을 불러옴   

```

### Redis Template & Spring Redis Template

Redis Template 에는 <code>RedisTemplate</code>과 <code>SpringRedisTemplate</code>이 있다.

둘의 차이는 Serialize를 할 때 사용하는 Serializer가 다르다.
<code>RedisTemplate</code>은 <code>JdkSerializationRedisSerializer</code>를 사용하는 반면,
<code>SpringRedisTemplate</code>는 <code>StringRedisSerializer</code>를 사용한다.

두 Serializer의 차이는 <code>JdkSerializationRedisSerializer</code>는 자바 클래스와 필드정보가 부가적으로 저장되는 반면,
<code>StringRedisSerializer</code>는 그러한 정보를 따로 붙이지 않고 Redis에 저장된다.

만약, <code>JdkSerializationRedisSerializer</code>로 직렬화하여 Redis에 올라갔다면 클래스, 필드 정보가 부가적으로 붙었기 때문에
<code>redis-cli</code>에서 값을 확인하기가 어렵다. 따라서 <code>redis-cli</code>에서 값을 확인하기 위해서는
<code>StringRedisSerializer</code>를 사용하거나 다른 Serializer를 사용해야한다.

반면, <code>StringRedisSerializer</code>를 사용할 경우 <code>ValueOperations</code>를 사용해서 Entity를 Redis에 올릴 수 있지만
<code>StringRedisSerializer</code>를 사용한다면 에러가 발생한다.

위와 같이 <code>ValueOperations</code>를 통해 Entity를 저장해야하는 상황이 발생한다면
<code>JdkSerializationRedisSerializer</code>를 사용하거나 <code>JacksonJsonRedisSerializer</code>와 같은 다른 Serializer를 사용해야 한다.

#### Set Serializer
```java
redisTemplate.setKeySerializer(new StringRedisSerializer());
redisTemplate.setValueSerializer(new StringRedisSerializer());
```
다음과 같이 Redis Template의 <code>set...Serializer</code> Method를 사용하여 Serializer를 따로 지정해 줄 수 있다.