package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.dto.JoinRequestDto;
import edu.junnikym.springredisrankingboard.user.dto.UpdateRequestDto;
import edu.junnikym.springredisrankingboard.user.entity.User;
import edu.junnikym.springredisrankingboard.user.exception.NotGreaterScore;
import edu.junnikym.springredisrankingboard.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserJpaRepository userJpaRepository;

	@Override
	@Transactional
	public User join(JoinRequestDto nickname) {
		final User newUser = nickname.toEntity();
		return userJpaRepository.save(newUser);
	}

	@Override
	public User find(String nickname) {
		return userJpaRepository
				.findByNickname(nickname)
				.orElseThrow(()-> new IllegalArgumentException("Can not found user"));
	}

	@Override
	@Transactional
	public User update (UpdateRequestDto request) {

		final User target = userJpaRepository.findById(request.getId())
				.orElseThrow(()-> new EntityExistsException("Target is not exist"));

		final long score = request.getScore();
		if(target.getScore() >= score)
			throw new NotGreaterScore();

		target.setScore(score);
		return userJpaRepository.save(target);
	}


}
