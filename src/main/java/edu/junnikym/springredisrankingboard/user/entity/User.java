package edu.junnikym.springredisrankingboard.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.LocalDateTime;
import java.util.UUID;

//@RedisHash (value="user", timeToLive=30)
@Entity
@Getter
@NoArgsConstructor
public class User {

	@javax.persistence.Id
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(nullable = false)
	private String name;

	@CreatedDate
	private LocalDateTime createAt;

	@Column
	private LocalDateTime updateAt;

	public User(String name) {
		this.name = name;
		this.createAt = LocalDateTime.now();
	}

//	public void setName(UUID id, String name) {
//		this.id 	= id;
//		this.name 	= name;
//	}
//
//	public Long getId () {
//		return id;
//	}
}
