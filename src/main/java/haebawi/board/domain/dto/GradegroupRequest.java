package haebawi.board.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradegroupRequest {

    private Long id;
    private String grade_name;
    private Long competitionId;
    private String user;
}
