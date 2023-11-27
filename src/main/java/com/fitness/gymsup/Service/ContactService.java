package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.ContactDTO;
import com.fitness.gymsup.DTO.ContactDTO;
import com.fitness.gymsup.Entity.ContactEntity;
import com.fitness.gymsup.Entity.ContactEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.ContactRepository;
import com.fitness.gymsup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        contactEntity.setAnswer("");

        ContactEntity result  = contactRepository.save(contactEntity);
    }

    public List<ContactDTO> userContact(HttpServletRequest request, Principal principal)throws Exception{

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        List<ContactEntity> contactEntities = contactRepository.findAllByUserEntity(user);
        List<ContactDTO> contactDTOS = Arrays.asList(modelMapper.map(contactEntities, ContactDTO[].class));

        return contactDTOS;
    }
    
    public Page<ContactDTO> contactList(Pageable page) throws Exception{
        int curPage = page.getPageNumber()-1;
        int pageLimit = 5;

        Pageable pageable = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "id"));

        Page<ContactEntity> contactEntities = contactRepository.findAll(pageable);
        Page<ContactDTO> contactDTOS = contactEntities.map(data->ContactDTO.builder()
                .id(data.getId())
                .userNickname(data.getUserEntity().getNickname())
                .userId(data.getUserEntity().getId())
                .title(data.getTitle())
                .content(data.getContent())
                .answer(data.getAnswer())
                .is_answer(data.is_answer())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build()
        );

        return contactDTOS;
    }

    public ContactDTO contactDetail(int id)throws Exception{
        Optional<ContactEntity> contactEntity = contactRepository.findById(id);

        ContactDTO contactDTO = modelMapper.map(contactEntity, ContactDTO.class);

        return contactDTO;
    }

    public void adminContactRegister(String answer, boolean is_answer,int id)throws Exception{
        ContactEntity contactEntity = contactRepository.findById(id).orElseThrow();
        contactEntity.setAnswer(answer);
        contactEntity.set_answer(is_answer);
        contactRepository.save(contactEntity);
    }
}
