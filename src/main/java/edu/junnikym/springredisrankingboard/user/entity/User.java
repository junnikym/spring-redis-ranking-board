package edu.junnikym.springredisrankingboard.user.entity;

//@RedisHash (value="user", timeToLive=30)

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false, columnDefinition = "0")
	private Long score;

	@CreatedDate
	private LocalDateTime createAt;

	@Column
	private LocalDateTime updateAt;

	public User(String nickname) {
		this.nickname = nickname;
		this.createAt = LocalDateTime.now();
	}

	public void setScore(Long score) {
		this.score = score;
	}

//  < Redis Practice >
//
//	public void setName(UUID id, String name) {
//		this.id 	= id;
//		this.name 	= name;
//	}
//
//	public Long getId () {
//		return id;
//	}
}
