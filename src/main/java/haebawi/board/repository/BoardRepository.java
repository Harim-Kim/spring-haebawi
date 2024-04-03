package haebawi.board.repository;

import haebawi.board.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByAuthor(String nickname);
    Optional<Board> findByTitle(String title);
    Optional<Board> findByContent(String content);



}
