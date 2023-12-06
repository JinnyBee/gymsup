package com.fitness.gymsup.Service;

import com.fitness.gymsup.Constant.UserRole;
import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.DTO.UserDTO;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.CommentEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.BoardRepository;
import com.fitness.gymsup.Repository.CommentRepository;
import com.fitness.gymsup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public void saveMember(UserDTO userDTO) {
        String password = passwordEncoder.encode(userDTO.getPassword());
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setPassword(password);
        userEntity.setRole(UserRole.USER);

        validateDuplicateUser(userEntity);
        userRepository.save(userEntity);
    }

    //중복체크
    private void validateDuplicateUser(UserEntity userEntity) {
        UserEntity findUser = userRepository.findByEmail(userEntity.getEmail());
        if (findUser != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().toString())
                .build();
    }

    //접속 중인 유저 정보 (마이페이지)
    public UserEntity bringUserInfo(HttpServletRequest request, Principal principal) throws Exception {
        HttpSession session = request.getSession();
        UserEntity userEntity = (UserEntity) session.getAttribute("user");

        if (userEntity != null) {
            return userEntity;
        } else {
            String email = principal.getName();
            userEntity = userRepository.findByEmail(email);
            return userEntity;
        }
    }

    //닉네임 중복 체크
    public String dupNickname(UserDTO userDTO) throws Exception {
        String nickname = userDTO.getNickname();
        String message = "";
        if (nickname.length() != 0) {
            Long dup = userRepository.countByNickname(nickname);
            if (dup == 1) {
                message = "닉네임이 중복입니다.";
            } else if (dup == 0) {
                message = "닉네임 사용이 가능합니다.";
            }
        } else {
            message = "닉네임을 입력해 주세요.";
        }
        return message;
    }

    //이메일 중복체크
    public String dupEmail(UserDTO userDTO) throws Exception {
        String email = userDTO.getEmail();
        String emessage = "";
        if (email.length() != 0) {
            Long dup = userRepository.countByEmail(email);
            if (dup == 1) {
                emessage = "이메일이 중복입니다.";
            } else if (dup == 0) {
                emessage = "이메일 사용이 가능합니다.";
            }
        } else {
            emessage = "이메일을 입력해 주세요.";
        }
        return emessage;
    }

    //내가 쓴 글 조회
    public Page<BoardDTO> myWrite(Pageable page, HttpServletRequest request, Principal principal) throws Exception {
        int curPage = page.getPageNumber() - 1;
        int pageLimit = 10;

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        Pageable pageable = PageRequest.of
                (curPage, pageLimit, Sort.by(Sort.Direction.DESC, "id"));
        Page<BoardEntity> boardEntities = boardRepository.findAllByUserEntity(pageable, user);
        Page<BoardDTO> boardDTOS = boardEntities.map(data -> BoardDTO.builder()
                .id(data.getId())
                .categoryType(data.getCategoryType())
                .userId(data.getUserEntity().getId())
                .userNickname(data.getUserEntity().getNickname())
                .title(data.getTitle())
                .content(data.getContent())
                .viewCnt(data.getViewCnt())
                .goodCnt(data.getGoodCnt())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build()
        );
        return boardDTOS;
    }

    //내가 쓴 댓글
    public Page<CommentDTO> myComment(Pageable page, HttpServletRequest request, Principal principal)throws Exception{
        int curPage = page.getPageNumber()-1;
        int pageLimit = 10;

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user ==null){
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }
        Integer userId = user.getId();

        Pageable pageable = PageRequest.of
                (curPage, pageLimit, Sort.by(Sort.Direction.DESC,"id"));
        Page<CommentEntity> commentEntities = commentRepository.findDistinctByBoardId(userId, pageable);

        Page<CommentDTO> commentDTOS = commentEntities.map(data->CommentDTO.builder()
                .id(data.getId())
                .comment(data.getComment())
                .boardTitle(data.getBoardEntity().getTitle())
                .boardCategoryType(data.getBoardEntity().getCategoryType())
                .boardViewCnt(data.getBoardEntity().getViewCnt())
                .boardRegDate(data.getBoardEntity().getRegDate())
                .boardId(data.getBoardEntity().getId())
                .goodCnt(data.getGoodCnt())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build());
        return commentDTOS;
    }

    //접속중인 유저 비밀번호 조회
    public String bringPassword(Principal principal) throws Exception {
        String email = principal.getName();
        UserEntity userEntity = userRepository.findByEmail(email);
        String bpassword = userEntity.getPassword();
        return bpassword;
    }

    //비밀번호 변경
    public void updatePassword(UserDTO userDTO, Principal principal) throws Exception {
        String email = principal.getName();

        String npassword = userDTO.getPassword();

        String newPassword = passwordEncoder.encode(npassword);
        UserEntity userEntity = userRepository.findByEmail(email);
        userEntity.setPassword(newPassword);

        userRepository.save(userEntity);
    }

    //닉네임 변경
    public void updateNickname(UserDTO userDTO, Principal principal, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String nickname = userDTO.getNickname();
        UserEntity userEntity = (UserEntity) session.getAttribute("user");

        if (userEntity != null) {
            userEntity.setNickname(nickname);
        } else {
            String loginId = principal.getName();
            userEntity = userRepository.findByEmail(loginId);
            userEntity.setNickname(nickname);
        }
        userRepository.save(userEntity);
    }

    //수정필요, 회원탈퇴
    public void cancelUser(Principal principal, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        String email = "";

        if (userEntity != null) {
            email = userEntity.getEmail();
        } else {
            email = principal.getName();
        }
        userRepository.deleteByEmail(email);
    }

    //전체 유저 리스트
    public Page<UserDTO> userList(Pageable page) throws Exception {
        int curPage = page.getPageNumber() - 1;
        int pageLimit = 10;

        Pageable pageable = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "id"));

        Page<UserEntity> userEntities = userRepository.findAll(pageable);
        Page<UserDTO> userDTOS = userEntities.map(data -> UserDTO.builder()
                .id(data.getId())
                .email(data.getEmail())
                .nickname(data.getNickname())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build()
        );

        return userDTOS;
    }

    //이메일 확인
    public boolean userEmailCheck(String email){

        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity!=null){
            return true;
        }
        else {
            return false;
        }
    }

}
