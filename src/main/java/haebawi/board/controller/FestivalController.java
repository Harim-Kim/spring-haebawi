package haebawi.board.controller;


import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.*;
import haebawi.board.domain.entity.*;
import haebawi.board.service.FestivalService;
import haebawi.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/festival")
public class FestivalController {
    private final UserService userService;
    private final FestivalService festivalService;

    @GetMapping({"","/"})
    public String indexList(Model model, @PageableDefault(size=5, sort="id", direction = Sort.Direction.DESC) Pageable pageable, Principal principal){
        model.addAttribute("festivals", festivalService.festivalList(pageable));
        User user = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", user);
        return "festival/index";
    }

    /*
    Create
     */
    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("festivalRequest", new FestivalRequest());
        return "festival/create";
    }
    @PostMapping("/create")
    public String create(@Valid FestivalRequest festivalRequest, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes){

        if(festivalRequest.getName() == null || festivalRequest.getName().isEmpty()){
            bindingResult.addError(new FieldError("festivalRequest","name", "이름을 입력해주세요"));
        }
        if (bindingResult.hasErrors()){
            return "festival/create";
        }
        User user = userService.getLoginUserByLoginId(principal.getName());
        Long festivalId = festivalService.save(user, festivalRequest);
        if (festivalId == null){
            bindingResult.addError(new ObjectError("festivalRequest", "볼파 생성 중 에러가 발생하였습니다. 잠시 후 재 작성해주세요."));
        }

        if (bindingResult.hasErrors()){
            return "festival/create";
        }
        redirectAttributes.addAttribute("festivalId", festivalId);
        return "redirect:/festival/{festivalId}";
    }
    /*
    Read
     */
    @GetMapping("/{festivalId}")
    public String festival(@PathVariable("festivalId") Long festivalId, Model model, Principal principal){
        Festival festival = festivalService.getFestivalById(festivalId);
        model.addAttribute("festivalResponse", festival); // 이미 team을 가지고 있음.
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("teamRequest", new TeamRequest());
//        model.addAttribute("sectionRequest", new SectionRequest());

        return "festival/one_festival";
    }

    /*
    Update
     */
    @GetMapping("/{festivalId}/update")
    public String updatePage(@PathVariable("festivalId") Long festivalId,@Valid FestivalRequest festivalRequest, Model model, Principal principal, BindingResult bindingResult){
        Festival festival = festivalService.getFestivalById(festivalId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("festivalRequest", festivalRequest);
        model.addAttribute("festivalResponse", festival);
        if(currentUser.getRole() != UserRole.ADMIN){
            bindingResult.addError(new ObjectError("festivalResponse", "admin이 아닙니다."));
            return "redirect:festival";
        }
        return "festival/update";
    }
    @PostMapping("/{festivalId}")
    public String update(@PathVariable("festivalId") Long festivalId, @Valid FestivalRequest festivalRequest, Model model, Principal principal, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        Festival festival = festivalService.getFestivalById(festivalId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(currentUser.getRole() != UserRole.ADMIN){
            bindingResult.addError(new ObjectError("festivalResponse", "admin이 아닙니다."));
            return "redirect:festival";
        }
        if(festivalRequest.getName() == null || festivalRequest.getName().isEmpty()){
            bindingResult.addError(new FieldError("festivalRequest","name", "이름을 입력해주세요"));
        }
        if(bindingResult.hasErrors()){
            return "festival/update";
        }
        festivalService.update(festivalId, festivalRequest);
        redirectAttributes.addAttribute("festivalId", festivalId);
        return "redirect:/festival/{festivalId}";
    }
    /*
    Delete
     */
    @DeleteMapping("/{festivalId}")
    public String delete(@PathVariable("festivalId") Long festivalId, Model model, Principal principal, RedirectAttributes redirectAttributes){
        Festival festival = festivalService.getFestivalById(festivalId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(currentUser.getRole() != UserRole.ADMIN){
            model.addAttribute("error","본인이 작성한 게시물이 아닙니다.");
            redirectAttributes.addAttribute("festivalId", festivalId);
            return "redirect:/festival/{festivalId}";
        }
        festivalService.delete(festivalId);
        return "redirect:/festival/";
    }

    /*
    Team 생성
     */
    @PostMapping("/{festivalId}/team")
    public String teamSave(@PathVariable("festivalId") Long festivalId,@Valid TeamRequest teamRequest, Principal principal, RedirectAttributes redirectAttributes){
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(currentUser.getRole() != UserRole.ADMIN){
            redirectAttributes.addAttribute("festivalId", festivalId);
            return "redirect:/festival/{festivalId}";
        }
        teamRequest.setFestivalId(festivalId);
        festivalService.teamSave(teamRequest);
        redirectAttributes.addAttribute("festivalId", festivalId);
        return "redirect:/festival/{festivalId}";

    }

    /*
    팀 상세 보기
     */
    @GetMapping("/{festivalId}/team/{teamId}")
    public String teamView(@PathVariable("festivalId") Long festivalId, @PathVariable("teamId") Long teamId,Model model, Principal principal){
        User user = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", user);
        Team team = festivalService.team(teamId);
        model.addAttribute("teamResponse", team);
        model.addAttribute("festivalId", festivalId);
        Festival festival = festivalService.getFestivalById(festivalId);
        model.addAttribute("sectionNum", festival.getSection_num());
        return "festival/one_team";
    }

    /*
    점수 입력
     */

    @GetMapping("/{festivalId}/team/{teamId}/score")
    public String scoreInput(@PathVariable("festivalId") Long festivalId, @PathVariable("teamId") Long teamId, @RequestParam("section") int sectionNum, @RequestParam("index") int indexNum, Model model, Principal principal){
        User user = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("festivalId", festivalId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("currentUser", user);
        model.addAttribute("scoreRequest", new ScoreInputFestivalRequest());
        model.addAttribute("sectionNum", sectionNum);
        model.addAttribute("indexNum", indexNum);
        return "festival/score_input";
    }

    @PostMapping("/{festivalId}/team/{teamId}/score")
    public void scoreUpdate(@PathVariable("festivalId") Long festivalId, @PathVariable("teamId") Long teamId, @Valid ScoreInputFestivalRequest  scoreInputFestivalRequest){
        scoreInputFestivalRequest.setFestivalId(festivalId);
        scoreInputFestivalRequest.setTeamId(teamId);
        festivalService.scoreUpdate(scoreInputFestivalRequest);
    }
}
