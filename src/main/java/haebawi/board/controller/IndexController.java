package haebawi.board.controller;


import haebawi.board.domain.entity.Board;
import haebawi.board.domain.entity.Festival;
import haebawi.board.domain.entity.User;
import haebawi.board.service.BoardService;
import haebawi.board.service.FestivalService;
import haebawi.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {
    private final UserService userService;
    private final BoardService boardService;
    private final FestivalService festivalService;
    @GetMapping({"","/"})
    public String index(Model model, Principal principal){
        if (principal == null){
            return "index";
        }

        User user = userService.getLoginUserByLoginId(principal.getName());
        List<Board> boards = boardService.findLast();
        List<Festival> festivals = festivalService.findLast();
        model.addAttribute("currentUser", user);
        model.addAttribute("boards", boards);
        model.addAttribute("festivals", festivals);
        return "index";
    }
}
