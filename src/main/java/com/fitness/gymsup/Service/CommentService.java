package com.fitness.gymsup.Service;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.DTO.BoardImageDTO;
import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.BoardImageEntity;
import com.fitness.gymsup.Entity.CommentEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.BoardImageRepository;
import com.fitness.gymsup.Repository.BoardRepository;
import com.fitness.gymsup.Repository.CommentRepository;
import com.fitness.gymsup.Repository.UserRepository;
import com.fitness.gymsup.Util.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CommentService {
    //게시글 업로드 사진파일이 저장될 경로
    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    //주입 : Repository, ModelMapper, 파일업로드 클래스
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final FileUploader fileUploader;
    private final ModelMapper modelMapper = new ModelMapper();

    //댓글 전체목록
    public List<CommentDTO> list(int boardId) throws Exception {
        log.info("boardId : " + boardId);
        List<CommentEntity> commentEntities = commentRepository.findByBoardId(boardId);
        List<CommentDTO> commentDTOS = new ArrayList<>();

        for(CommentEntity commentEntity : commentEntities) {
            CommentDTO commentDTO = CommentDTO.builder()
                    .id(commentEntity.getId())
                    .boardId(commentEntity.getBoardEntity().getId())
                    .userId(commentEntity.getUserEntity().getId())
                    .userNickname(commentEntity.getUserEntity().getNickname())
                    .content(commentEntity.getContent())
                    .goodCnt(commentEntity.getGoodCnt())
                    .regDate(commentEntity.getRegDate())
                    .modDate(commentEntity.getModDate())
                    .build();
            commentDTOS.add(commentDTO);
        }

        return commentDTOS;
    }
    //댓글 등록
    public void register(CommentDTO commentDTO) throws Exception {
        log.info("id : " + commentDTO.getBoardId());
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
        commentRepository.deleteById(id);
    }
}