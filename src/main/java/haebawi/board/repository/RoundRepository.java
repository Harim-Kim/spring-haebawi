package haebawi.board.repository;

import haebawi.board.domain.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {
    Optional<Round> findByTeamIdAndSectionNumAndMemberName(Long teamId, int sectionNum, String memberName);
}
