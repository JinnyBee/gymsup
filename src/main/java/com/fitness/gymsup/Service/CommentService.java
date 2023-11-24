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
        log.info("boardId : " + boardId);
        //댓글목록 조회
        List<CommentEntity> commentEntities = commentRepository.findByBoardId(boardId);
        List<CommentDTO> commentDTOS = new ArrayList<>();

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
    public void register(CommentDTO commentDTO) throws Exception {
        log.info("boardId : " + commentDTO.getBoardId());
        //부모 게시글 Entity
        BoardEntity parentBoard = boardRepository.findById(commentDTO.getBoardId()).orElseThrow();
        //댓글 작성자 Entity
        UserEntity writer = userRepository.findById(commentDTO.getUserId()).orElseThrow();
        log.info(parentBoard);
        log.info(writer);

        CommentEntity commentEntity = modelMapper.map(commentDTO, CommentEntity.class);
        commentEntity.setBoardEntity(parentBoard);
        commentEntity.setUserEntity(writer);

        commentRepository.save(commentEntity);
    }
    //댓글 수정
    public void modify(CommentDTO commentDTO) throws Exception {
        //댓글 조회
        CommentEntity commentEntity = commentRepository.findById(commentDTO.getId()).orElseThrow();

        //부모 게시글 조회
        BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardId()).orElseThrow();

        //댓글 작성자 조회
        UserEntity userEntity = userRepository.findById(commentDTO.getUserId()).orElseThrow();

        CommentEntity updateEntity = modelMapper.map(commentDTO, CommentEntity.class);
        updateEntity.setId(commentEntity.getId());
        updateEntity.setBoardEntity(boardEntity);
        updateEntity.setUserEntity(userEntity);

        commentRepository.save(updateEntity);
    }
    //댓글 삭제
    public void remove(Integer id) throws Exception {
        //해당 댓글에 등록된 답글 조회
        List<ReplyEntity> replyEntities = replyRepository.findByCommentId(id);
        for(ReplyEntity replyEntity : replyEntities) {
            //reply 테이블에서 답글 삭제
            replyRepository.deleteAllByCommentId(id);
        }

        //comment 테이블에서 해당 댓글 삭제
        commentRepository.deleteById(id);
    }
}