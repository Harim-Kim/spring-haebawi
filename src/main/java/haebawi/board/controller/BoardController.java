package haebawi.board.controller;

import haebawi.board.auth.PrincipalDetails;
import haebawi.board.domain.dto.BoardRequest;
import haebawi.board.domain.dto.ReplyRequest;
import haebawi.board.domain.dto.ResponseCommon;
import haebawi.board.domain.entity.Board;
import haebawi.board.domain.entity.User;
import haebawi.board.service.BoardService;
import haebawi.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
@RequestMapping("/board")
public class BoardController {
    private final UserService userService;
    private final BoardService boardService;
    /*
    read
     */
    @GetMapping({"","/"})
    public String indexList(Model model, @PageableDefault(size=5, sort="id", direction = Sort.Direction.DESC)Pageable pageable){
        model.addAttribute("boards", boardService.boardList(pageable));
        return "board/index";
    }
    @GetMapping("/{boardId}")
    public String board(@PathVariable("boardId") Long boardId, Model model, Principal principal){
        Board board = boardService.getBoardById(boardId);
        model.addAttribute("boardResponse", board);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("author", board.getUser().equals(currentUser));
        model.addAttribute("replyRequest", new ReplyRequest());
        return "board/one_board";
    }
    /*
    create
     */
    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("boardRequest", new BoardRequest());
        return "board/create";
    }
    @PostMapping("/create")
    public String create(@Valid BoardRequest boardRequest, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes){
        if(boardRequest.getTitle() == null || boardRequest.getTitle().isEmpty()){
            bindingResult.addError(new FieldError("boardRequest","title", "제목을 입력해주세요"));
        }
        if(boardRequest.getTitle() == null || boardRequest.getTitle().isEmpty()){
            bindingResult.addError(new FieldError("boardRequest","content", "내용을 입력해주세요"));
        }
        if (bindingResult.hasErrors()){
            return "board/create";
        }
        User user = userService.getLoginUserByLoginId(principal.getName());
        boardRequest.setUser(user);
        Long boardId = boardService.save(user, boardRequest);
        if (boardId == null){
            bindingResult.addError(new ObjectError("boardRequest", "게시물 생성 중 에러가 발생하였습니다. 잠시 후 재 작성해주세요."));
        }

        if (bindingResult.hasErrors()){
            return "board/create";
        }
//        return "redirect:/board";
        redirectAttributes.addAttribute("boardId", boardId);
        return "redirect:/board/{boardId}";
    }

    /*
    update
     */
    @GetMapping("/{boardId}/update")
    public String updatePage(@PathVariable("boardId") Long boardId,@Valid BoardRequest boardRequest, Model model, Principal principal, BindingResult bindingResult){
        Board board = boardService.getBoardById(boardId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        model.addAttribute("boardRequest", boardRequest);
        model.addAttribute("boardResponse", board);
        if(!board.getUser().equals(currentUser)){
            bindingResult.addError(new ObjectError("boardResponse", "본인이 작성한 게시물이 아닙니다."));
            return "redirect:board";
        }


        return "board/update";
    }
    @PostMapping("/{boardId}")
    public String update(@PathVariable("boardId") Long boardId, @Valid BoardRequest boardRequest, Model model, Principal principal, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        Board board = boardService.getBoardById(boardId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(!board.getUser().equals(currentUser)){
            bindingResult.addError(new FieldError("boardRequest","user", "본인이 작성한 게시물이 아닙니다."));
            return "redirect:board";
        }
        if(boardRequest.getTitle() == null || boardRequest.getTitle().isEmpty()){
            bindingResult.addError(new FieldError("boardRequest","title", "제목을 입력해주세요"));
        }
        if(boardRequest.getTitle() == null || boardRequest.getTitle().isEmpty()){
            bindingResult.addError(new FieldError("boardRequest","content", "내용을 입력해주세요"));
        }
        if(bindingResult.hasErrors()){
            return "board/update";
        }
        boardService.update(boardId, boardRequest);
        redirectAttributes.addAttribute("boardId", boardId);
        return "redirect:/board/{boardId}";
    }
    /*
    delete
     */

    @DeleteMapping("/{boardId}")
    public String delete(@PathVariable("boardId") Long boardId, Model model, Principal principal, RedirectAttributes redirectAttributes){
        Board board = boardService.getBoardById(boardId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        if(!board.getUser().equals(currentUser)){
            model.addAttribute("error","본인이 작성한 게시물이 아닙니다.");
            redirectAttributes.addAttribute("boardId", boardId);
            return "redirect:/board/{boardId}";
        }

        boardService.delete(boardId);
        return "redirect:/board/";
    }

    @PostMapping("/{boardId}/reply")
    public String replySave(@PathVariable Long boardId, @Valid ReplyRequest replyRequest, Principal principal, RedirectAttributes redirectAttributes){
        replyRequest.setBoardId(boardId);
        User currentUser = userService.getLoginUserByLoginId(principal.getName());
        replyRequest.setUserId(currentUser.getId());
        boardService.replyForm(replyRequest);
        redirectAttributes.addAttribute("boardId", boardId);
        return "redirect:/board/{boardId}";
    }

    @DeleteMapping("/{boardId}/reply/{replyId}")
    public String replyDelete(@PathVariable Long boardId, @PathVariable Long replyId, RedirectAttributes redirectAttributes){
        boardService.replyDelete(replyId);
        redirectAttributes.addAttribute("boardId", boardId);
        return "redirect:/board/{boardId}";
    }

}
