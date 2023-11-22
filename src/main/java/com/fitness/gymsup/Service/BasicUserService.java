package com.fitness.gymsup.Service;

import com.fitness.gymsup.Constant.UserRole;
import com.fitness.gymsup.DTO.UserDTO;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;

    public void saveMember(UserDTO userDTO){
        String password = passwordEncoder.encode(userDTO.getPassword());
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setPassword(password);
        userEntity.setRole(UserRole.USER);

        validateDuplicateUser(userEntity);
        userRepository.save(userEntity);
    }

    private void validateDuplicateUser(UserEntity userEntity){
        UserEntity findUser = userRepository.findByEmail(userEntity.getEmail());
        if(findUser != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null){
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().toString())
                .build();
    }

    public UserEntity bringUserInfo(HttpServletRequest request,Principal principal)throws Exception{
        HttpSession session = request.getSession();
        UserEntity userEntity = (UserEntity) session.getAttribute("user");

        if (userEntity != null){
            return userEntity;
        }else{
            String email = principal.getName();
            userEntity = userRepository.findByEmail(email);
            return userEntity;
        }
    }

    public String dupNickname(UserDTO userDTO) throws Exception{
        String nickname = userDTO.getNickname();
        String message="";
        if (nickname.length() != 0){
            Long dup = userRepository.countByNickname(nickname);
            if (dup == 1){
                message = "닉네임이 중복입니다.";
            }else if(dup==0){
                message = "닉네임 사용이 가능합니다.";
            }
        }else{
            message = "닉네임을 입력해 주세요.";
        }
        return message;
    }


    public String dupEmail(UserDTO userDTO)throws Exception{
        String email = userDTO.getEmail();
        String emessage="";
        if (email.length() != 0){
            Long dup = userRepository.countByEmail(email);
            if (dup == 1){
                emessage = "이메일이 중복입니다.";
            }else if(dup==0){
                emessage = "이메일 사용이 가능합니다.";
            }
        }else{
            emessage = "이메일을 입력해 주세요.";
        }
        return emessage;
    }


}
