package haebawi.board.service;

import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.BoardRequest;
import haebawi.board.domain.dto.FestivalRequest;
import haebawi.board.domain.dto.ScoreInputFestivalRequest;
import haebawi.board.domain.dto.TeamRequest;
import haebawi.board.domain.entity.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    // Team 등록 --> round도 등록
    @Transactional
    public void teamSave(TeamRequest teamRequest){
        Festival festival = festivalRepository.findById(teamRequest.getFestivalId()).orElseThrow(()->{
            return new IllegalArgumentException("Team 생성 실패: 야더링 Id를 찾을 수 없습니다.");
        });

        String[] members = teamRequest.getUser().split(",");
        // team 생성
        Team team = Team.builder()
                .team_name(teamRequest.getTeam_name())
                .festival(festival)
                .user(Arrays.asList(members))
                .build();
        teamRepository.save(team);
        // 팀원 수에 맞게 round 생성
        int section_num = festival.getSection_num();
        List<Round> roundList = new ArrayList<>();
        for(int i = 1; i < section_num+1; i++){
            for(String member : members){
                Round round = Round.builder()
                        .section_id(Integer.toUnsignedLong(i))
                        .tries(0)
                        .success(false)
                        .member_name(member)
                        .team(team)
                        .build();
                roundList.add(round);
                roundRepository.save(round);
            }
        }
    }

    @Transactional
    public Team team(Long teamId){
        Team team = teamRepository.findById(teamId).orElseThrow(()->{
            return new IllegalArgumentException("해당 팀을 찾을 수 없습니다.");
        });
        return team;
    }

    @Transactional
    public void scoreUpdate(ScoreInputFestivalRequest scoreInputFestivalRequest){
        Team team = teamRepository.findById(scoreInputFestivalRequest.getTeamId()).orElseThrow(()->{
            return new IllegalArgumentException("해당 팀을 찾을 수 없습니다.");
        });
        String member = team.getUser().get(scoreInputFestivalRequest.getIndexNum());
        Round round = roundRepository.findByTeamIdAndSectionIdAndMemberName(
                scoreInputFestivalRequest.getTeamId(),
                scoreInputFestivalRequest.getSectionNum(),
                member
        ).orElseThrow(()->{
            return new IllegalArgumentException("해당 점수표를 찾을 수 없습니다.");
        });
        round.setFestival_score(scoreInputFestivalRequest.getScore());
        roundRepository.save(round);
    }
}
