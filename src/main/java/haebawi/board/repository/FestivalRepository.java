package haebawi.board.repository;

import haebawi.board.domain.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
    List<Festival> findTop10By();
}
