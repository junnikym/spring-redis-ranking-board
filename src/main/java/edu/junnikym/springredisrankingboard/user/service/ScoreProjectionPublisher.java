package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.dto.UpdateRequestDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class ScoreProjectionPublisher {

	private final RedisTemplate<String, UpdateRequestDto> redisTemplate;
	private final ChannelTopic rankTopic;

	public ScoreProjectionPublisher (
			RedisTemplate<String, UpdateRequestDto> redisTemplate,
			ChannelTopic rankTopic
	) {
		this.redisTemplate = redisTemplate;
		this.rankTopic = rankTopic;
	}

	public void publishMessage (UpdateRequestDto message) {
		redisTemplate.convertAndSend(rankTopic.getTopic(), message);
	}

}
