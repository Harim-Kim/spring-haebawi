package haebawi.board.service;

import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.BoardRequest;
import haebawi.board.domain.dto.FestivalRequest;
import haebawi.board.domain.entity.Board;
import haebawi.board.domain.entity.Festival;
import haebawi.board.domain.entity.User;
import haebawi.board.repository.FestivalRepository;
import haebawi.board.repository.RoundRepository;
import haebawi.board.repository.TeamRepository;
import haebawi.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FestivalService {

    private final FestivalRepository festivalRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository; // festival에 종속적이므로 teamService 생성안함.
    private final RoundRepository roundRepository;

    @Transactional
    public Page<Festival> festivalList(Pageable pageable){
        return festivalRepository.findAll(pageable);
    }

    @Transactional
    public Long save(User user, FestivalRequest request){

        if (user.getRole() != UserRole.ADMIN){
            return null;
        }
        Festival festival = request.toEntity();
        festivalRepository.save(festival);

        return festival.getId();

    }

    public Festival getFestivalById(Long festivalId){
        if (festivalId == null) return null;

        return festivalRepository.findById(festivalId)
                .orElseThrow(()->{
                    return new IllegalArgumentException("볼파 상세 보기 실패");
                });
    }

    @Transactional
    public Long update(Long festivalId, FestivalRequest festivalRequest){
        Optional<Festival> optionalFestival = festivalRepository.findById(festivalId);
        if (optionalFestival.isEmpty()){
            return -1L;
        }
        Festival festival = optionalFestival.get();
        festival.setUpdated_at(LocalDateTime.now());
        festival.setName(festivalRequest.getName());
        festivalRepository.save(festival);

        return festivalId;
    }

    @Transactional
    public Long delete(Long festivalId){
        Optional<Festival> optionalFestival = festivalRepository.findById(festivalId);
        if(optionalFestival.isEmpty()) return -1L;
        Festival festival = optionalFestival.get();
        festival.setDeleted_at(LocalDateTime.now());
        festivalRepository.save(festival);
        return festivalId;
    }

}
