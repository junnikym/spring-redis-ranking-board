package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.dto.JoinRequestDto;
import edu.junnikym.springredisrankingboard.user.dto.UpdateRequestDto;
import edu.junnikym.springredisrankingboard.user.entity.User;

import java.util.UUID;

public interface UserService {

	void join(JoinRequestDto nickname);

	User find(String nickname);

	void update(UpdateRequestDto update);

}
