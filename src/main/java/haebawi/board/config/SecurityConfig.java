package haebawi.board.config;

import haebawi.board.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
        http
                .authorizeHttpRequests().requestMatchers("/security-login/info").authenticated()
                .requestMatchers("/security-login/admin").hasAnyAuthority(UserRole.ADMIN.name()) // 여기까지 authentication 필요
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .usernameParameter("loginId")
                .passwordParameter("password")
                .loginPage("/security-login/login")
                .defaultSuccessUrl("/security-login")
                .failureUrl("/security-login/login");
        http.logout().logoutUrl("/security-login/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID");

        return http.build();
    }
}
