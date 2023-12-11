/*
    파일명 : BoardService.java
    기 능 : 게시판 CRUD 기능
    작성일 : 2023.12.08
    작성자 : 전현진
*/
package com.fitness.gymsup.Service;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.Constant.BookmarkType;
import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.DTO.BoardImageDTO;
import com.fitness.gymsup.DTO.Time;
import com.fitness.gymsup.Entity.*;
import com.fitness.gymsup.Repository.*;
import com.fitness.gymsup.Util.FileUploader;
import com.fitness.gymsup.Util.S3Uploader;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BoardService {
    //업로드 사진파일이 저장될 경로
    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    //주입 : Repository, ModelMapper, 파일업로드 클래스
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FileUploader fileUploader;    //로컬 파일업로드
    private final S3Uploader s3Uploader;        //S3 파일업로드

    private final ModelMapper modelMapper = new ModelMapper();

    //게시글 전체목록
    public Page<BoardDTO> listAll(Pageable page) throws Exception {

        int curPage = page.getPageNumber()-1;
        int pageLimit = 10;

        Pageable pageable = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "id"));

        Page<BoardEntity> boardEntities = boardRepository.findAll(pageable);
        Page<BoardDTO> boardDTOS = boardEntities.map(data->BoardDTO.builder()
                .id(data.getId())
                .categoryType(data.getCategoryType())
                .userId(data.getUserEntity().getId())
                .userNickname(data.getUserEntity().getNickname())
                .title(data.getTitle())
                .content(data.getContent())
                .viewCnt(data.getViewCnt())
                .goodCnt(data.getGoodCnt())
                .regDate(data.getRegDate())
                .displayRegDate(Time.calculateTime(data.getRegDate()))
                .modDate(data.getModDate())
                .build()
        );

        return boardDTOS;
    }

    //특정 카테고리 제외한 게시글 전체목록
    public Page<BoardDTO> listAllWithoutCategory(Pageable page,
                                                 BoardCategoryType categoryType) throws Exception {

        int curPage = page.getPageNumber()-1;
        int pageLimit = 10;

        Pageable pageable = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "id"));

        Page<BoardEntity> boardEntities = boardRepository.findAllWithoutCategoryType(pageable, categoryType);
        Page<BoardDTO> boardDTOS = boardEntities.map(data->BoardDTO.builder()
                .id(data.getId())
                .categoryType(data.getCategoryType())
                .userId(data.getUserEntity().getId())
                .userNickname(data.getUserEntity().getNickname())
                .title(data.getTitle())
                .content(data.getContent())
                .viewCnt(data.getViewCnt())
                .goodCnt(data.getGoodCnt())
                .commentCount(commentRepository.countByBoardEntity_Id(data.getId()))
                .regDate(data.getRegDate())
                .displayRegDate(Time.calculateTime(data.getRegDate()))
                .modDate(data.getModDate())
                .build()
        );

        return boardDTOS;
    }

    //특정카테고리 게시글 전체목록
    public Page<BoardDTO> list(Pageable page,
                               BoardCategoryType categoryType) throws Exception {

        return list(page, categoryType, "", "");
    }
    public Page<BoardDTO> list(Pageable page,
                               BoardCategoryType categoryType,
                               String searchType,
                               String keyword) throws Exception {

        int curPage = page.getPageNumber()-1;
        int pageLimit = 10;

        log.info(categoryType.name());

        Pageable pageable = PageRequest.of(curPage, pageLimit,Sort.by(Sort.Direction.DESC, "id"));

        //각 검색조건에 따른 조회
        Page<BoardEntity> boardEntities;
        if(searchType.equals("t") && keyword != null) {             //제목으로 검색
            boardEntities = boardRepository.searchTitle(pageable, categoryType, keyword);
        } else if(searchType.equals("c") && keyword != null) {      //내용으로 검색
            boardEntities = boardRepository.searchContent(pageable, categoryType, keyword);
        } else if(searchType.equals("n") && keyword != null) {      //닉네임으로 검색
            boardEntities = boardRepository.searchNickname(pageable, categoryType, keyword);
        } else if(searchType.equals("tc") && keyword != null) {     //제목+내용으로 검색
            boardEntities = boardRepository.searchTitleContent(pageable, categoryType, keyword);
        } else if(searchType.equals("tcn") && keyword != null) {    //제목+내용+닉네임으로 검색
            boardEntities = boardRepository.searchTitleContentNickname(pageable, categoryType, keyword);
        } else {
            boardEntities = boardRepository.findAllByCategoryType(pageable, categoryType);
        }

        log.info(boardEntities.getTotalPages());
        log.info(boardEntities.getTotalElements());

        Page<BoardDTO> boardDTOS = boardEntities.map(data->BoardDTO.builder()
                .id(data.getId())
                .categoryType(data.getCategoryType())
                .userId(data.getUserEntity().getId())
                .userNickname(data.getUserEntity().getNickname())
                .title(data.getTitle())
                .content(data.getContent())
                .viewCnt(data.getViewCnt())
                .goodCnt(data.getGoodCnt())
                .commentCount(commentRepository.countByBoardEntity_Id(data.getId()))
                .regDate(data.getRegDate())
                .displayRegDate(Time.calculateTime(data.getRegDate()))
                .modDate(data.getModDate())
                .build()
        );

        return boardDTOS;
    }

    //특정카테고리 게시글 인기글(TOP2)
    public List<BoardDTO> best(BoardCategoryType categoryType) throws Exception {

        //좋아요 높은 순으로 TOP2 게시글 조회
        List<BoardEntity> boardEntities = boardRepository.findTop2ByCategoryTypeOrderByGoodCntDesc(categoryType);
        List<BoardDTO> boardDTOS = new ArrayList<>();

        for(BoardEntity data : boardEntities) {
            //게시글 첨부이미지 조회
            List<String> imgFileList = new ArrayList<>();
            List<BoardImageDTO> boardImageDTOS = Arrays.asList(
                    modelMapper.map(boardImageRepository.findAllByBoardId(data.getId()), BoardImageDTO[].class));

            for(BoardImageDTO boardImageDTO : boardImageDTOS) {
                imgFileList.add(boardImageDTO.getImgFile());
            }

            BoardDTO boardDTO = BoardDTO.builder()
                    .id(data.getId())
                    .categoryType(data.getCategoryType())
                    .userId(data.getUserEntity().getId())
                    .userNickname(data.getUserEntity().getNickname())
                    .title(data.getTitle())
                    .content(data.getContent())
                    .imgFileList(imgFileList)
                    .viewCnt(data.getViewCnt())
                    .goodCnt(data.getGoodCnt())
                    .regDate(data.getRegDate())
                    .displayRegDate(Time.calculateTime(data.getRegDate()))
                    .modDate(data.getModDate())
                    .build();
            boardDTOS.add(boardDTO);
        }        

        return boardDTOS;
    }

    //특정카테고리 게시글 최신글(5개)
    public List<BoardDTO> latest(BoardCategoryType categoryType) throws Exception {

        //등록일 기준 최근5개 게시글 조회
        List<BoardEntity> boardEntities = boardRepository.findTop5ByCategoryTypeOrderByRegDateDesc(categoryType);
        List<BoardDTO> boardDTOS = new ArrayList<>();

        for(BoardEntity data : boardEntities) {
            BoardDTO boardDTO = BoardDTO.builder()
                    .id(data.getId())
                    .categoryType(data.getCategoryType())
                    .userId(data.getUserEntity().getId())
                    .userNickname(data.getUserEntity().getNickname())
                    .title(data.getTitle())
                    .content(data.getContent())
                    .viewCnt(data.getViewCnt())
                    .goodCnt(data.getGoodCnt())
                    .commentCount(commentRepository.countByBoardEntity_Id(data.getId()))
                    .regDate(data.getRegDate())
                    .displayRegDate(Time.calculateTime(data.getRegDate()))
                    .modDate(data.getModDate())
                    .build();
            boardDTOS.add(boardDTO);
        }

        return boardDTOS;
    }

    //게시글 등록
    public void register(BoardDTO boardDTO,
                         List<MultipartFile> imgFiles,
                         HttpServletRequest request,
                         Principal principal) throws Exception {

        BoardEntity boardEntity = modelMapper.map(boardDTO, BoardEntity.class);
        String originalFileName = "";
        String newFileName = "";

        //현재 접속중인 UserEntity 가져오기
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        //board 테이블에 새 게시글 저장
        boardEntity.setUserEntity(user);
        BoardEntity newBoard = boardRepository.save(boardEntity);

        log.info("userId : " + boardDTO.getUserId());
        log.info(user.toString());
        log.info(newBoard.toString());

        for(MultipartFile imgFile : imgFiles) {
            originalFileName = imgFile.getOriginalFilename();

            //게시글에 첨부된 이미지파일이 존재하면
            if(originalFileName.length() != 0) {
                //이미지파일을 이미지 저장경로에 업로드
                newFileName = s3Uploader.upload(imgFile, imgUploadLocation);
                /*newFileName = fileUploader.uploadFile(imgUploadLocation,
                                                      originalFileName,
                                                      imgFile.getBytes());*/
                log.info("newFileName : "+ newFileName);

                //board_image 테이블에 이미지파일 정보 저장
                BoardImageEntity boardImageEntity = BoardImageEntity.builder()
                        .boardEntity(newBoard)
                        .imgFile(newFileName)
                        .build();
                boardImageRepository.save(boardImageEntity);

                log.info(boardImageEntity);
            }
        }
    }

    //게시글 상세보기
    public BoardDTO detail(Integer id,
                           Boolean isFirst,
                           HttpServletRequest request,
                           Principal principal) throws Exception {

        //조회수 증가 (첫 상세보기에만 증가)
        if(isFirst) {
            log.info("id : "+id);
            boardRepository.viewCntUp(id);
        }

        //게시글 상세정보 및 첨부이미지 조회
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow();
        BoardDTO boardDTO = modelMapper.map(boardEntity, BoardDTO.class);
        List<BoardImageDTO> boardImageDTOS = Arrays.asList(
                modelMapper.map(boardImageRepository.findAllByBoardId(id), BoardImageDTO[].class));

        List<String> imgFileList = new ArrayList<>();
        for(BoardImageDTO boardImageDTO : boardImageDTOS) {
            imgFileList.add(boardImageDTO.getImgFile());
        }
        boardDTO.setImgFileList(imgFileList);

        log.info("imgFileList.size() : " + imgFileList.size());
        log.info(imgFileList);

        //게시글 조회하고 있는 login 사용자 Entity
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }
        boardDTO.setLoginUserId(user.getId());

        //북마크, 좋아요 확인
        if(bookmarkRepository.countAllByUserEntityAndBoardEntityAndBookmarkType(
                user, boardEntity, BookmarkType.BOOKMARK) >= 1) {
            boardDTO.setBookmarkOn(true);
        } else {
            boardDTO.setBookmarkOn(false);
        }
        if(bookmarkRepository.countAllByUserEntityAndBoardEntityAndBookmarkType(
                user, boardEntity, BookmarkType.GOOD) >= 1) {
            boardDTO.setGoodOn(true);
        } else {
            boardDTO.setGoodOn(false);
        }

        return boardDTO;
    }

    //게시글 수정
    public void modify(BoardDTO boardDTO,
                       List<MultipartFile> imgFiles) throws Exception {

        BoardEntity boardEntity = boardRepository.findById(boardDTO.getId()).orElseThrow();
        String originalFileName = "";
        String newFileName = "";
        Boolean existUploadImgFile = false;

        log.info("DTO : " +boardDTO);
        log.info("Entity : " +boardEntity);

        for(MultipartFile imgFile : imgFiles) {
            if( !imgFile.isEmpty() ) existUploadImgFile = true;
        }

        log.info("existUploadImgFile : "+existUploadImgFile);
        //새로 업로드할 이미지파일이 존재한다면 기존 이미지파일을 삭제 후 새 이미지파일 업로드
        if( existUploadImgFile ) {
            //board_image 테이블에 저장된 기존 이미지파일 데이터 모두 조회
            List<BoardImageEntity> boardImageEntities = boardImageRepository.findAllByBoardId(boardDTO.getId());
            //기존 이미지파일 삭제
            for (BoardImageEntity boardImageEntity : boardImageEntities) {
                log.info(boardImageEntity);
                //fileUploader.deleteFile(imgUploadLocation, boardImageEntity.getImgFile());
                s3Uploader.deleteFile(boardImageEntity.getImgFile(), imgUploadLocation);
            }
            //board_image 테이블에 저장된 기존 이미지파일 데이터 모두 삭제
            boardImageRepository.deleteAllByBoardId(boardDTO.getId());

            //새 이미지 업로드 후 board_image 테이블에 저장
            for(MultipartFile imgFile : imgFiles) {
                originalFileName = imgFile.getOriginalFilename();
                if(originalFileName.length() != 0) {
                    //이미지파일을 이미지 저장경로에 업로드
                    newFileName = s3Uploader.upload(imgFile, imgUploadLocation);
                    /*newFileName = fileUploader.uploadFile(imgUploadLocation,
                                                          originalFileName,
                                                          imgFile.getBytes());*/
                    log.info(newFileName);

                    //board_image 테이블에 이미지파일 데이터 저장
                    BoardImageEntity boardImageEntity = BoardImageEntity.builder()
                            .boardEntity(boardEntity)
                            .imgFile(newFileName)
                            .build();
                    boardImageRepository.save(boardImageEntity);
                }
            }
        }

        //수정된 게시글 업데이트
        BoardEntity update = modelMapper.map(boardDTO, boardEntity.getClass());
        update.setId(boardEntity.getId());
        update.setUserEntity(boardEntity.getUserEntity());

        boardRepository.save(update);
    }

    //게시글 삭제
    public void delete(Integer id) throws Exception {

        //해당 게시글에 등록된 이미지파일 조회
        List<BoardImageEntity> boardImageEntities = boardImageRepository.findAllByBoardId(id);
        log.info("boardImageEntities.size() : " + boardImageEntities.size());

        for(BoardImageEntity boardImageEntity : boardImageEntities) {
            log.info(boardImageEntity.getImgFile());
            //이미지파일 삭제
            s3Uploader.deleteFile(boardImageEntity.getImgFile(), imgUploadLocation);
            /*fileUploader.deleteFile(imgUploadLocation,
                                    boardImageEntity.getImgFile());*/
        }
        //board_image 테이블에서 해당 게시글에 등록된 모든 이미지파일 데이터 삭제
        boardImageRepository.deleteAllByBoardId(id);

        //해당 게시글에 등록된 댓글 조회
        List<CommentEntity> commentEntities = commentRepository.findByBoardId(id);
        for(CommentEntity commentEntity : commentEntities) {
            log.info(commentEntity.getId());
            //reply 테이블에서 답글 삭제
            replyRepository.deleteAllByCommentId(commentEntity.getId());
        }
        //comment 테이블에서 댓글 삭제
        commentRepository.deleteAllByBoardId(id);

        //bookmark 테이블에서 해당 게시글 북마크한 bookmark 삭제
        bookmarkRepository.deleteAllByBoardId(id);

        //board 테이블에서 해당 게시글 삭제
        boardRepository.deleteById(id);
    }

    //게시판유저아이디와 로그인한 유저 아이디 비교
    public boolean userConfirm(Integer id,
                               HttpServletRequest request,
                               Principal principal) throws Exception {

        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow();

        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

        boolean userLoginConfirm;
        int boardUserId = boardEntity.getUserEntity().getId();
        int sessionUserId = user.getId();

        if(boardUserId == sessionUserId) {
            userLoginConfirm = true;
        } else {
            userLoginConfirm= false;
        }

        return userLoginConfirm;
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

    public void userBoardDelete(HttpServletRequest request,
                                Principal principal) throws Exception {

        HttpSession session = request.getSession();
        UserEntity writer = (UserEntity) session.getAttribute("user");
        if(writer == null) {
            String email = principal.getName();
            writer = userRepository.findByEmail(email);
        }

        List<BoardEntity> boardEntities = boardRepository.findAllByUserEntity(writer);

        for(BoardEntity boardEntity : boardEntities){
            int id = boardEntity.getId();
            List<BoardImageEntity> boardImageEntities = boardImageRepository.findAllByBoardId(id);

            for(BoardImageEntity boardImageEntity : boardImageEntities) {
                log.info(boardImageEntity.getImgFile());
                //이미지파일 삭제
                s3Uploader.deleteFile(boardImageEntity.getImgFile(), imgUploadLocation);
            }

            boardImageRepository.deleteAllByBoardId(id);

            List<CommentEntity> commentEntities = commentRepository.findByBoardId(id);
            for(CommentEntity commentEntity : commentEntities) {
                //reply 테이블에서 답글 삭제
                replyRepository.deleteAllByCommentId(commentEntity.getId());
            }

            //comment 테이블에서 댓글 삭제
            commentRepository.deleteAllByBoardId(id);

            //bookmark 테이블에서 해당 게시글 북마크한 bookmark 삭제
            bookmarkRepository.deleteAllByBoardId(id);

            //board 테이블에서 해당 게시글 삭제
            boardRepository.deleteById(id);
        }
    }
}