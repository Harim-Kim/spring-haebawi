package haebawi.board.service;


import haebawi.board.domain.UserRole;
import haebawi.board.domain.dto.JoinRequest;
import haebawi.board.domain.dto.LoginRequest;
import haebawi.board.domain.entity.User;
import haebawi.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // Spring Security를 사용한 로그인 구현 시 사용
     private final BCryptPasswordEncoder encoder;

    /**
     * loginId 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    /**
     * nickname 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 회원가입 기능 1
     * 화면에서 JoinRequest(loginId, password, nickname)을 입력받아 User로 변환 후 저장
     * loginId, nickname 중복 체크는 Controller에서 진행 => 에러 메세지 출력을 위해
     */
    public void join(JoinRequest req) {
        userRepository.save(req.toEntity());
    }

    /**
     * 회원가입 기능 2
     * 화면에서 JoinRequest(loginId, password, nickname)을 입력받아 User로 변환 후 저장
     * 회원가입 1과는 달리 비밀번호를 암호화해서 저장
     * loginId, nickname 중복 체크는 Controller에서 진행 => 에러 메세지 출력을 위해
     */
    public void join2(JoinRequest req) {
        userRepository.save(req.toEntity(encoder.encode(req.getPassword())));
    }

    /**
     *  로그인 기능
     *  화면에서 LoginRequest(loginId, password)을 입력받아 loginId와 password가 일치하면 User return
     *  loginId가 존재하지 않거나 password가 일치하지 않으면 null return
     */
    public User login(LoginRequest req) {
        Optional<User> optionalUser = userRepository.findByLoginId(req.getLoginId());

        // loginId와 일치하는 User가 없으면 null return
        if(optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // 찾아온 User의 password와 입력된 password가 다르면 null return
        if(!user.getPassword().equals(req.getPassword())) {
            return null;
        }

        return user;
    }

    /**
     * userId(Long)를 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * userId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * userId로 찾아온 User가 존재하면 User return
     */
    public User getLoginUserById(Long userId) {
        if(userId == null) return null;

        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    /**
     * loginId(String)를 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * loginId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * loginId로 찾아온 User가 존재하면 User return
     */
    public User getLoginUserByLoginId(String loginId) {
        if(loginId == null) return null;

        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    public List<User> findAll(){ return userRepository.findAll(); }

    public int updateRole(Map<String, String> data){
        for (Map.Entry<String, String> entrySet : data.entrySet()){
            String[] userData = entrySet.getKey().split("-");
            String user_id = userData[1];
            String role = entrySet.getValue();
            if (role.isEmpty()){
                continue;
            }
            long userId;
            try{
                userId = Long.parseLong(user_id);
                User user = userRepository.findById(userId).orElseThrow(()->{
                    return new IllegalArgumentException("해당 유저를 찾을 수 없습니다.");
                });
                UserRole newRole = UserRole.valueOf(role);
                user.setRole(newRole);
                userRepository.save(user);

            }catch (Exception e){
                e.printStackTrace();
                return 1;
            }
        }
        return 0;
    }
}
