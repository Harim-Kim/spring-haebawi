package haebawi.board.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreInputFestivalRequest {
    private int score;
    private Long teamId;
    private Long festivalId;
    private int sectionNum;
    private int indexNum;
}
