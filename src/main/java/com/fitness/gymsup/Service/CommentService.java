/*
    파일명 : CommentService.java
    기 능 : 댓글 전체목록 조회(각 댓글의 답글목록 포함), 댓글 등록/수정/삭제, 유저 댓글 모두 삭제
    작성일 : 2023.12.08
    작성자 : 전현진
*/
package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.DTO.ReplyDTO;
import com.fitness.gymsup.Entity.*;
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
public class CommentService {
    //주입 : Repository, ModelMapper
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    //댓글 전체목록
    public List<CommentDTO> list(int boardId) throws Exception {

        //댓글목록 조회
        List<CommentEntity> commentEntities = commentRepository.findByBoardId(boardId);
        List<CommentDTO> commentDTOS = new ArrayList<>();
        log.info("boardId : " + boardId);

        //각 댓글의 답글목록 조회
        for(CommentEntity commentEntity : commentEntities) {
            List<ReplyEntity> replyEntities = replyRepository.findByCommentId(commentEntity.getId());
            List<ReplyDTO> replyDTOS = new ArrayList<>();

            for(ReplyEntity replyEntity : replyEntities) {
                ReplyDTO replyDTO = ReplyDTO.builder()
                        .id(replyEntity.getId())
                        .commentId(replyEntity.getCommentEntity().getId())
                        .boardId(boardId)
                        .userId(replyEntity.getUserEntity().getId())
                        .userNickname(replyEntity.getUserEntity().getNickname())
                        .reply(replyEntity.getReply())
                        .regDate(replyEntity.getRegDate())
                        .modDate(replyEntity.getModDate())
                        .build();
                replyDTOS.add(replyDTO);
            }

            CommentDTO commentDTO = CommentDTO.builder()
                    .id(commentEntity.getId())
                    .boardId(commentEntity.getBoardEntity().getId())
                    .userId(commentEntity.getUserEntity().getId())
                    .userNickname(commentEntity.getUserEntity().getNickname())
                    .comment(commentEntity.getComment())
                    .goodCnt(commentEntity.getGoodCnt())
                    .replyDTOList(replyDTOS)
                    .regDate(commentEntity.getRegDate())
                    .modDate(commentEntity.getModDate())
                    .build();

            commentDTOS.add(commentDTO);
        }

        return commentDTOS;
    }

    //댓글 등록
    public void register(CommentDTO commentDTO,
                         HttpServletRequest request,
                         Principal principal) throws Exception {

        //부모 게시글 Entity
        BoardEntity parentBoard = boardRepository.findById(commentDTO.getBoardId()).orElseThrow();
        log.info("boardId : " + commentDTO.getBoardId());

        //댓글 작성자 Entity
        HttpSession session = request.getSession();
        UserEntity writer = (UserEntity) session.getAttribute("user");
        if(writer == null) {
            String email = principal.getName();
            writer = userRepository.findByEmail(email);
        }
        log.info(parentBoard);
        log.info(writer);

        CommentEntity commentEntity = modelMapper.map(commentDTO, CommentEntity.class);
        commentEntity.setBoardEntity(parentBoard);
        commentEntity.setUserEntity(writer);

        commentRepository.save(commentEntity);
    }

    //댓글 수정
    public void modify(CommentDTO commentDTO,
                       HttpServletRequest request,
                       Principal principal) throws Exception {

        //댓글 조회
        CommentEntity commentEntity = commentRepository.findById(commentDTO.getId()).orElseThrow();

        //부모 게시글 조회
        BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardId()).orElseThrow();

        //댓글 작성자 조회
        //댓글 작성자 Entity
        HttpSession session = request.getSession();
        UserEntity writer = (UserEntity) session.getAttribute("user");
        if(writer == null) {
            String email = principal.getName();
            writer = userRepository.findByEmail(email);
        }

        CommentEntity updateEntity = modelMapper.map(commentDTO, CommentEntity.class);
        updateEntity.setId(commentEntity.getId());
        updateEntity.setBoardEntity(boardEntity);
        updateEntity.setUserEntity(writer);

        commentRepository.save(updateEntity);
    }

    //댓글 삭제
    public void delete(Integer id) throws Exception {

        //해당 댓글에 등록된 답글 조회
        List<ReplyEntity> replyEntities = replyRepository.findByCommentId(id);

        //reply 테이블에서 답글 삭제
        for(ReplyEntity replyEntity : replyEntities) {
            replyRepository.deleteAllByCommentId(id);
        }

        //comment 테이블에서 해당 댓글 삭제
        commentRepository.deleteById(id);
    }

    //유저 댓글 모두 삭제
    public void userCommentDelete(HttpServletRequest request,
                                  Principal principal) throws Exception {

        HttpSession session = request.getSession();
        UserEntity writer = (UserEntity) session.getAttribute("user");
        if(writer == null) {
            String email = principal.getName();
            writer = userRepository.findByEmail(email);
        }

        List<CommentEntity> commentEntities = commentRepository.findAllByUserEntity(writer);
        for(CommentEntity commentEntity : commentEntities){
            replyRepository.deleteAllByCommentId(commentEntity.getId());
        }

        commentRepository.deleteAllByUserEntity(writer);
    }

    public Integer userId(HttpServletRequest request,
                          Principal principal) throws Exception {

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        return user.getId();
    }

    /*//게시판유저아이디와 로그인한 유저 아이디 비교
    public boolean userConfirm(Integer id,
                               HttpServletRequest request,
                               Principal principal) throws Exception {

        CommentEntity commentEntity = commentRepository.findById(id).orElseThrow();

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        boolean userLoginConfirm;
        int commentUserId = commentEntity.getUserEntity().getId();
        int sessionUserId = user.getId();

        if(commentUserId == sessionUserId) {
            userLoginConfirm = true;
        } else {
            userLoginConfirm= false;
        }

        return userLoginConfirm;
    }*/
}