package haebawi.board.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Where( clause = "deleted_at IS NULL")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
    private String team_name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="FESTIVAL_ID")
    private Festival festival;

//    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY) // list 형태도 생각해봐야함.
//    private List<User> users = new ArrayList<>();
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> user;

//    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JsonIgnoreProperties({"team"})
//    @OrderBy("id desc")
//    private List<Section> section = new ArrayList<>();

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = CascadeType.ALL) //mappedBy연관관계의 주인이 아니다(FK키가아니에요).
    @JsonIgnoreProperties({"team"})
    @OrderBy("id desc")
    private List<Round> round = new ArrayList<>();;


    public List<Round> GetScoreSection(Long sectionNum){
        List<Round> scores = new ArrayList<>();

        for(Round r : this.round){
            if(r.getSectionNum().equals(sectionNum)){
                scores.add(r);
            }
        }
        Collections.reverse(scores);
        return scores;
    }

    public int GetTeamScore(){
        int ret = 0;
        for(Round r : this.round) {
            ret += r.getFestival_score();
        }
        return ret;
    }
}

