package edu.junnikym.springredisrankingboard.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UpdateRequestDto implements Serializable {

	private String nickname;

	private long score;

	public UpdateRequestDto(String nickname, long score) {
		this.nickname = nickname;
		this.score = score;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
