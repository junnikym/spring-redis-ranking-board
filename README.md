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

## CRUD Test
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

* ref : <https://bcp0109.tistory.com/328>

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

## CRUD Test

### String

```java
@Autowired
private StringRedisTemplate redisTemplate;

private final String key = "RedisTemplateTest_String";

private final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    
    ...
    
        valueOperations.set(key, "1");

        // 다음과 같이 key에 관한 만료시간을 지정해 줄 수 있다.
        redisTemplate.expire(key, Duration.ofSeconds(10));
        
        // get 함수를 통해 key에 해당하는 value를 가져올 수 있다.
        final String item = valueOperations.get(key);
        
        // increment, decrement 함수를 통해 증가, 감소 연산을 할 수 있다.
        valueOperations.increment(key);
        valueOperations.decrement(key, 10);
        
        // 또는 set함수를 통해 key에 저장된 값을 변경 할 수도 있다.
        valueOperations.set(key, "40");
        
        redisTemplate.delete(key);

        // 다음과 같이 할 경우 value 값 조회와 동시에 item을 삭제한다.
        final String itme = valueOperations.getAndDelete(key);
```

### List

```java
@Autowired
private StringRedisTemplate redisTemplate;

private final String key = "RedisTemplateTest_List";

private final ListOperations<String, String> listOperations = redisTemplate.opsForList();

    ...

        // List에 아래 문자 추가 
        listOperations.rightPush(key, "r");
        listOperations.rightPush(key, "e");
        listOperations.rightPush(key, "d");
        listOperations.rightPush(key, "i");
        listOperations.rightPush(key, "s");

		// pushAll 함수로 여러개를 추가할 수 있다.
        listOperations.rightPushAll(key, " ", "t", "e", "m", "p", "l", "a", "t", "e");

        // 다음과 같이 key에 관한 만료시간을 지정해 줄 수 있다.
        redisTemplate.expire(key, Duration.ofSeconds(10));

		// 해당 함수로 리스트의 index에 해당하는 value를 가져올 수 있다.
        final long idxForGet = 4;
        final String nthChar = listOperations.index(key, idxForGet);
        System.out.println(idxForGet + "th character is " + nthChar);

        // size 함수를 통해 list의 사이즈를 가져올 수 있다.
        final Long size = listOperations.size(key);
        System.out.println("size is " + size);

		// range 함수는 범위를 지정하여 범위에 해당하는 list의 value를 가져올 수 있다.
        final List<String> allOfItem = listOperations.range(key, 0, -1);
        System.out.print("all of list value : ");
        for(String it : allOfItem) {
            System.out.print(it);
        }
        System.out.println();

        ...
		
        final Consumer<String> printAll = (comment)-> {
            final List<String> allOfItem = listOperations.range(key, 0, -1);
            System.out.print("all of list value - "+comment+" : ");
            for(String it : allOfItem) {
                System.out.print(it);
            }
            System.out.println();
        };
		
        // left, right 원하는 방향으로 push, pop이 가능
        listOperations.leftPush(key, " ");
        listOperations.leftPushAll(key, "s", "i", "h", "t");
        printAll.accept("after left push");

        listOperations.rightPop(key, 9);
        printAll.accept("after right pop");

		// redisTemplate의 delete 함수를 통해 key에 해당하는 값을 삭제할 수 있다.
        redisTemplate.delete(key);

}
```

위와 같이 redis의 키워드와 유사한 네이밍의 함수를 사용하여 redis에 값을 저장 또는 삭제, 조회, 수정 등.. 이 가능하며
Value, List 타입의 데이터 외에도 Set, ZSet, Hash 등.. 의 타입도 Operations를 통해 Redis의 데이터를 조작할 수 있다.
자세한 사항은 [Spring Docs](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/HashOperations.html) 를 참고 
(해당 링크는 Hash 타입에 관한 Docs)

* ref : <https://sabarada.tistory.com/105>

---

## Redis Pub/Sub

Redis의 <code>Subscribe</code>, <code>Unsubscribe</code>, <code>Publish</code>는 메시지 페러다임을 구현한 기능이다.
<code>Sender</code><sub>Publisher</sub>는 <code>Receiver</code><sub>Subscriber</sub>에게 값을 전달하는게 아닌 
해당 채널에 메시지를 전달하면 그 메시지를 구독하고 있는 <code>Receiver</code><sub>Subscriber</sub>에게 메시지를 전송한다.

### Channel, Brocker and Topic

<code>Publisher</code>가 <code>Message</code>를 보내면 해당 <code>Message</code>는 <code>Broker</code>에게 전달된다.
여기서 <code>Broker</code>는 <code>Channel</code>이라고도 불린다.<br/> 
이때, <code>Message</code>는 <code>Topic</code>이라는 것을 사용하여 누구의 메시지인지 구분한다. 

<code>Subscriber</code>는 <code>Broker</code>를 통해 <code>Topic</code>으로 구분된 <code>Message</code>를 가져감으로써
최종적으로 <code>Message</code>를 받아갈 수 있다.

![pub/sub and brocker, topic](./img/brokerAndTopic.png)

### Pros & Cons

이러한 Pub/Sub messaging pattern은 <code>느리다</code>라는 단점이 있지만 반면 <code>안전하다</code>라는 장점이 있다.

여기서 <code>Broker</code><sub><code>Channel</code></sub>(이)가 없다면 그만큼 중간 단계가 하나 없어지기 때문에 당연히 빨라질 수 밖에 없다.

하지만, <code>Subscriber</code>에 장애가 생겨 <code>Message</code>를 받을 수 없는 상황에 놓인다면 
<code>Publisher</code>는 계속 하염없이 <code>Subscriber</code>를 가다릴수는 없다. <br/>
그렇기 때문에 <code>Broker</code><sub><code>Channel</code></sub>, <code>Topic</code>를 사용하여 이러한 상황을 방지하고 안정적으로 메시지를 보내는 방법이 등장하였다.

* ref : <https://sugerent.tistory.com/585>