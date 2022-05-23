# Spring & Redis - Ranking Board

## Redis on Java
java에서 사용하는 Redis Client는 크게 <code>Lettuce</code>와 <code>Jedis</code>이 있다. 

<code>Jedis</code>는 예전부터 Java의 표준 Redis Client로 많이 사용되어 왔다. 
하지만 <code>Lettuce</code>는 Netty 기반이기에 비동기로 요청을 처리하기 때문에 <code>Jedis</code>보다 고성능이며
멀티 쓰레드 불안정, Pool 한계 등.. 으로 인해 최근에는 <code>Jedis</code>보다 <code>Lettuce</code>를 선호하는 편이다.

이 외에도 잘 만들어진 문서, 깔끔하게 디자인된 코드 등.. 으로 인한 이유도 있다.

*ref : <https://jojoldu.tistory.com/418>

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
  * <code>Jedis</code>를 사용하고 싶다면 [여기](https://jojoldu.tistory.com/418) 의 1-1 부분의 코드를 참고.

