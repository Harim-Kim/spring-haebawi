package haebawi.board.service;

import haebawi.board.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final GradeGroupRepository gradeGroupRepository;
    private final RoundRepository roundRepository;



}
