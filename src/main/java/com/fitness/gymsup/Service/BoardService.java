package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.Entity.BoardCategoryEntity;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.BoardCategoryRepository;
import com.fitness.gymsup.Repository.BoardRepository;
import com.fitness.gymsup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    //게시글 전체목록
    public Page<BoardDTO> list(Integer categoryId, Pageable page) throws Exception {
        int curPage = page.getPageNumber()-1;
        int pageLimit = 5;

        Pageable pageable = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "id"));

        Page<BoardEntity> boardEntities = boardRepository.findAllByCategoryId(pageable, categoryId);
        Page<BoardDTO> boardDTOS = boardEntities.map(data->BoardDTO.builder()
                .id(data.getId())
                .categoryId(data.getCategory().getId())
                .userId(data.getUser().getId())
                .userNickname(data.getUser().getNickname())
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
    //게시글 등록
    public void register(BoardDTO boardDTO) throws Exception {
        BoardEntity boardEntity = modelMapper.map(boardDTO, BoardEntity.class);

        log.info("categoryId : " + boardDTO.getCategoryId());
        log.info("userId : " + boardDTO.getUserId());
        BoardCategoryEntity category = boardCategoryRepository.findById(boardDTO.getCategoryId()).orElseThrow();
        UserEntity user = userRepository.findById(boardDTO.getUserId()).orElseThrow();

        boardEntity.setCategory(category);
        boardEntity.setUser(user);

        boardRepository.save(boardEntity);
    }
    //게시글 상세보기
    public BoardDTO detail(Integer id, String pan) throws Exception {
        //조회수 증가 (개별읽기에만 증가)
        if(pan.equals("R")) {
            log.info("id : "+id);
            boardRepository.viewcnt(id);
        }

        Optional<BoardEntity> boardEntity = boardRepository.findById(id);
        return modelMapper.map(boardEntity, BoardDTO.class);
    }
    public void modify(BoardDTO boardDTO) throws Exception {
        int id = boardDTO.getId();
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow();

        BoardEntity update = modelMapper.map(boardDTO, BoardEntity.class);
        update.setId(boardEntity.getId());

        boardRepository.save(update);
    }
    //게시글 삭제
    public void remove(Integer id) throws Exception {
        boardRepository.deleteById(id);
    }
}