package haebawi.board.domain.dto;


import haebawi.board.domain.entity.User;
import haebawi.board.domain.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardRequest {

    private Long id;
    private String title;
    private String author;
    private String content;
    private LocalDateTime createdAt, updatedAt, deletedAt;
    private int view;
    private User user;

    @Builder
    public BoardRequest(Long id, String title, String content, LocalDateTime createdAt, User user){
        this.id = id;
        this.title=title;
        this.content=content;
        this.createdAt=createdAt;
        this.user=user;
    }

    public Board toEntity(){
        return Board.builder()
                .id(id)
                .title(title)
                .author(author)
                .content(content)
                .created_at(LocalDateTime.now())
                .view(0)
                .user(user)
                .build();
    }


}
