package edu.junnikym.springredisrankingboard.user.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.UUID;

@RedisHash (value="user", timeToLive=30)
@Getter
public class User {

	@Id
	private UUID id;

	private String name;

	private LocalDateTime createAt;

	public User(String name) {
		this.name = name;
		this.createAt = LocalDateTime.now();
	}

	public void setName(String name) {
		this.name = name;
	}

}
