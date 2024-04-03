package haebawi.board.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;


/*
볼더링대회, 최야대 둘다 사용
-> 대회마다 section 수가 다름
-> 팀은 각 상위
Festival - section
         - team
Competition - section(2개로 구성)
            - grade_group
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where( clause = "deleted_at IS NULL")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=500, nullable = false)
    private String section_name;

    // user는 팀을 통해 등록?
    // 대회는 team 하나에 전부?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="GRADEGROUP_ID")
    private GradeGroup gradeGroup;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TEAM_ID")
    private Team Team;

    @OneToMany(mappedBy = "round", fetch = FetchType.EAGER, cascade = CascadeType.ALL) //mappedBy연관관계의 주인이 아니다(FK키가아니에요).
    @JsonIgnoreProperties({"round"})
    private List<Round> rounds = new ArrayList<>();;
}
