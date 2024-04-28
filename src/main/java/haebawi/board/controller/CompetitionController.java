package haebawi.board.controller;


import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.*;
import haebawi.board.domain.entity.Competition;
import haebawi.board.domain.entity.GradeGroup;
import haebawi.board.domain.entity.Team;
import haebawi.board.domain.entity.User;
import haebawi.board.service.CompetitionService;
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
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/competition")
public class CompetitionController {
    private final UserService userService;
    private final CompetitionService competitionService;

    @GetMapping({"","/"})
    public String indexList(Model model, @PageableDefault(size=5, sort="id", direction = Sort.Direction.DESC) Pageable pageable, Principal principal){
        model.addAttribute("competitions", competitionService.CompetitionList(pageable));
        User user = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", user);
        return "competition/index";
    }

    /*
    Create
     */
    @GetMapping("/create")
    public String create(Model model, Principal principal){
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("competitionRequest", new CompetitionRequest());
        return "competition/create";
    }
    @PostMapping("/create")
    public String create(@Valid CompetitionRequest competitionRequest, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes){

        if(competitionRequest.getName() == null || competitionRequest.getName().isEmpty()){
            bindingResult.addError(new FieldError("competitionRequest","name", "이름을 입력해주세요"));
        }
        if (bindingResult.hasErrors()){
            return "competition/create";
        }
        User user = userService.getLoginUserByLoginId(principal.getName());
        Long competitionId = competitionService.save(user, competitionRequest);
        if (competitionId == null){
            bindingResult.addError(new ObjectError("competitionRequest", "대회 생성 중 에러가 발생하였습니다. 잠시 후 재 작성해주세요."));
        }

        if (bindingResult.hasErrors()){
            return "competition/create";
        }
        redirectAttributes.addAttribute("competitionId", competitionId);
        return "redirect:/competition/{competitionId}";
    }
    /*
    Read
     */
    @GetMapping("/{competitionId}")
    public String competition(@PathVariable("competitionId") Long competitionId, Model model, Principal principal){
        Competition competition = competitionService.getCompetitionById(competitionId);
        model.addAttribute("competitionResponse", competition); // 이미 team을 가지고 있음.
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("gradegroupRequest", new GradegroupRequest());
        // 예선 순위, 결승 순위

//        Map<GradeGroup, List<CompetitionMemberScore>> finalMember = competitionService.GetBestMemberGrade(competition); // grade당 순위
//        List<Team> bestTeam = competitionService.GetBestTeam(competition); // 결승 진출자
//        model.addAttribute("finalMember", finalMember);
//        model.addAttribute("bestTeam", bestTeam);
        // 현재 최고점
        // 현재 최고점수
        return "competition/one_competition";
    }

    /*
    Update
     */
    @GetMapping("/{competitionId}/update")
    public String updatePage(@PathVariable("competitionId") Long competitionId, @Valid CompetitionRequest competitionRequest, Model model, Principal principal, BindingResult bindingResult){
        Competition competition = competitionService.getCompetitionById(competitionId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("competitionRequest", competitionRequest);
        model.addAttribute("competitionResponse", competition);
        if(currentUser.getRole() != UserRole.ADMIN){
            bindingResult.addError(new ObjectError("festivalResponse", "admin이 아닙니다."));
            return "redirect:competition";
        }
        return "competition/update";
    }
    @PostMapping("/{competitionId}")
    public String update(@PathVariable("competitionId") Long competitionId, @Valid CompetitionRequest competitionRequest, Model model, Principal principal, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        Competition competition = competitionService.getCompetitionById(competitionId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(currentUser.getRole() != UserRole.ADMIN){
            bindingResult.addError(new ObjectError("festivalResponse", "admin이 아닙니다."));
            return "redirect:competition";
        }
        if(competitionRequest.getName() == null || competitionRequest.getName().isEmpty()){
            bindingResult.addError(new FieldError("competitionRequest","name", "이름을 입력해주세요"));
        }
        if(bindingResult.hasErrors()){
            return "competition/update";
        }
        competitionService.update(competitionId, competitionRequest);
        redirectAttributes.addAttribute("competitionId", competitionId);
        return "redirect:/competition/{competitionId}";
    }
    /*
    Delete
     */
    @DeleteMapping("/{competitionId}")
    public String delete(@PathVariable("competitionId") Long competitionId, Model model, Principal principal, RedirectAttributes redirectAttributes){
        Competition competition = competitionService.getCompetitionById(competitionId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(currentUser.getRole() != UserRole.ADMIN){
            model.addAttribute("error","Admin이 아닙니다.");
            redirectAttributes.addAttribute("competitionId", competitionId);
            return "redirect:/competition/{competitionId}";
        }
        competitionService.delete(competitionId);
        return "redirect:/competition/";
    }

    /*
    grade group 생성
     */
    @PostMapping("/{competitionId}/gradegroup")
    public String teamSave(@PathVariable("competitionId") Long competitionId,@Valid GradegroupRequest gradegroupRequest, Principal principal, RedirectAttributes redirectAttributes){
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(currentUser.getRole() != UserRole.ADMIN){
            redirectAttributes.addAttribute("competitionId", competitionId);
            return "redirect:/competition/{competitionId}";
        }
        gradegroupRequest.setCompetitionId(competitionId);
        competitionService.gradegroupSave(gradegroupRequest);
        redirectAttributes.addAttribute("competitionId", competitionId);
        return "redirect:/competition/{competitionId}";

    }
    /*
    final 생성
     */
    @PostMapping("/{competitionId}/final")
    public String createFinal(@PathVariable("competitionId") Long competitionId, Principal principal, RedirectAttributes redirectAttributes){
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(currentUser.getRole() != UserRole.ADMIN){
            redirectAttributes.addAttribute("competitionId", competitionId);
            return "redirect:/competition/{competitionId}";
        }
        competitionService.createFinal(competitionId);
        redirectAttributes.addAttribute("competitionId", competitionId);
        return "redirect:/competition/{competitionId}";

    }

    /*
    난이도 상세 보기
     */
    @GetMapping("/{competitionId}/gradegroup/{gradegroupId}")
    public String gradeGroupView(@PathVariable("competitionId") Long competitionId, @PathVariable("gradegroupId") Long gradegroupId,Model model, Principal principal){
        User user = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", user);
        GradeGroup gradeGroup = competitionService.gradeGroup(gradegroupId);
        model.addAttribute("gradeGroupResponse", gradeGroup);
        model.addAttribute("competitionId", competitionId);
        Competition competition = competitionService.getCompetitionById(competitionId);
        model.addAttribute("competition", competition);
        return "one_gradegroup";
    }

    /*
    점수 입력
     */

    @PostMapping("/{competitionId}/gradegroup/{gradegroupId}/score")
    public String scoreUpdate(@PathVariable("competitionId") Long competitionId, @PathVariable("gradegroupId") Long gradegroupId, @RequestParam Map<String, String> data, RedirectAttributes redirectAttributes){

        System.out.println(data); //{1-0=11, 1-1=22, 1-2=33, 2-0=66, 2-1=55, 2-2=44}
        int result = competitionService.scoreUpdate(data, gradegroupId, competitionId);
        if (result == 0){
            // 에러 작업
        }
        redirectAttributes.addAttribute("competitionId", competitionId);
        redirectAttributes.addAttribute("gradegroupId", gradegroupId);
        return "redirect:/competition/{competitionId}/gradegroup/{gradegroupId}";
    }

}
