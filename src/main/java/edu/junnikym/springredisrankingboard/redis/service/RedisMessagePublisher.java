package edu.junnikym.springredisrankingboard.redis.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisMessagePublisher<T> {

	private RedisTemplate<String, T> redisTemplate;

	private ChannelTopic topic;

	public RedisMessagePublisher(RedisTemplate<String, T> redisTemplate, final ChannelTopic topic) {
		this.redisTemplate = redisTemplate;
		this.topic = topic;
	}

	public void publish(T message) {
		redisTemplate.convertAndSend(topic.getTopic(), message);
	}

}
