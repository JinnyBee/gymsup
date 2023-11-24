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
    public void register(BookmarkDTO bookmarkDTO,
                         HttpServletRequest request,
                         Principal principal) throws Exception{
        //북마크 게시글 Entity
        BoardEntity board = boardRepository.findById(bookmarkDTO.getBoardId()).orElseThrow();
        //북마크 선택한 사용자 Entity
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user ==null){
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        log.info(board);
        log.info(user);

        BookmarkEntity bookmarkEntity = modelMapper.map(bookmarkDTO, BookmarkEntity.class);
        bookmarkEntity.setBoardEntity(board);
        bookmarkEntity.setUserEntity(user);

        if(bookmarkRepository.countAllByUserEntityAndBoardEntityAndBookmarkType(
                user, board, bookmarkDTO.getBookmarkType()) == 0) {
            bookmarkRepository.save(bookmarkEntity);
        }
    }
}
