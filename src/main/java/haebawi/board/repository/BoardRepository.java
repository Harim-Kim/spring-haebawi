package haebawi.board.repository;

import haebawi.board.domain.entity.Board;
import haebawi.board.domain.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByAuthor(String nickname);
    Optional<Board> findByTitle(String title);
    Optional<Board> findByContent(String content);

    List<Board> findTop10By();

}
