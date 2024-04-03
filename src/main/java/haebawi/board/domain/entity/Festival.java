package haebawi.board.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where( clause = "deleted_at IS NULL")
public class Festival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String description;
    // 게시판 댓글 처럼 유저
    // Score?
    // many to many? 아니면 한 개에 여러개 만들고
    // score entity -> com? fest? user_id? score 자체
    // 문제 등록은?
    // 최대 문제 갯수 -->
    @Column(nullable = false)
    private String name;
    @Column
    private LocalDateTime created_at;

    @Column
    private LocalDateTime updated_at;

    @Column
    private LocalDateTime deleted_at;

    @Column(nullable = false)
    private LocalDateTime day;

    // team one to many 느낌
    @OneToMany(mappedBy = "festival", fetch = FetchType.EAGER, cascade = CascadeType.ALL) //mappedBy연관관계의 주인이 아니다(FK키가아니에요).
    @JsonIgnoreProperties({"festival"})
    private List<Team> team = new ArrayList<>();;

}
