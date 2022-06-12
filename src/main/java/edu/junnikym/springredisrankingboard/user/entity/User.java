package edu.junnikym.springredisrankingboard.user.entity;

//@RedisHash (value="user", timeToLive=30)

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
public class User {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(nullable = false, unique=true)
	private String nickname;

	@Column()
	@ColumnDefault("0")
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
