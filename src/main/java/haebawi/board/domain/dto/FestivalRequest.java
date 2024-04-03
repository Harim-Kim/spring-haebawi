package haebawi.board.domain.dto;

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
public class FestivalRequest {
    private Long id;
    private String name, description;
    private LocalDateTime createdAt, updatedAt, deletedAt, day;

    @Builder
    public FestivalRequest(Long id, String name, LocalDateTime createdAt, LocalDateTime day, String description){
        this.id = id;
        this.name = name;
        this.day = day;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Festival toEntity(){
        return Festival.builder()
                .id(id)
                .name(name)
                .day(day)
                .description(description)
                .created_at(LocalDateTime.now())
                .build();
    }
}
