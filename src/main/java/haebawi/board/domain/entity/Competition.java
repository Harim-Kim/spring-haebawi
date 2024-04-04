package haebawi.board.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
Competition - section(2개로 구성)
            - grade_group
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where( clause = "deleted_at IS NULL")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    // 게시판의 댓글 처럼 --> User 가 많이 등록되는 느낌으로 가고
    // user - competition - score는 어케 연결하지?
    @OneToMany(mappedBy = "competition", fetch = FetchType.EAGER, cascade = CascadeType.ALL) //mappedBy연관관계의 주인이 아니다(FK키가아니에요).
    @JsonIgnoreProperties({"competition"})
    private List<GradeGroup> gradeGroup = new ArrayList<>();;
}
