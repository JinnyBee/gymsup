package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.BookmarkDTO;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.BookmarkEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.BoardRepository;
import com.fitness.gymsup.Repository.BookmarkRepository;
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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public void saveBookmark(BookmarkDTO bookmarkDTO, HttpServletRequest request, Principal principal)throws Exception{

        BookmarkEntity bookmarkEntity = modelMapper.map(bookmarkDTO, BookmarkEntity.class);

        BoardEntity boardEntity = boardRepository.findById(bookmarkDTO.getBoardId()).orElseThrow();

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user ==null){
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        BookmarkEntity search = bookmarkRepository.findAllByBoardEntityAndUserEntity(boardEntity, user);
        if (search != null){
            if(search.isBookmark()==true){
                search.setBookmark(false);
                bookmarkRepository.save(search);
            }else if(search.isBookmark()==false) {
                search.setBookmark(true);
                bookmarkRepository.save(search);
            }
        }else {
            bookmarkEntity.setUserEntity(user);
            bookmarkEntity.setBookmark(true);
            bookmarkEntity.setBoardEntity(boardEntity);
            bookmarkRepository.save(bookmarkEntity);
        }

    }

    public Page<BookmarkDTO> bookmarkList(Pageable page, HttpServletRequest request, Principal principal)throws Exception{
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

        Page<BookmarkEntity> bookmarkEntities = bookmarkRepository.findAllByUserEntity(pageable,user);
        Page<BookmarkDTO> bookmarkDTOS = bookmarkEntities.map(data->BookmarkDTO.builder()
                .id(data.getId())
                .isBookmark(data.isBookmark())
                .boardId(data.getBoardEntity().getId())
                .boardTitle(data.getBoardEntity().getTitle())
                .boardViewCnt(data.getBoardEntity().getViewCnt())
                .categoryType(data.getBoardEntity().getCategoryType())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build()
        );
        return bookmarkDTOS;
    }
}
