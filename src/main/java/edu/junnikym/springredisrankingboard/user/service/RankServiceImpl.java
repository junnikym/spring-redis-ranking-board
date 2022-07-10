package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.dto.RankResponseDto;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RankServiceImpl implements RankService {

	private static final String RANK_KEY = "rank";
	private static final Integer N_SELECTION = 10;

	private final RedisTemplate<String, String> template;
	private final ZSetOperations<String, String> operations;

	public RankServiceImpl (RedisTemplate<String, String> redisTemplate) {
		this.template 	= redisTemplate;
		this.operations = redisTemplate.opsForZSet();
	}

	@Override
	@Transactional
	public void add(String nickname, double score) {
		operations.add(RANK_KEY, nickname, score);
	}

	@Override
	public List<RankResponseDto> get (int n) {
		AtomicInteger index = new AtomicInteger();
		return operations
				.reverseRangeWithScores(RANK_KEY, 0, n-1)
				.stream()
				.sorted((lhs, rhs) -> rhs.getScore().compareTo(lhs.getScore()))
				.map((tuple)->RankResponseDto.of(index.incrementAndGet(), tuple))
				.collect(Collectors.toList());
	}

	@Override
	public RankResponseDto get (String nickname) {
		final Long rank = operations.reverseRank(RANK_KEY, nickname);
		if(rank == null)
			return null;

		final Double score = operations.score(RANK_KEY, nickname);
		return RankResponseDto.of(rank.intValue()+1, nickname, score);
	}

}