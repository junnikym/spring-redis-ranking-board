package edu.junnikym.springredisrankingboard.user.service;

import edu.junnikym.springredisrankingboard.user.entity.User;

import java.util.UUID;

public interface UserService {

	User join(String nickname);

	User find(String nickname);

	User update(UUID id, long score);

}
