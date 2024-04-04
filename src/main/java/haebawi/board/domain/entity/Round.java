package haebawi.board.domain.entity;


import jakarta.persistence.*;
import lombok.*;
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="user_id")
//    private User user;

    @Column
    private Long user_id;

    @Column
    private String member_name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TEAM_ID")
    private Team team;

    @Column
    private Long section_id;

}
