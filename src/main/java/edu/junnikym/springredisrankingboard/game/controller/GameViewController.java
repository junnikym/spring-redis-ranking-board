package edu.junnikym.springredisrankingboard.game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GameViewController {

	@RequestMapping(value = "/game",method = RequestMethod.GET)
	public String game(Model model){
		return "/game/index.html";
	}

}
