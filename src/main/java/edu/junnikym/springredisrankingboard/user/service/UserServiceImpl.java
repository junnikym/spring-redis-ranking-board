package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.entity.User;
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
	public User join(String nickname) {
		final User newUser = new User(nickname);
		return userJpaRepository.save(newUser);
	}

	@Override
	public User find(String nickname) {
		return userJpaRepository.findByNickname(nickname);
	}

	@Override
	@Transactional
	public User update (UUID id, long score) {
		final User target = userJpaRepository.findById(id)
				.orElseThrow(()-> new EntityExistsException("Target is not exist"));

		target.setScore(score);
		return userJpaRepository.save(target);
	}


}
