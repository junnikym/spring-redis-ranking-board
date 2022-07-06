package edu.junnikym.springredisrankingboard.user.dto;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.redis.core.ZSetOperations;

@Getter
@ToString
public class RankResponseDto {

	private final Integer rank;

	private final String nickname;

	private final Double score;

	public RankResponseDto(Integer rank, String nickname, Double score) {
		this.rank = rank;
		this.nickname = nickname;
		this.score = score;
	}

	public static RankResponseDto of(Integer rank, String nickname, Double score) {
		return new RankResponseDto(rank, nickname, score);
	}

	public static RankResponseDto of(Integer rank, ZSetOperations.TypedTuple<String> tuple) {
		return new RankResponseDto(
				rank,
				tuple.getValue(),
				tuple.getScore()
		);
	}

}
