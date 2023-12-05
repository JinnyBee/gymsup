package com.fitness.gymsup.Config;

import com.fitness.gymsup.Config.oauth.OAuthLoginFailureHandler;
import com.fitness.gymsup.Config.oauth.OAuthLoginSuccessHandler;
import com.fitness.gymsup.Service.OAuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //간편로그인 성공 헨들러
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    //간편로그인 실패 헨들러
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;

    //간편로그인 서비스
    private final OAuthUserService oAuthUserService;

    //권한이 없을때 예외처리
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    //비로그인일떄 예외처리
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    PasswordEncoder passwordEncoder(){
        //비밀번호 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        //유저, 관리자 사이트 권한 설정
        http.authorizeHttpRequests((auth)->{
            auth.antMatchers("/","/user_login", "/user_join").permitAll();
            auth.antMatchers("/user_logout").hasRole("USER");
            auth.antMatchers("/user_logout", "/admin_detail").hasRole("ADMIN");
            auth.antMatchers("/board_all_detail", "/board_notify_detail",
                                        "/board_tip_detail", "/board_qna_detail",
                                        "/board_diary_detail", "/machine_about",
                                        "/board_tip_register", "/board_diary_register",
                                        "/board_qna_register", "/board_notify_register").hasAnyRole("USER","ADMIN");
        });


        //유저 로그인 설정
        http.formLogin()
                .loginPage("/user_login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/user_login_error");
        http.csrf().disable();

        //유저 로그아웃 설정
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/user_logout"))
                .logoutSuccessUrl("/");

        //권한이 없거나 비로그인 예외처리
        http.exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint);

        //간편로그인(카카오, 구글, 네이버) 설정
        http.oauth2Login()
                .loginPage("/user_login")
                .authorizationEndpoint()
                .and()
                .successHandler(oAuthLoginSuccessHandler)
                .failureHandler(oAuthLoginFailureHandler)
                .userInfoEndpoint()
                .userService(oAuthUserService);
        return http.build();
    }
}
