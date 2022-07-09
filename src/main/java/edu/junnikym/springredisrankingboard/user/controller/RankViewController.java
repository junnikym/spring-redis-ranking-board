package edu.junnikym.springredisrankingboard.user.controller;

import edu.junnikym.springredisrankingboard.user.dto.RankResponseDto;
import edu.junnikym.springredisrankingboard.user.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RankViewController {

	final private RankService rankService;

	@RequestMapping(value = "/rank",method = RequestMethod.GET)
	public String score(Model model, HttpSession session){

		String nickname = (String) session.getAttribute("user");
		if(nickname != null) {
			final RankResponseDto rank = rankService.get(nickname);
			model.addAttribute("ownRank",rank);
		}

		final List<RankResponseDto> ranks = rankService.get(10);
		model.addAttribute("ranks", ranks);

		return "game/rank.html";
	}

}
