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
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
    private final OAuthUserService oAuthUserService;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests((auth)->{
            auth.antMatchers("/","/user_login", "/user_join") .permitAll();
            auth.antMatchers("/user_logout").hasRole("USER");
            auth.antMatchers("/user_logout").hasRole("ADMIN");
            auth.antMatchers("/board_all_detail", "/board_notify_detail",
                                        "/board_tip_detail", "/board_qna_detail",
                                        "/board_diary_detail").hasRole("USER");
        });



        http.formLogin()
                .loginPage("/user_login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/user_login_error");
        http.csrf().disable();
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/user_logout"))
                .logoutSuccessUrl("/");

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
