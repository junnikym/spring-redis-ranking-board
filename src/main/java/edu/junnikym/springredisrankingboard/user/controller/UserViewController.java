package edu.junnikym.springredisrankingboard.user.controller;


import edu.junnikym.springredisrankingboard.user.dto.JoinRequestDto;
import edu.junnikym.springredisrankingboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLIntegrityConstraintViolationException;

@Controller
@RequiredArgsConstructor
public class UserViewController {

	private final UserService userService;

	@RequestMapping(value = "/",method = RequestMethod.GET)
	public String index(Model model, HttpSession session){
		session.invalidate();
		model.addAttribute("user", new JoinRequestDto());
		return "index.html";
	}

	@PostMapping
	public ModelAndView join(
			HttpSession session,
			@ModelAttribute("user") JoinRequestDto request
	) {
		try {
			userService.find(request.getNickname());
		} catch (IllegalArgumentException e) {
			userService.join(request);
		}

		session.setAttribute("user", request.getNickname());
		return new ModelAndView("redirect:/game");
	}

}
