package haebawi.board.controller;


import haebawi.board.domain.entity.User;
import haebawi.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.logging.Level;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final UserService userService;
    @GetMapping({"","/"})
    public String indexList(Model model, Principal principal){ // @PageableDefault(size=5, sort="id", direction = Sort.Direction.DESC)Pageable pageable,
        if (!checkAdmin(principal)){
            return "redirect:/";
        }
        User user = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", user);
        model.addAttribute("users", userService.findAll());
        return "member/user_list";
    }

    @GetMapping("/{userId}")
    public String board(@PathVariable("userId") Long userId, Model model, Principal principal){
        if (checkAdmin(principal)){
            return "redirect:/";
        }
        User user = userService.getLoginUserById(userId);
        model.addAttribute("userResponse", user);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", currentUser);
        return "member/one_user";
    }

    @PostMapping("/changeRole")
    public String chageRole( @RequestParam Map<String, String> data){
        System.out.println(data+"@@@@@@@@@@@@@@@@@@@@@@@");
        log.warn("@@@@@@@@@@@@@@@@@@");
        log.warn(data.toString());
        log.warn("@@@@@@@@@@@@@@@@@@");
        return "redirect:/member/";
    }
//    @PostMapping("/{userId}")
//    public String update(@PathVariable("userId") Long userId, @Valid UserReq festivalRequest, Model model, Principal principal, BindingResult bindingResult, RedirectAttributes redirectAttributes){
//        User user = userService.getLoginUserById(userId);
//        User currentUser = userService.getLoginUserByLoginId(principal.getName());
//        if(currentUser.getRole() != UserRole.ADMIN){
//            bindingResult.addError(new ObjectError("festivalResponse", "admin이 아닙니다."));
//            return "redirect:festival";
//        }
//        if(festivalRequest.getName() == null || festivalRequest.getName().isEmpty()){
//            bindingResult.addError(new FieldError("festivalRequest","name", "이름을 입력해주세요"));
//        }
//        if(bindingResult.hasErrors()){
//            return "festival/update";
//        }
//        festivalService.update(festivalId, festivalRequest);
//        redirectAttributes.addAttribute("festivalId", festivalId);
//        return "redirect:/festival/{festivalId}";
//    }
    private boolean checkAdmin(Principal principal){
        User user = userService.getLoginUserByLoginId(principal.getName());
        if (user.getRole().name().equals("ADMIN") ){
            return true;
        }
        return false;
    }
}
