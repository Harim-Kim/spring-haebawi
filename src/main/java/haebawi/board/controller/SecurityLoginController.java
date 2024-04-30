package haebawi.board.controller;

import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.JoinRequest;
import haebawi.board.domain.dto.LoginRequest;
import haebawi.board.service.UserService;
import haebawi.board.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class SecurityLoginController {
    private final UserService userService;
    private static String[] admin_id = {"admin1", "admin2", "admin3", "admin4", "admin5"};
    private static ArrayList<String> admins = new ArrayList<>(Arrays.asList(admin_id));
    @GetMapping(value = {"", "/"})
    public String home(Model model, Authentication auth) {
        model.addAttribute("loginType", "user");
        model.addAttribute("pageName", "로그인");

        if(auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }

        return "redirect:/";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "user");
        model.addAttribute("pageName", "로그인");

        model.addAttribute("joinRequest", new JoinRequest());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "user");
        model.addAttribute("pageName", "로그인");

        // loginId 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }
        // 닉네임 중복 체크.

        if(userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
        }
        // password와 passwordCheck가 같은지 체크
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "바밀번호가 일치하지 않습니다."));
        }

        if(bindingResult.hasErrors()) {
            return "join";
        }
        if(admins.contains(joinRequest.getLoginId())){
            joinRequest.setUserRole(UserRole.ADMIN);
        }else{
            joinRequest.setUserRole(UserRole.USER);
        }
        userService.join2(joinRequest);
        return "redirect:/user";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String exception,
                            Model model) {
        model.addAttribute("loginType", "user");
        model.addAttribute("pageName", "로그인");
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }


    @GetMapping("/info")
    public String userInfo(Model model, Authentication auth) {
        model.addAttribute("loginType", "user");
        model.addAttribute("pageName", "로그인");

        User loginUser = userService.getLoginUserByLoginId(auth.getName());

        if(loginUser == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("currentUser", loginUser);
        return "info";
    }

    @GetMapping("/admin")
    public String adminPage( Model model) {
        model.addAttribute("loginType", "user");
        model.addAttribute("pageName", "로그인");

        return "admin";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // session이 없으면 null return

        if(session != null){
            session.invalidate();
        }

        return "redirect:/user";
    }
}
