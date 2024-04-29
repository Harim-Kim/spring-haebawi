package haebawi.board.service;

import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.*;
import haebawi.board.domain.entity.*;
import haebawi.board.repository.FestivalRepository;
import haebawi.board.repository.RoundRepository;
import haebawi.board.repository.TeamRepository;
import haebawi.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FestivalService {

    private final FestivalRepository festivalRepository;
    private final TeamRepository teamRepository; // festival에 종속적이므로 teamService 생성안함.
    private final RoundRepository roundRepository;


    public List<Festival> findAll(){ return festivalRepository.findAll() ;}
    public List<Festival> findLast(){return festivalRepository.findTop10By();}
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
        for(int i = 1; i < section_num+1; i++){
            for(String member : members){
                Round round = Round.builder()
                        .sectionNum(Integer.toUnsignedLong(i))
                        .tries(0)
                        .success(false)
                        .memberName(member)
                        .team(team)
                        .build();
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
    public int scoreUpdate(Map<String, String> data, Long teamId, Long festivalId){
        Team team = teamRepository.findById(teamId).orElseThrow(()->{
            return new IllegalArgumentException("해당 팀을 찾을 수 없습니다.");
        });
        // 한번에 받아서 처리함
        for (Map.Entry<String, String> entrySet : data.entrySet()){
            String[] section_index = entrySet.getKey().split("-");
            String memberIndex = section_index[1];
            String sectionNum = section_index[0];
            String member;
            Round round;
            int score;
            try{
                score = Integer.parseInt(entrySet.getValue());
                member = team.getUser().get(Integer.parseInt(memberIndex));
                round = roundRepository.findByTeamIdAndSectionNumAndMemberName(
                        teamId,
                        Integer.parseInt(sectionNum),
                        member
                ).orElseThrow(()->{
                    return new IllegalArgumentException("해당 점수표를 찾을 수 없습니다.");
                });
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
            round.setFestival_score(score);
            roundRepository.save(round);
        }
        return 1;
    }

    public List<Team> GetBestTeam(Festival festival){
        List<Team> bestTeamList = festival.getTeam();
        bestTeamList.sort(Comparator.comparing(Team::GetTeamScore).reversed());
        return bestTeamList;
    }

    public List<MemberScore> GetBestMemberScore(Festival festival){
        List<Round> rounds = new ArrayList<>();
        List<MemberScore> memberScores = new ArrayList<>();
        Map<String, Integer> memberScore = new HashMap<>();
        for(Team team: festival.getTeam()){
            rounds.addAll(team.getRound());
        }
        for(Round round: rounds){
            if(memberScore.containsKey(round.getMemberName())){
                memberScore.replace(round.getMemberName(), memberScore.get(round.getMemberName())+ round.getFestival_score());
            }else{
                memberScore.put(round.getMemberName(), round.getFestival_score());
            }
        }
        Iterator<String> keys = memberScore.keySet().iterator();
        while(keys.hasNext()){
            String name = keys.next();
            MemberScore temp = new MemberScore(name, memberScore.get(name));
            memberScores.add(temp);
        }
        memberScores.sort(Comparator.comparing(MemberScore::getScore).reversed());

        return memberScores;

    }


}
