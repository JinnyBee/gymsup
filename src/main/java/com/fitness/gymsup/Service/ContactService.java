/*
    파일명 : ContactService.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
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

    //문의 등록
    public void contactRegister(ContactDTO contactDTO,
                                HttpServletRequest request,
                                Principal principal) throws Exception {

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

    //내 문의 보기
    public List<ContactDTO> userContact(HttpServletRequest request,
                                        Principal principal) throws Exception {

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

    //전체 문의 보기
    public Page<ContactDTO> contactList(Pageable page) throws Exception {

        int curPage = page.getPageNumber()-1;
        int pageLimit = 10;

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

    //문의 상세보기
    public ContactDTO contactDetail(int id) throws Exception {

        Optional<ContactEntity> contactEntity = contactRepository.findById(id);
        ContactDTO contactDTO = modelMapper.map(contactEntity, ContactDTO.class);

        return contactDTO;
    }

    //답변 등록
    public void adminContactRegister(String answer,
                                     boolean is_answer,
                                     int id) throws Exception {

        ContactEntity contactEntity = contactRepository.findById(id).orElseThrow();

        contactEntity.setAnswer(answer);
        contactEntity.set_answer(is_answer);

        contactRepository.save(contactEntity);
    }

    //문의 삭제
    public void contactDelete(int id) throws Exception {

        contactRepository.deleteById(id);
    }

    //유저 문의 수정
    public void userContactModify(ContactDTO contactDTO,
                                  int id) throws Exception {

        ContactEntity contactEntity = contactRepository.findById(id).orElseThrow();

        contactEntity.setTitle(contactDTO.getTitle());
        contactEntity.setContent(contactDTO.getContent());

        contactRepository.save(contactEntity);
    }

    public void userContactDelete(HttpServletRequest request,
                                  Principal principal) throws Exception {

        HttpSession session = request.getSession();
        UserEntity writer = (UserEntity) session.getAttribute("user");
        if(writer == null) {
            String email = principal.getName();
            writer = userRepository.findByEmail(email);
        }

        contactRepository.deleteAllByUserEntity(writer);
    }
}
