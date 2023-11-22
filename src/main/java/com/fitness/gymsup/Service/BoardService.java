package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.DTO.BoardImageDTO;
import com.fitness.gymsup.Entity.BoardCategoryEntity;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.BoardImageEntity;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Repository.BoardCategoryRepository;
import com.fitness.gymsup.Repository.BoardImageRepository;
import com.fitness.gymsup.Repository.BoardRepository;
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

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private final BoardCategoryRepository boardCategoryRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
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
    //특정게시글 전체목록
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
    public void register(BoardDTO boardDTO, MultipartFile imgFile) throws Exception {
        BoardEntity boardEntity = modelMapper.map(boardDTO, BoardEntity.class);
        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        BoardCategoryEntity category = boardCategoryRepository.findById(boardDTO.getCategoryId()).orElseThrow();
        UserEntity user = userRepository.findById(boardDTO.getUserId()).orElseThrow();

        boardEntity.setCategory(category);
        boardEntity.setUser(user);
        BoardEntity newBoard = boardRepository.save(boardEntity);

        log.info("categoryId : " + boardDTO.getCategoryId());
        log.info("userId : " + boardDTO.getUserId());
        log.info(category.toString());
        log.info(user.toString());
        log.info(newBoard.toString());

        if(originalFileName != null) { //파일이 존재하면 이미지를 업로드, 데이터베이스 저장
            newFileName = fileUploader.uploadFile(
                    imgUploadLocation,
                    originalFileName,
                    imgFile.getBytes()
            );
            log.info("newFileName : "+newFileName);
            BoardImageEntity boardImageEntity = BoardImageEntity.builder()
                            .board(newBoard)
                            .imgFile(newFileName)
                            .build();
            log.info(boardImageEntity.toString());
            log.info(boardImageEntity.getImgFile());

            boardImageRepository.save(boardImageEntity);
        }
    }
    //게시글 상세보기
    public BoardDTO detail(Integer id, String pan) throws Exception {
        //조회수 증가 (개별읽기에만 증가)
        if(pan.equals("R")) {
            log.info("id : "+id);
            boardRepository.viewcnt(id);
        }

        BoardDTO boardDTO = modelMapper.map(boardRepository.findById(id), BoardDTO.class);
        List<BoardImageDTO> boardImageDTOS = Arrays.asList(modelMapper.map(boardImageRepository.findAllByBoardId(id), BoardImageDTO[].class));

        List<String> imgFileList = new ArrayList<>();
        for(BoardImageDTO boardImageDTO : boardImageDTOS) {
            imgFileList.add(boardImageDTO.getImgFile());
        }
        log.info("imgFileList.size() : " + imgFileList.size());
        log.info(imgFileList);

        boardDTO.setImgFileList(imgFileList);

        return boardDTO;
    }
    public void modify(BoardDTO boardDTO) throws Exception {
        modify(boardDTO, null);
    }
    public void modify(BoardDTO boardDTO, MultipartFile imgFile) throws Exception {
        String originalFileName = "";
        BoardEntity boardEntity = boardRepository.findById(boardDTO.getId()).orElseThrow();

        if(imgFile != null) {
            originalFileName = imgFile.getOriginalFilename();
        }

        //새로 업로드할 파일이 존재하면 기존 파일 삭제 후 새로운 파일 업로드
        if(originalFileName.length() != 0) {
            List<String> newFileList = new ArrayList<>();
            List<BoardImageEntity> boardImageEntities = boardImageRepository.findAllByBoardId(boardDTO.getId());

            //기존 파일 삭제
            for (BoardImageEntity boardImageEntity : boardImageEntities) {
                log.info(boardImageEntity);
                fileUploader.deleteFile(imgUploadLocation, boardImageEntity.getImgFile());
            }
            //board_image 테이블에서 해당 이미지 데이터 삭제
            boardImageRepository.deleteAllByBoardId(boardDTO.getId());

            //새로운 파일 업로드
            newFileList.add(fileUploader.uploadFile(imgUploadLocation,
                    originalFileName,
                    imgFile.getBytes()));
            //board_image 테이블에 새 이미지 데이터 저장
            for (String newFile : newFileList) {
                BoardImageEntity boardImageEntity = BoardImageEntity.builder()
                        .board(boardEntity)
                        .imgFile(newFile)
                        .build();
                log.info(newFile);
                boardImageRepository.save(boardImageEntity);
            }
            boardDTO.setId(boardEntity.getId());
        }

        //수정된 게시판 데이터 업데이트
        BoardEntity update = modelMapper.map(boardDTO, boardEntity.getClass());
        boardRepository.save(update);
    }
    //게시글 삭제
    public void remove(Integer id) throws Exception {
        //물리적 위치에 저장된 이미지를 삭제
        List<BoardImageEntity> boardImageEntities = boardImageRepository.findAllByBoardId(id);
        log.info("boardImageEntities.size() : " + boardImageEntities.size());
        for(int i=0; i<boardImageEntities.size(); i++) {
            log.info(boardImageEntities.get(i).getImgFile());
            fileUploader.deleteFile(
                    imgUploadLocation,
                    boardImageEntities.get(i).getImgFile()
            );
            boardImageRepository.deleteById(boardImageEntities.get(i).getId());
        }

        //게시글 레코드 삭제
        boardRepository.deleteById(id);
    }
}