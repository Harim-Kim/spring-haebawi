package haebawi.board.controller;


import haebawi.board.domain.entity.User;
import haebawi.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class IndexController {
    private final UserService userService;
    @GetMapping({"","/"})
    public String index(Model model, Principal principal){
        if (principal == null){
            return "index";
        }
        User user = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", user);
        return "index";
    }
}
