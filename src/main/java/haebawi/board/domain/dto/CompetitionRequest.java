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

    @Builder
    public CompetitionRequest(Long id, String name, LocalDateTime day, String description){
        this.id = id;
        this.name = name;
        this.day = day;
        this.description = description;
    }

    public Competition toEntity(){
        return Competition.builder()
                .id(id)
                .name(name)
                .day(day)
                .description(description)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .section_num(2)
                .build();
    }
}
