package com.fitness.gymsup.Service;

import com.fitness.gymsup.Constant.BookmarkType;
import com.fitness.gymsup.DTO.BookmarkDTO;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.BookmarkEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.BoardRepository;
import com.fitness.gymsup.Repository.BookmarkRepository;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class BookmarkService {
    //주입 : Repository, ModelMapper
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    //북마크 전체목록
    public Page<BookmarkDTO> list(BookmarkType bookmarkType,
                                  Pageable page,
                                  HttpServletRequest request,
                                  Principal principal) throws Exception{
        int curPage = page.getPageNumber()-1;
        int pageLimit = 5;

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user ==null){
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        Pageable pageable = PageRequest.of
                (curPage, pageLimit, Sort.by(Sort.Direction.DESC,"id"));

        //Page<BookmarkEntity> bookmarkEntities = bookmarkRepository.findAllByUserEntity(pageable,user);
        Page<BookmarkEntity> bookmarkEntities = bookmarkRepository
                .findAllByUserEntityAndBookmarkType(pageable, user, bookmarkType);
        Page<BookmarkDTO> bookmarkDTOS = bookmarkEntities.map(data->BookmarkDTO.builder()
                .id(data.getId())
                .userId(data.getUserEntity().getId())
                .boardId(data.getBoardEntity().getId())
                .boardTitle(data.getBoardEntity().getTitle())
                .boardViewCnt(data.getBoardEntity().getViewCnt())
                .categoryType(data.getBoardEntity().getCategoryType())
                .bookmarkType(data.getBookmarkType())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build()
        );
        return bookmarkDTOS;
    }

    //북마크(북마크|좋아요) 등록
    public void register(Integer boardId,
                         BookmarkType bookmarkType,
                         HttpServletRequest request,
                         Principal principal) throws Exception {
        log.info(boardId);
        //북마크 게시글 Entity
        BoardEntity board = boardRepository.findById(boardId).orElseThrow();

        //북마크 선택한 사용자 Entity
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        log.info(board);
        log.info(user);
        log.info(boardId);

        //기존에 등록되어 있지 않을 경우
        if(bookmarkRepository.countAllByUserEntityAndBoardEntityAndBookmarkType(
                user, board, bookmarkType) == 0) {

            BookmarkEntity bookmarkEntity = new BookmarkEntity();
            bookmarkEntity.setBoardEntity(board);
            bookmarkEntity.setUserEntity(user);
            bookmarkEntity.setBookmarkType(bookmarkType);

            //bookmark 테이블에 등록
            bookmarkRepository.save(bookmarkEntity);

            //좋아요를 눌렀을 경우 board 테이블의 good_cnt 증가
            if(bookmarkType.equals(BookmarkType.GOOD)) {
                boardRepository.goodCntUp(boardId);
            }
        }
    }
    //북마크(북마크) 삭제
    public void remove(Integer boardId,
                       BookmarkType bookmarkType,
                       HttpServletRequest request,
                       Principal principal) throws Exception {
        //북마크 게시글 Entity
        BoardEntity board = boardRepository.findById(boardId).orElseThrow();

        //북마크해제 선택한 사용자 Entity
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        log.info(board);
        log.info(user);
        log.info(boardId);

        BookmarkEntity bookmarkEntity = bookmarkRepository
                .findByUserEntityAndBoardEntityAndBookmarkType(user, board, bookmarkType);

        //기존에 등록되어 있는 경우 bookmark 테이블에서 해당 북마크 삭제
        if(bookmarkEntity != null) {
            log.info(bookmarkEntity);
            log.info(bookmarkEntity.getId());
            bookmarkRepository.deleteById(bookmarkEntity.getId());
        }
    }
}