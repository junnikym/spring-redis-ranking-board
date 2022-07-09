package edu.junnikym.springredisrankingboard.user.dto;

import com.sun.istack.NotNull;
import edu.junnikym.springredisrankingboard.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDto {

	private String nickname;

	public JoinRequestDto (String nickname) {
		this.nickname = nickname;
	}

	public User toEntity() {
		return new User(nickname);
	}

}
