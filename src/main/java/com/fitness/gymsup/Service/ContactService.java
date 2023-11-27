package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.ContactDTO;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.ContactEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.ContactRepository;
import com.fitness.gymsup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public void contactRegister(ContactDTO contactDTO, HttpServletRequest request, Principal principal){
        ContactEntity contactEntity = modelMapper.map(contactDTO, ContactEntity.class);

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        contactEntity.setUserEntity(user);
        contactEntity.set_answer(false);

        ContactEntity result  = contactRepository.save(contactEntity);
    }
}
