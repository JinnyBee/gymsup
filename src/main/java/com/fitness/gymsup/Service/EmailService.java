/*
    파일명 : EmailService.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.MailDTO;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public MailDTO createMailAndChangePassword(String email) {

        String str = getTempPassword();
        MailDTO dto = new MailDTO();

        dto.setAddress(email);
        dto.setTitle(email+"님의 GYMsup 임시비밀번호 안내 이메일 입니다.");
        dto.setMessage("안녕하세요. GYMsup 임시비밀번호 안내 관련 이메일 입니다." + "[" + email + "]" +"님의 임시 비밀번호는 "
                + str + " 입니다.");

        updatePassword(str, email);

        return dto;
    }

    public void updatePassword(String str,
                               String email) {

        String pw = passwordEncoder.encode(str);
        UserEntity user = userRepository.findByEmail(email);
        user.setPassword(pw);

        userRepository.save(user);
    }

    public String getTempPassword() {

        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }

        return str;
    }

    public void mailSend(MailDTO mailDTO) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mailDTO.getAddress());
        message.setSubject(mailDTO.getTitle());
        message.setText(mailDTO.getMessage());

        mailSender.send(message);
    }
}
