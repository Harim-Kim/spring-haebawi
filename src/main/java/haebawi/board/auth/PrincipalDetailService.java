package haebawi.board.auth;

import haebawi.board.domain.UserRole;
import haebawi.board.domain.entity.User;
import haebawi.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
                });
        return new PrincipalDetails(user);
    }

    @Transactional
    public boolean join(String userId, String password, String nickname, UserRole role){

        Optional<User> user = userRepository.findByLoginId(userId);
        if (!user.isEmpty()){
            return false;
        }
        User.builder()
                .loginId(userId)
                .password(encoder.encode(password))
                .nickname(nickname)
                .role(role)
                .build();
        return true;
    }


}
