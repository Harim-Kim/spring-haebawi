package haebawi.board.domain.entity;


import haebawi.board.domain.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{3,12}$", message = "아이디를 3~12자로 입력해주세요. [특수문자 X]")
    private String loginId;

    @Column
    @NotBlank
    private String password;

    @Column
    @NotBlank
    private String nickname;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Board> boards;

//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private List<Round> rounds = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="GRADEGROUP_ID")
    private GradeGroup gradeGroup;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="TEAM_ID")
//    private Team team;

    private UserRole role;

    @Builder
    public User(String loginId, String password, String nickname){
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
    }

}
