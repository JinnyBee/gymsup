package com.fitness.gymsup.Service;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.Constant.BookmarkType;
import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.DTO.BoardImageDTO;
import com.fitness.gymsup.DTO.ReplyDTO;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.BoardImageEntity;
import com.fitness.gymsup.Entity.CommentEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.*;
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
    //게시글 업로드 사진파일이 저장될 경로
    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    //주입 : Repository, ModelMapper, 파일업로드 클래스
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FileUploader fileUploader;
    private final ModelMapper modelMapper = new ModelMapper();

    //게시글 전체목록
    public Page<BoardDTO> listAll(Pageable page) throws Exception {
        int curPage = page.getPageNumber()-1;
        int pageLimit = 5;

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
                .modDate(data.getModDate())
                .build()
        );

        return boardDTOS;
    }
    //특정게시글 전체목록
    public Page<BoardDTO> list(BoardCategoryType categoryType, Pageable page) throws Exception {
        int curPage = page.getPageNumber()-1;
        int pageLimit = 5;

        log.info(categoryType.name());

        Pageable pageable = PageRequest.of(curPage, pageLimit,Sort.by(Sort.Direction.DESC, "id"));

        Page<BoardEntity> boardEntities = boardRepository.findAllByCategoryType(pageable, categoryType);
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
                .modDate(data.getModDate())
                .build()
        );

        return boardDTOS;
    }
    //특정게시글 인기글(TOP2)
    public List<BoardDTO> best(BoardCategoryType categoryType) throws Exception {
        log.info(categoryType.name());

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
                    .modDate(data.getModDate())
                    .build();
            boardDTOS.add(boardDTO);
        }        

        return boardDTOS;
    }
    //게시글 등록
    public void register(BoardDTO boardDTO, List<MultipartFile> imgFiles,
                         HttpServletRequest request, Principal principal) throws Exception {
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
                newFileName = fileUploader.uploadFile(imgUploadLocation,
                                                      originalFileName,
                                                      imgFile.getBytes());
                log.info("newFileName : "+ newFileName);

                //board_image 테이블에 이미지파일 정보 저장
                BoardImageEntity boardImageEntity = BoardImageEntity.builder()
                        .boardEntity(newBoard)
                        .imgFile(newFileName)
                        .build();

                log.info(boardImageEntity.toString());
                log.info(boardImageEntity.getImgFile());

                boardImageRepository.save(boardImageEntity);
            }
        }
    }
    //게시글 상세보기
    public BoardDTO detail(Integer id, Boolean isFirst,
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
        log.info("imgFileList.size() : " + imgFileList.size());
        log.info(imgFileList);

        boardDTO.setImgFileList(imgFileList);

        //게시글 조회하고 있는 사용자 Entity
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if(user == null) {
            String email = principal.getName();
            user = userRepository.findByEmail(email);
        }

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
    public void modify(BoardDTO boardDTO, List<MultipartFile> imgFiles) throws Exception {
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
            List<BoardImageEntity> boardImageEntities = boardImageRepository.findAllByBoardId(boardDTO.getId());
            //기존 파일 삭제
            for (BoardImageEntity boardImageEntity : boardImageEntities) {
                log.info(boardImageEntity);
                fileUploader.deleteFile(imgUploadLocation, boardImageEntity.getImgFile());
            }
            //board_image 테이블에서 해당게시판에 올라간 이미지 데이터 모두 삭제
            boardImageRepository.deleteAllByBoardId(boardDTO.getId());

            //새 이미지 업로드 후 board_image 테이블에 저장
            for(MultipartFile imgFile : imgFiles) {
                originalFileName = imgFile.getOriginalFilename();
                if(originalFileName.length() != 0) {
                    newFileName = fileUploader.uploadFile(imgUploadLocation,
                            originalFileName,
                            imgFile.getBytes());
                    log.info(newFileName);

                    //board_image 테이블에 이미지파일 정보 저장
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
    public void remove(Integer id) throws Exception {
        //해당 게시글에 등록된 이미지파일 조회
        List<BoardImageEntity> boardImageEntities = boardImageRepository.findAllByBoardId(id);
        log.info("boardImageEntities.size() : " + boardImageEntities.size());

        for(BoardImageEntity boardImageEntity : boardImageEntities) {
            log.info(boardImageEntity.getImgFile());
            //물리적으로 저장된 이미지파일 삭제
            fileUploader.deleteFile(imgUploadLocation,
                                    boardImageEntity.getImgFile());
            //board_image 테이블에서 이미지파일 정보 삭제
            boardImageRepository.deleteAllByBoardId(boardImageEntity.getId());
        }

        //해당 게시글에 등록된 댓글 조회
        List<CommentEntity> commentEntities = commentRepository.findByBoardId(id);
        for(CommentEntity commentEntity : commentEntities) {
            //comment 테이블에서 댓글 삭제
            commentRepository.deleteAllByBoardId(id);
        }

        //board 테이블에서 해당 게시글 삭제
        boardRepository.deleteById(id);
    }
}