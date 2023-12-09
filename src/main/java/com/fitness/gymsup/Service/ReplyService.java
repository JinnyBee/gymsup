/*
    파일명 : ReplyService.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.ReplyDTO;
import com.fitness.gymsup.Entity.CommentEntity;
import com.fitness.gymsup.Entity.ReplyEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ReplyService {
    //주입 : Repository, ModelMapper, 파일업로드 클래스
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    //답글 전체목록
    public List<ReplyDTO> list(int commentId) throws Exception {

        List<ReplyEntity> replyEntities = replyRepository.findByCommentId(commentId);
        List<ReplyDTO> replyDTOS = new ArrayList<>();
        log.info("commentId : " + commentId);

        for(ReplyEntity replyEntity : replyEntities) {
            ReplyDTO replyDTO = ReplyDTO.builder()
                    .id(replyEntity.getId())
                    .commentId(replyEntity.getCommentEntity().getId())
                    .boardId(replyEntity.getCommentEntity().getBoardEntity().getId())
                    .userId(replyEntity.getUserEntity().getId())
                    .userNickname(replyEntity.getUserEntity().getNickname())
                    .reply(replyEntity.getReply())
                    .regDate(replyEntity.getRegDate())
                    .modDate(replyEntity.getModDate())
                    .build();

            replyDTOS.add(replyDTO);
        }

        return replyDTOS;
    }

    //답글 등록
    public void register(ReplyDTO replyDTO,
                         HttpServletRequest request,
                         Principal principal) throws Exception {

        //부모 댓글 Entity 조회
        CommentEntity parentComment = commentRepository.findById(replyDTO.getCommentId()).orElseThrow();
        log.info("commentId : " + replyDTO.getCommentId());

        //댓글 작성자 Entity
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        log.info(parentComment);
        log.info(user);

        ReplyEntity replyEntity = modelMapper.map(replyDTO, ReplyEntity.class);
        replyEntity.setCommentEntity(parentComment);
        replyEntity.setUserEntity(user);

        replyRepository.save(replyEntity);
    }

    //답글 수정
    public void modify(ReplyDTO replyDTO,
                       HttpServletRequest request,
                       Principal principal) throws Exception {

        //답글 조회
        ReplyEntity replyEntity = replyRepository.findById(replyDTO.getId()).orElseThrow();

        //부모 댓글 Entity 조회
        CommentEntity commentEntity = commentRepository.findById(replyDTO.getCommentId()).orElseThrow();

        //답글 작성자 Entity 조회
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        ReplyEntity updateEntity = modelMapper.map(replyDTO, ReplyEntity.class);
        updateEntity.setId(replyEntity.getId());
        updateEntity.setCommentEntity(commentEntity);
        updateEntity.setUserEntity(user);

        replyRepository.save(updateEntity);
    }

    //답글 삭제
    public void delete(Integer id) throws Exception {

        replyRepository.deleteById(id);
    }

    //유저 답글 모두 삭제
    public void userReplyDelete(HttpServletRequest request,
                                Principal principal) throws Exception {

        HttpSession session = request.getSession();
        UserEntity writer = (UserEntity) session.getAttribute("user");

        if(writer == null) {
            String email = principal.getName();
            writer = userRepository.findByEmail(email);
        }

        replyRepository.deleteAllByUserEntity(writer);
    }
}