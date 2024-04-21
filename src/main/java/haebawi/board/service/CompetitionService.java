package haebawi.board.service;

import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.*;
import haebawi.board.domain.entity.*;
import haebawi.board.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final GradeGroupRepository gradeGroupRepository;
    private final RoundRepository roundRepository;
    @Transactional
    public Page<Competition> CompetitionList(Pageable pageable){
        return competitionRepository.findAll(pageable);
    }
    @Transactional
    public Long save(User user, CompetitionRequest request){

        if (user.getRole() != UserRole.ADMIN){
            return null;
        }
        Competition competition = request.toEntity();
        competitionRepository.save(competition);

        return competition.getId();

    }

    public Competition getCompetitionById(Long competitionId){
        if (competitionId == null) return null;

        return competitionRepository.findById(competitionId)
                .orElseThrow(()->{
                    return new IllegalArgumentException("최야대 상세 보기 실패");
                });
    }

    @Transactional
    public Long update(Long competitionId, CompetitionRequest competitionRequest){
        Optional<Competition> optionalCompetition = competitionRepository.findById(competitionId);
        if (optionalCompetition.isEmpty()){
            return -1L;
        }
        Competition competition = optionalCompetition.get();
        competition.setUpdated_at(LocalDateTime.now());
        competition.setName(competitionRequest.getName());
        competitionRepository.save(competition);

        return competitionId;
    }
    @Transactional
    public Long delete(Long competitionId){
        Optional<Competition> optionalCompetition = competitionRepository.findById(competitionId);
        if (optionalCompetition.isEmpty()){
            return -1L;
        }
        Competition competition  = optionalCompetition.get();
        competition.setDeleted_at(LocalDateTime.now());
        competitionRepository.save(competition);
        return competitionId;
    }

    @Transactional
    public void gradegroupSave(GradegroupRequest gradegroupRequest){
        Competition competition = competitionRepository.findById(gradegroupRequest.getCompetitionId()).orElseThrow(()->{
            return new IllegalArgumentException("gradegroup 생성 실패: 대회 Id를 찾을 수 없습니다.");
        });

        String[] members = gradegroupRequest.getUser().split(",");
        // team 생성
        GradeGroup gradeGroup = GradeGroup.builder()
                .grade_name(gradegroupRequest.getGrade_name())
                .competition(competition)
                .member(Arrays.asList(members))
                .build();
        gradeGroupRepository.save(gradeGroup);
        // 팀원 수에 맞게 round 생성
        int section_num = competition.getSection_num();
        for(int i = 1; i < section_num+1; i++){ //예선 문제 수.
            for(String member : members){
                Round round = Round.builder()
                        .sectionNum(Integer.toUnsignedLong(i))
                        .tries(0)
                        .success(false)
                        .isFinal(false)
                        .memberName(member)
                        .gradeGroup(gradeGroup)
                        .build();
                roundRepository.save(round);

            }
        }

    }
    @Transactional
    public GradeGroup gradeGroup(Long gradegroupId){
        GradeGroup gradeGroup = gradeGroupRepository.findById(gradegroupId).orElseThrow(()->{
            return new IllegalArgumentException("해당 난이도를 찾을 수 없습니다.");
        });
        return gradeGroup;
    }

    @Transactional
    public void createFinal(Long competitionId){
        Competition competition = competitionRepository.findById(competitionId).orElseThrow(()->{
            return new IllegalArgumentException("gradegroup 생성 실패: 대회 Id를 찾을 수 없습니다.");
        });

        // team 생성
        Map<GradeGroup, List<CompetitionMemberScore>> finals = GetBestMemberGrade(competition);

        // 팀원 수에 맞게 round 생성
        for(GradeGroup gradeGroup : finals.keySet()){
            for (CompetitionMemberScore memberScore : finals.get(gradeGroup)){
                for(int i=1; i < competition.getFinal_round_num()+1; i++){
                    Round round = Round.builder()
                            .sectionNum(Integer.toUnsignedLong(competition.getSection_num())+i) // 결승 순서
                            .tries(0)
                            .success(false)
                            .memberName(memberScore.getMemberName())
                            .gradeGroup(gradeGroup)
                            .isFinal(true)
                            .build();
                    roundRepository.save(round);
                }
            }
        }
    }
    @Transactional
    public int scoreUpdate(Map<String, String> data, Long gradegroupId, Long competitionId){
        GradeGroup gradeGroup = gradeGroupRepository.findById(gradegroupId).orElseThrow(()->{
            return new IllegalArgumentException("해당 팀을 찾을 수 없습니다.");
        });
        // 한번에 받아서 처리함
        for (Map.Entry<String, String> entrySet : data.entrySet()){
            String[] section_index = entrySet.getKey().split("-");
            String memberIndex = section_index[1];
            String sectionNum = section_index[0];
            String st = section_index[2];
            String member;
            Round round;
            int tries;
            boolean success;
            if(st.equals("0")) {
                try{
                    success = Boolean.parseBoolean(entrySet.getValue());
                    member = gradeGroup.getMember().get(Integer.parseInt(memberIndex));
                    round = roundRepository.findByGradeGroupIdAndSectionNumAndMemberName(
                            gradegroupId,
                            Integer.parseInt(sectionNum),
                            member
                    ).orElseThrow(()->{
                        return new IllegalArgumentException("해당 점수표를 찾을 수 없습니다.");
                    });
                    round.setSuccess(success);
                    roundRepository.save(round);
                }catch (Exception e){
                    e.printStackTrace();
                    return 0;
                }
            }else{
                try{
                    tries = Integer.parseInt(entrySet.getValue());
                    member = gradeGroup.getMember().get(Integer.parseInt(memberIndex));
                    round = roundRepository.findByGradeGroupIdAndSectionNumAndMemberName(
                            gradegroupId,
                            Integer.parseInt(sectionNum),
                            member
                    ).orElseThrow(()->{
                        return new IllegalArgumentException("해당 점수표를 찾을 수 없습니다.");
                    });
                    round.setTries(tries);
                    roundRepository.save(round);
                }catch (Exception e){
                    e.printStackTrace();
                    return 0;
                }
            }



        }
        return 1;
    }

