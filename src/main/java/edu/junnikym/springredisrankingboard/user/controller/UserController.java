package edu.junnikym.springredisrankingboard.user.controller;

import edu.junnikym.springredisrankingboard.user.dto.JoinRequestDto;
import edu.junnikym.springredisrankingboard.user.dto.UpdateRequestDto;
import edu.junnikym.springredisrankingboard.user.entity.User;
import edu.junnikym.springredisrankingboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

@RestController
@RequestMapping(value="/api/v1/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<?> join(
			@RequestBody JoinRequestDto request
	) {
		userService.join(request);

		return ResponseEntity.status(201).build();
	}

	@GetMapping()
	public ResponseEntity<?> find(
			@RequestParam String nickname
	) {
		final User user = userService.find(nickname);

		return ResponseEntity.ok().body(user);
	}

	@PutMapping
	public ResponseEntity<?> update(
			HttpSession session,
			@RequestBody UpdateRequestDto request
	) {
		request.setNickname ((String) session.getAttribute("user"));
		userService.update(request);

		return ResponseEntity.ok().build();
	}

	//////////////////////////////
	// Exception Handlers
	//////////////////////////////

	@ExceptionHandler
	public ResponseEntity<String> alreadyExistUser(
			SQLIntegrityConstraintViolationException ex
	) {
		return ResponseEntity.status(423).build();
	}

	@ExceptionHandler
	public ResponseEntity<String> notFoundUser(
			IllegalArgumentException ex
	) {
		return ResponseEntity.status(404).build();
	}

}
