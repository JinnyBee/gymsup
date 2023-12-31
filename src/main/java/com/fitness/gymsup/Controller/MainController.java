/*
    파일명 : MainController.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Service.BasicUserService;
import com.fitness.gymsup.Service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    private final BasicUserService basicUserService;
    private final BoardService boardService;

    @GetMapping({"/", "/index"})
    public String main(Model model,
                       HttpServletRequest request,
                       Principal principal, String errorMessage) throws Exception {

        if(request != null && principal != null) {
            UserEntity userEntity = basicUserService.bringUserInfo(request, principal);
            model.addAttribute("userEntity",userEntity);
        }

        List<BoardDTO> tipBoardBestDTOS = boardService.best(BoardCategoryType.BTYPE_TIP);
        List<BoardDTO> diaryBoardBestDTOS = boardService.best(BoardCategoryType.BTYPE_DIARY);

        model.addAttribute("errorMessage",errorMessage);
        model.addAttribute("tipBoardBestDTOS", tipBoardBestDTOS);
        model.addAttribute("diaryBoardBestDTOS", diaryBoardBestDTOS);
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        log.info(tipBoardBestDTOS);
        log.info(diaryBoardBestDTOS);

        return "index";
    }
    @GetMapping({"/test"})
    public String test() throws Exception {
        return "test";
    }
}