package haebawi.board.service;

import haebawi.board.domain.dto.BoardRequest;
import haebawi.board.domain.dto.ReplyRequest;
import haebawi.board.domain.entity.Board;
import haebawi.board.domain.entity.Reply;
import haebawi.board.domain.entity.User;
import haebawi.board.repository.BoardRepository;
import haebawi.board.repository.ReplyRepository;
import haebawi.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    @Transactional
    public Long save(User user, BoardRequest request){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalName
//        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> {
//            return new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
//        });
        if (user == null){
            return null;
        }
        request.setUser(user);
        Board board = request.toEntity();
        boardRepository.save(board);

        return board.getId();

    }

    public Board getBoardById(Long boardId){
        if (boardId == null) return null;

        return boardRepository.findById(boardId)
                .orElseThrow(()->{
                   return new IllegalArgumentException("글 상세 보기 실패");
                });
//        Optional<Board> optionalBoard = boardRepository.findById(boardId);
//        List<Comment> comments = bo
//        if (optionalBoard.isEmpty()) return null;
//        return optionalBoard.get();
    }

    public List<Board> findAll(){
        return boardRepository.findAll();
    }

    @Transactional
    public Page<Board> boardList(Pageable pageable){
        return boardRepository.findAll(pageable);
    }
    @Transactional
    public Long update(Long boardId, BoardRequest boardRequest){
        Optional<Board> optionalBoardoard = boardRepository.findById(boardId);
        if (optionalBoardoard.isEmpty()){
            return -1L;
        }
        Board board = optionalBoardoard.get();
        board.setUpdated_at(LocalDateTime.now());
        board.setContent(boardRequest.getContent());
        board.setTitle(boardRequest.getTitle());
        boardRepository.save(board);

        return boardId;
    }
    @Transactional
    public Long delete(Long boardId){
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        if(optionalBoard.isEmpty()) return -1L;
        Board board = optionalBoard.get();
        board.setDeleted_at(LocalDateTime.now());
        boardRepository.save(board);
        return boardId;
    }



    //댓글 등록
    @Transactional
    public void replyForm(ReplyRequest replyRequest){
        User user = userRepository.findById(replyRequest.getUserId()).orElseThrow(()->{
            return new IllegalArgumentException("댓글 쓰기 실패: 유저 Id를 찾을 수 없습니다.");
        });
        Board board = boardRepository.findById(replyRequest.getBoardId()).orElseThrow(()->{
            return new IllegalArgumentException("댓글 쓰기 실패: 게시글 Id를 찾을 수 없습니다.");
        });

        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .content(replyRequest.getContent())
                .build();

        replyRepository.save(reply);
    }

    //댓글 삭제
    @Transactional
    public Long replyDelete(Long replyId){
        Optional<Reply> optionalReply = replyRepository.findById(replyId);
        if(optionalReply.isEmpty()) return -1L;
        Reply reply = optionalReply.get();
        reply.setDeleted_at(LocalDateTime.now());
        replyRepository.save(reply);
        return replyId;
    }
}
