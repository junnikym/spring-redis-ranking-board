package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.dto.UpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Service
@RequiredArgsConstructor
public class ScoreProjectionSubscriber implements MessageListener {

	private final RankService rankService;

	@SneakyThrows
	@Override
	public void onMessage(Message message, byte[] pattern) {
		UpdateRequestDto dto = (UpdateRequestDto) deserialize(message.getBody());
		rankService.add(dto.getNickname(), dto.getScore());
	}

	private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

}
