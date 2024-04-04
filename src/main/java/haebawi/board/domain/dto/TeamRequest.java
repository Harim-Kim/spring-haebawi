package haebawi.board.domain.dto;

import haebawi.board.domain.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequest {

    private Long id;
    private String team_name;
    private Long festivalId;
    private String user;


}
