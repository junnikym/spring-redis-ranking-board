package edu.junnikym.springredisrankingboard.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.OK)
public class NotGreaterScore extends RuntimeException {

	public NotGreaterScore() {
		super("Score is not greater than existing score");
	}

}