//    public Map<GradeGroup, List<CompetitionMemberScore>>  GetGradeGroupScoreOrder(Competition competition){
//        List<GradeGroup> gradeGroups = competition.getGradeGroup();
////        gradeGroups.sort(Comparator.comparing(GradeGroup::))
//        Map<GradeGroup, List<CompetitionMemberScore>> gradeGroupListMapOrderbyScore = new HashMap<>();
//        for(GradeGroup gradeGroup : gradeGroups){
//            List<Round> rounds = gradeGroup.getRound();
//            List<CompetitionMemberScore> temp1 = new ArrayList<>(); Map으로 만들어서 사람 있는지 확인하고, try랑 success(success인 경우에만)
//            for(Round round : rounds){
//                CompetitionMemberScore temp2 = new CompetitionMemberScore(round.getMemberName(),);
//                temp2.setMemberName();
//                tem
//            }
//        }
//    }
    public List<Team> GetBestTeam(Festival festival){
        List<Team> bestTeamList = festival.getTeam();
        bestTeamList.sort(Comparator.comparing(Team::GetTeamScore).reversed());
        return bestTeamList;
    }


    public Map<GradeGroup, List<CompetitionMemberScore>>  GetBestMemberGrade(Competition competition){
        Map<GradeGroup, List<CompetitionMemberScore>> finals = new HashMap<>();

        for(GradeGroup gradeGroup: competition.getGradeGroup()) {

            Map<String, List<Round>> memberScore = new HashMap<>();
            for(Round round: gradeGroup.getRound()){
                if(memberScore.containsKey(round.getMemberName()) && round.isSuccess()){
                    memberScore.get(round.getMemberName()).add(round);
                }else{
                    if(round.isSuccess()){
                        List<Round> temp = new ArrayList<>();
                        temp.add(round);
                        memberScore.put(round.getMemberName(), temp);
                    }
                }
            }
            List<CompetitionMemberScore> competitionMemberScores = new ArrayList<>();
            for(String member : memberScore.keySet()){
                int successCount = memberScore.get(member).size();
                int tries = 0;
                for(Round round: memberScore.get(member)){
                    tries += round.getTries();
                }
                CompetitionMemberScore temp = new CompetitionMemberScore(member,successCount,tries);
                competitionMemberScores.add(temp);
            }
            //여기서 sort하고 final에 담기.
            competitionOrder(competitionMemberScores);
            competitionMemberScores.subList(0,competition.getFinal_num());
            finals.put(gradeGroup, competitionMemberScores);
        }


        return finals;

    }

    private static void competitionOrder(List<CompetitionMemberScore> competitionMemberScores){
        Collections.sort(competitionMemberScores, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Integer success1 = ((CompetitionMemberScore) o1).getSuccessCount();
                Integer success2 = ((CompetitionMemberScore) o2).getSuccessCount();
                int sComp = success1.compareTo(success2);

                if (sComp != 0){
                    return sComp;
                }

                Integer tries1 = ((CompetitionMemberScore) o1).getTries()*(-1);
                Integer tries2 = ((CompetitionMemberScore) o2).getTries()*(-1);
                return tries1.compareTo(tries2);
            }
        });
    }
}
