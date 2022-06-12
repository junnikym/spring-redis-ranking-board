package edu.junnikym.springredisrankingboard.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UpdateRequestDto {

	private UUID id;

	private long score;

	public UpdateRequestDto(UUID id, long score) {
		this.id = id;
		this.score = score;
	}

}
