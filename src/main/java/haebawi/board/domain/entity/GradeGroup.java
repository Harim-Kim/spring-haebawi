package haebawi.board.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

/*
team이랑 같지만 논리적 구분

 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where( clause = "deleted_at IS NULL")
public class GradeGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column
    private String grade_name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="COMPETITION_ID")
    private Competition competition;

    @OneToMany(mappedBy = "gradeGroup", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "gradeGroup", fetch = FetchType.LAZY)
    private List<Section> sections = new ArrayList<>();


}
