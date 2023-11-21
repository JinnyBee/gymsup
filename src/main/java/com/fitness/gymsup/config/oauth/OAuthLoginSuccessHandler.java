package com.fitness.gymsup.config.oauth;

import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Service.OAuthUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;


@Slf4j
@Component
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    OAuthUserService oAuthUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String email = null;
        String oauthType = token.getAuthorizedClientRegistrationId();

        if ("kakao".equals(oauthType.toLowerCase())) {
            email = ((Map<String, Object>) token.getPrincipal().getAttribute("kakao_account")).get("email").toString();
        } else if ("google".equals(oauthType.toLowerCase())) {
            email = token.getPrincipal().getAttribute("email").toString();
        } else if ("naver".equals(oauthType.toLowerCase())) {
            email = ((Map<String, Object>) token.getPrincipal().getAttribute("response")).get("email").toString();
        }

        log.info("LOGIN SUCCESS : {} FROM {}", email, oauthType);

        String type =null;

        //Ouath2 자동 닉네임 설정
        if(oauthType.equals("kakao")){
            type = "카" + email;
        }else if (oauthType.equals("naver")){
            type = "네"+ email;
        }else if (oauthType.equals("google")){
            type = "구"+ email;
        }

        UserEntity user = oAuthUserService.getUserByEmailAndOAuthType(type, oauthType);

        log.info("USER SAVED IN SESSION");
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        super.onAuthenticationSuccess(request, response, authentication);

    }
}
