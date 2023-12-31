/*
    파일명 : OAuthUserService.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Service;

import com.fitness.gymsup.Constant.UserRole;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();
        log.info("ATTR INFO : {}",attributes.toString());

        String email = null;
        String oauthType = userRequest.getClientRegistration().getRegistrationId();

        OAuth2User user2 = super.loadUser(userRequest);

        if("kakao".equals(oauthType.toLowerCase())) {
            email = ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
        } else if ("google".equals(oauthType.toLowerCase())) {
            email = attributes.get("email").toString();
        } else if("naver".equals(oauthType.toLowerCase())) {
            email = ((Map<String, Object>) attributes.get("response")).get("email").toString();
        }

        String nickType =null;

        //Ouath2 자동 닉네임 설정
        if(oauthType.equals("kakao")) {
            nickType = "카" + email;
        } else if (oauthType.equals("naver")) {
            nickType = "네"+ email;
        } else if (oauthType.equals("google")) {
            nickType = "구"+ email;
        }

        String mailType = null;

        //이메일 중복 방지 설정
        if(oauthType.equals("kakao")) {
            mailType = "(kakao)" + email;
        } else if (oauthType.equals("naver")) {
            mailType = "(naver)" + email;
        } else if (oauthType.equals("google")) {
            mailType = "(google)" + email;
        }

        if (getUserByEmailAndOAuthType(mailType, oauthType) == null) {
            UserEntity user = new UserEntity();
            user.setEmail(mailType);
            user.setNickname(nickType);
            user.setOauthType(oauthType);
            user.setRole(UserRole.USER);
            save(user);
        }

        return super.loadUser(userRequest);
    }

    //저장
    public void save(UserEntity user){
        userRepository.save(user);
    }

    //조회
    public UserEntity getUserByEmailAndOAuthType(String email,
                                                 String oauthType) {

        return userRepository.findByEmailAndOauthType(email, oauthType).orElse(null);
    }
}
