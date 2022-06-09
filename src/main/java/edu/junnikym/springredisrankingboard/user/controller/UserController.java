package edu.junnikym.springredisrankingboard.user.controller;

import edu.junnikym.springredisrankingboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

	UserService userService;

	@PostMapping
	public void join(String nickname) {
		userService.join(nickname);
	}

	@GetMapping
	public void find(String nickname) {
		userService.find(nickname);
	}

	@PutMapping
	public void update(UUID id, long score) {
		userService.update(id, score);
	}

}
