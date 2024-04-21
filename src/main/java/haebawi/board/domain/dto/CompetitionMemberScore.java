package haebawi.board.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompetitionMemberScore {
    String memberName;
    Integer successCount;
    Integer tries;
}
