package haebawi.board.domain.dto;

import haebawi.board.domain.entity.Competition;
import haebawi.board.domain.entity.Festival;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompetitionRequest {
    private Long id;
    private String name, description;
    private LocalDateTime createdAt, updatedAt, deletedAt, day;
    private int section_num;
    private int final_num;
    private int final_round_num;
    @Builder
    public CompetitionRequest(Long id, String name, LocalDateTime day,int section_num,int final_round_num, int final_num, String description){
        this.id = id;
        this.name = name;
        this.day = day;
        this.description = description;
        this.section_num = section_num;
        this.final_round_num = final_round_num;
        this.final_num = final_num;
    }

    public Competition toEntity(){
        return Competition.builder()
                .id(id)
                .name(name)
                .day(day)
                .final_num(final_num)
                .section_num(section_num)
                .description(description)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .section_num(2)
                .build();
    }
}
