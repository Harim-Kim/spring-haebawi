package haebawi.board.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

/*
볼더링 문제로 보면됨.
user_id 들어가야함
트라이, 성공 여부
Team - User - 유저당 10개 라운드 이런식
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Where( clause = "deleted_at IS NULL")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
    private int tries;

    @Column
    private boolean success;

    @Column
    private int festival_score;


    @Column
    private Long user_id;

    @Column
    private String memberName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TEAM_ID")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="GRADEGROUP_ID")
    private GradeGroup gradeGroup;

    @Column
    private Long sectionNum;

    @Column
    @Builder.Default()
    private boolean isFinal = false;

}
