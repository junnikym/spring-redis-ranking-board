package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.dto.JoinRequestDto;
import edu.junnikym.springredisrankingboard.user.dto.UpdateRequestDto;
import edu.junnikym.springredisrankingboard.user.entity.User;

import java.util.UUID;

public interface UserService {

	User join(JoinRequestDto nickname);

	User find(String nickname);

	User update(UpdateRequestDto update);

}
