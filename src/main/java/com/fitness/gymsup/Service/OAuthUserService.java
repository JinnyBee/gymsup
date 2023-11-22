package com.fitness.gymsup.Service;

import com.fitness.gymsup.Constant.Role;
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
    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException{
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();
        log.info("ATTR INFO : {}",attributes.toString());

        String email = null;
        String oauthType = userRequest.getClientRegistration().getRegistrationId();

        OAuth2User user2 = super.loadUser(userRequest);

        if("kakao".equals(oauthType.toLowerCase())){
            email = ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
        }else if ("google".equals(oauthType.toLowerCase())){
            email = attributes.get("email").toString();
        }else if("naver".equals(oauthType.toLowerCase())){
            email = ((Map<String, Object>) attributes.get("response")).get("email").toString();
        }


        String type =null;

        //Ouath2 자동 닉네임 설정
        if(oauthType.equals("kakao")){
            type = "카" + email;
        }else if (oauthType.equals("naver")){
            type = "네"+ email;
        }else if (oauthType.equals("google")){
            type = "구"+ email;
        }
        log.info("======================================================");



        if (getUserByEmailAndOAuthType(type, oauthType) == null){
            UserEntity user = new UserEntity();
            user.setEmail(type);
            user.setNickname(type);
            user.setOauthType(oauthType);
            user.setRole(Role.USER);
            save(user);
        }

        return super.loadUser(userRequest);
    }

    public void save(UserEntity user){
        userRepository.save(user);
    }

    public UserEntity getUserByEmailAndOAuthType(String email, String oauthType){
        return userRepository.findByEmailAndOauthType(email, oauthType).orElse(null);
    }
}
