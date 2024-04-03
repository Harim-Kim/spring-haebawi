package haebawi.board;


import haebawi.board.domain.UserRole;
import haebawi.board.domain.entity.Board;
import haebawi.board.domain.entity.User;
import haebawi.board.repository.BoardRepository;
import haebawi.board.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MakeInitData {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final BoardRepository boardRepository;

//    @PostConstruct
//    public void makeAdminAndUser(){
////        List<GrantedAuthority> authorities = new ArrayList<>();
////        authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.name()));
////        for(int i = 0; i < 5; i ++){
////            String userName = "admin"+Integer.toString(i+1);
////            String password = "P!ssw0rd";
////            String nickname = "관리자"+Integer.toString(i+1);
////            User user = new User(userName, password, nickname);
////            Authentication authentication = new UsernamePasswordAuthenticationToken(user, password,authorities);
////            SecurityContextHolder.getContext().setAuthentication(authentication);
////            System.out.println("!!!!!!!!!!!!관리자"+ userName+password+nickname);
////            userRepository.save(user);
////        }
//
//
//        User admin1 = User.builder()
//                .loginId("admin1")
//                .password(encoder.encode("P!ssw0rd"))
//                .nickname("관리자1")
//                .role(UserRole.ADMIN)
//                .build();
//        userRepository.save(admin1);
//
//        User admin2 = User.builder()
//                .loginId("admin2")
//                .password(encoder.encode("P!ssw0rd"))
//                .nickname("관리자2")
//                .role(UserRole.ADMIN)
//                .build();
//        userRepository.save(admin2);
//
//        User admin3 = User.builder()
//                .loginId("admin3")
//                .password(encoder.encode("P!ssw0rd"))
//                .nickname("관리자3")
//                .role(UserRole.ADMIN)
//                .build();
//        userRepository.save(admin3);
//
//        User admin4 = User.builder()
//                .loginId("admin4")
//                .password(encoder.encode("P!ssw0rd"))
//                .nickname("관리자4")
//                .role(UserRole.ADMIN)
//                .build();
//        userRepository.save(admin4);
//
//        User admin5 = User.builder()
//                .loginId("admin5")
//                .password(encoder.encode("P!ssw0rd"))
//                .nickname("관리자4")
//                .role(UserRole.ADMIN)
//                .build();
//        userRepository.save(admin5);
//
//        User user1 = User.builder()
//                .loginId("user1")
//                .password(encoder.encode("P!ssw0rd"))
//                .nickname("유저1")
//                .role(UserRole.USER)
//                .build();
//        userRepository.save(user1);
//
//        User user2 = User.builder()
//                .loginId("user2")
//                .password(encoder.encode("P!ssw0rd"))
//                .nickname("유저2")
//                .role(UserRole.USER)
//                .build();
//        userRepository.save(user2);
//
//        for(int i=0; i<10; i++){
//            boardRepository.save(
//                    Board.builder()
//                            .user(user1)
//                            .title(Integer.toString(i))
//                            .content(Integer.toString(i))
//                            .build()
//            );
//        }
//
//    }


}
