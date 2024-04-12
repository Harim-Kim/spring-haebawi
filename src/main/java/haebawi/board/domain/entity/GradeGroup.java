package haebawi.board.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> member = new ArrayList<>();

    @OneToMany(mappedBy = "gradeGroup", fetch = FetchType.EAGER, cascade = CascadeType.ALL) //mappedBy연관관계의 주인이 아니다(FK키가아니에요).
    @JsonIgnoreProperties({"gradeGroup"})
    @OrderBy("id desc")
    private List<Round> round = new ArrayList<>();;

}
