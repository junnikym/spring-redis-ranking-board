package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.dto.RankResponseDto;

import java.util.List;

public interface RankService {

	void add(String nickname, double score);

	/**
	 * Get score as many as 'n'
	 *
	 * @param n number of user's score
	 */
	List<RankResponseDto> get(int n);

	/**
	 * Get score by user's id
	 * @param
	 */
	RankResponseDto get(String nickname);

}
