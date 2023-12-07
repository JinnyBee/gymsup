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

            //모든권한
            auth.antMatchers("/","/user_login", "/user_login_error", "/user_join","/board_list", "/board_diary_list",
                                        "/machine_detect", "/board_notify_list", "/board_qna_list", "/board_tip_list",
                                        "/password_change", "/user_entry_error", "/user_regDupNickname" ,"/user_regDupEmail").permitAll();

            //유저권한
            auth.antMatchers("/user_logout", "/board_detail*", "/bookmark_*", "/good_on*", "/mybmi_calc*",
                                        "/food_calorie_*", "/myfood_calorie_*", "/exercise_calorie_*",
                                        "/comment_*", "/reply_*", "/board_diary_*", "/board_notify_detail*",
                                        "/board_qna_*", "/board_tip_*" ,"/user_*", "/contact_*", "/machine_about", "/machine_select_list*",
                                        "/machine_detail*").hasAnyRole("USER", "ADMIN");
            //관리자 권한
            auth.antMatchers("/machine_detect", "/machine_*", "/admin_*", "/board_notify_*").hasRole("ADMIN");
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
