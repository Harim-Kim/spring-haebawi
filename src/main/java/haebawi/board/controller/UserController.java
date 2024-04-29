package haebawi.board.controller;


import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.FestivalRequest;
import haebawi.board.domain.dto.ReplyRequest;
import haebawi.board.domain.entity.Board;
import haebawi.board.domain.entity.Festival;
import haebawi.board.domain.entity.User;
import haebawi.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class UserController {

    private final UserService userService;
    @GetMapping({"","/"})
    public String indexList(Model model, Principal principal){ // @PageableDefault(size=5, sort="id", direction = Sort.Direction.DESC)Pageable pageable,
        if (checkAdmin(principal)){
            return "redirect:/";
        }
        User user = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", user);
        model.addAttribute("boards", userService.findAll());
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
        if (user.getRole() == UserRole.ADMIN){
            return true;
        }
        return false;
    }
}
