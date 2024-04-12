package haebawi.board.config;

import haebawi.board.domain.UserRole;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
//@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFailureHandler customFailureHandler;
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/user/info").authenticated()
                        .requestMatchers("/festival/**").authenticated()
                        .requestMatchers("/board/**").authenticated()
                        .requestMatchers("/user/admin/**").hasAnyAuthority(UserRole.ADMIN.name())
                        .anyRequest().permitAll()
                )
                .formLogin( form -> form
                        .loginPage("/user/login")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .permitAll()
                        .failureUrl("/user/login")
                        .failureHandler(customFailureHandler)
                )
                .logout( logout -> logout
                        .permitAll()
                        .invalidateHttpSession(true)
//                        .logoutSuccessUrl("/")
//                        .logoutUrl("/logout")
                        .addLogoutHandler(((request, response, authentication) -> {
                            HttpSession session = request.getSession();
                            if(session != null){
                                session.invalidate();
                            }
                        }))
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            response.sendRedirect("/");
                        }))
                        .deleteCookies("JSESSIONID")

                )
                .csrf().disable()
        ;

        return http.build();
    }
}
