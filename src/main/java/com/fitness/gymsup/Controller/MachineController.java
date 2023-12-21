/*
    파일명 : MachineController.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.FlaskResponseDTO;
import com.fitness.gymsup.DTO.MachineInfoDTO;
import com.fitness.gymsup.DTO.MachineUsageDTO;
import com.fitness.gymsup.Service.MachineInfoService;
import com.fitness.gymsup.Service.MachineUsageService;
import com.fitness.gymsup.Util.Flask;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
@Log4j2
public class MachineController {

    //S3 관련 주소
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    @Autowired
    private Flask flask;

    private final MachineUsageService machineUsageService;
    private final MachineInfoService machineInfoService;


    //운동기구 인식 Form
    @GetMapping("/machine_detect")
    public String detectForm(Model model) throws Exception {

        return "machine/detect";
    }

    //운동기구 인식 처리 (플라스크 AI 서버 연동)
    @PostMapping("/machine_detect")
    public String detectProc(@RequestParam("detectImg") MultipartFile detectImg,
                             Model model) throws Exception {

        if(detectImg.getOriginalFilename().length() == 0) {
            return "machine/detect_error";
        }
        log.info(detectImg.getOriginalFilename());

        //플라스크 서버에 분석할 이미지를 전달하여 처리
        FlaskResponseDTO flaskResponseDTO = flask.requestToFlask(detectImg);

        if(flaskResponseDTO == null || flaskResponseDTO.getName().isEmpty()) {
            return "machine/detect_error";
        }
        log.info("Flask Response DTO (result filename) : " + flaskResponseDTO.getResultFilename());
        log.info("Flask Response DTO (class name) : " + flaskResponseDTO.getName());

        //flaskResponseDTO name 리스트 중 첫번째 값만 사용 (첫번째 class 이름)
        MachineInfoDTO machineInfoDTO = machineInfoService.find(flaskResponseDTO.getName().get(0));
        log.info(machineInfoDTO);

        //S3 관련 주소
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        model.addAttribute("flaskResponseDTO", flaskResponseDTO);
        model.addAttribute("machineInfoDTO", machineInfoDTO);

        return "machine/detect_howto";
    }

    //운동기구 전체 페이지
    @GetMapping("/machine_about")
    public String aboutForm(Model model) throws Exception {

        String foamroller= "foamroller"; // html 디자인을 위해 별도로 아이디 값을 불러옴
        String dumbbell = "dumbbell";
        String kettlebell = "kettlebell";
        String babell = "babell";
        String shoulder_press = "shoulder_press";

        MachineInfoDTO machineInfoDTO_foamRoller = machineInfoService.resultDetail(foamroller);
        MachineInfoDTO machineInfoDTO_dumbBell = machineInfoService.resultDetail(dumbbell);
        MachineInfoDTO machineInfoDTO_kettleBell = machineInfoService.resultDetail(kettlebell);
        MachineInfoDTO machineInfoDTO_babel = machineInfoService.resultDetail(babell);
        MachineInfoDTO machineInfoDTO_shoulderPress = machineInfoService.resultDetail(shoulder_press);

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        model.addAttribute("foamRoller", machineInfoDTO_foamRoller);
        model.addAttribute("dumbBell", machineInfoDTO_dumbBell);
        model.addAttribute("kettleBell", machineInfoDTO_kettleBell);
        model.addAttribute("babel", machineInfoDTO_babel);
        model.addAttribute("shoulderPress", machineInfoDTO_shoulderPress);

        return "machine/about";
    }

    //운동기구 상세보기 (운동기구 기본 정보, 운동기구 사용법 영상 리스트)
    @GetMapping("/machine_info_detail")
    public String machineInfoDetailForm(@PageableDefault(page =1) Pageable pageable,
                                        int id,
                                        String errorMessage,
                                        Model model) throws Exception {

        MachineInfoDTO machineInfoDTO = machineInfoService.detail(id);
        Page<MachineUsageDTO> machineUsageDTOS =  machineUsageService.partList(id, pageable);

        int blockLimit = 5;
        int startPage, endPage, prevPage, currentPage, nextPage, lastPage;

        if(machineUsageDTOS.isEmpty()) {
            startPage = 0;
            endPage = 0;
            prevPage = 0;
            currentPage = 0;
            nextPage = 0;
            lastPage = 0;
        } else {
            startPage = (((int)(Math.ceil((double) pageable.getPageNumber()/blockLimit)))-1) * blockLimit + 1;
            //endPage = Math.min(startPage+blockLimit-1, machineUsageDTOS.getTotalPages());
            endPage = ((startPage+blockLimit-1)<machineUsageDTOS.getTotalPages()) ? startPage+blockLimit-1 : machineUsageDTOS.getTotalPages();

            prevPage = machineUsageDTOS.getNumber();
            currentPage = machineUsageDTOS.getNumber() + 1;
            nextPage = machineUsageDTOS.getNumber() + 2;
            lastPage = machineUsageDTOS.getTotalPages();
        }

        //S3 관련 주소
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        model.addAttribute("errorMessage",errorMessage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("machineUsageDTOS", machineUsageDTOS);
        model.addAttribute("machineInfoDTO", machineInfoDTO);

        return "machine/info_detail";
    }

    //운동기구 사용법 영상 상세보기
    @GetMapping("/machine_usage_detail")
    public String machineUsageDetailForm(@PageableDefault(page = 1) Pageable pageable,
                                         int id, int infoId,
                                         Model model) throws Exception {
        Page<MachineUsageDTO> machineUsageDTOS = machineUsageService.partList(infoId, pageable);
        MachineUsageDTO machineUsageDTO = machineUsageService.detail(id,true);

        int blockLimit = 5;
        int startPage, endPage, prevPage, currentPage, nextPage, lastPage;

        if(machineUsageDTOS.isEmpty()) {
            startPage = 0;
            endPage = 0;
            prevPage = 0;
            currentPage = 0;
            nextPage = 0;
            lastPage = 0;
        } else {
            startPage = (((int)(Math.ceil((double) pageable.getPageNumber()/blockLimit)))-1) * blockLimit + 1;
            //endPage = Math.min(startPage+blockLimit-1, machineUsageDTOS.getTotalPages());
            endPage = ((startPage+blockLimit-1)<machineUsageDTOS.getTotalPages()) ? startPage+blockLimit-1 : machineUsageDTOS.getTotalPages();

            prevPage = machineUsageDTOS.getNumber();
            currentPage = machineUsageDTOS.getNumber() + 1;
            nextPage = machineUsageDTOS.getNumber() + 2;
            lastPage = machineUsageDTOS.getTotalPages();
        }

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("machineUsageDTOS", machineUsageDTOS);
        model.addAttribute("machineUsageDTO", machineUsageDTO);

        return "machine/usage_detail";
    }

    //(관리자) 운동기구 정보 수정 form
    @GetMapping("/machine_info_modify")
    public String machineInfoModifyForm(Integer id,
                                        Model model) throws Exception {

        MachineInfoDTO machineInfoDTO = machineInfoService.detail(id);
        model.addAttribute("machineInfoDTO", machineInfoDTO);

        return "machine/info_modify";
    }

    //(관리자) 운동기구 정보 수정 proc
    @PostMapping("/machine_info_modify")
    public String machineInfoModifyProc(MachineInfoDTO machineInfoDTO,
                                        MultipartFile imgFile,
                                        RedirectAttributes redirectAttributes) throws Exception {

        int id = machineInfoDTO.getId();

        redirectAttributes.addAttribute("id",id);
        machineInfoService.modify(machineInfoDTO, imgFile);

        return "redirect:/machine_info_detail";
    }

    //(관리자) 운동기구 사용법 영상 등록 form
    @GetMapping("/machine_usage_register")
    public String machineUsageRegisterForm(Model model) throws Exception {

        return "machine/usage_register";
    }

    //운동 기구 영상 등록 proc
    @PostMapping("/machine_usage_register")
    public String machineUsageRegisterProc(MachineUsageDTO machineUsageDTO,
                                           MultipartFile imgFile,
                                           RedirectAttributes redirectAttributes) throws Exception {

        Integer id = machineUsageDTO.getMachineInfoId();
        machineUsageService.register(machineUsageDTO, imgFile);

        redirectAttributes.addAttribute("id",id);
        redirectAttributes.addAttribute("errorMessage","등록되었습니다.");

        return "redirect:/machine_info_detail";
    }

    //운동 기구 영상 수정 form
    @GetMapping("/machine_usage_modify")
    public String machineUsageModifyForm(Integer id,
                                         Model model) throws Exception {

        MachineUsageDTO machineUsageDTO = machineUsageService.detail(id, false);
        model.addAttribute("machineUsageDTO", machineUsageDTO);

        return "machine/usage_modify";
    }

    //운동 기구 영상 수정 proc
    @PostMapping("/machine_usage_modify")
    public String machineUsageModifyProc(MachineUsageDTO machineUsageDTO,
                                         MultipartFile imgFile,
                                         RedirectAttributes redirectAttributes) throws Exception {

        int id = machineUsageDTO.getMachineInfoId();
        machineUsageService.modify(machineUsageDTO, imgFile);

        redirectAttributes.addAttribute("id",id);
        redirectAttributes.addAttribute("errorMessage","수정되었습니다.");

        return "redirect:/machine_info_detail";
    }

    //운동 기구 영상 삭제
    @GetMapping("/machine_usage_delete")
    public String machineUsageDelete(Integer uid,
                                     Integer id,
                                     RedirectAttributes redirectAttributes) throws Exception {

        machineUsageService.delete(uid);

        redirectAttributes.addAttribute("id",id);
        redirectAttributes.addAttribute("errorMessage","삭제되었습니다.");

        return "redirect:/machine_info_detail";
    }

    //(관리자페이지 - 운동기구 관리) 운동기구 전체목록
    @GetMapping("/admin_machine_list")
    public String listForm(Model model) throws Exception {

        String foamroller= "foamroller"; // html 디자인을 위해 별도로 아이디 값을 불러옴
        String dumbbell = "dumbbell";
        String kettlebell = "kettlebell";
        String babell = "babell";
        String shoulder_press = "shoulder_press";

        MachineInfoDTO machineInfoDTO_foamRoller = machineInfoService.resultDetail(foamroller);
        MachineInfoDTO machineInfoDTO_dumbBell = machineInfoService.resultDetail(dumbbell);
        MachineInfoDTO machineInfoDTO_kettleBell = machineInfoService.resultDetail(kettlebell);
        MachineInfoDTO machineInfoDTO_babel = machineInfoService.resultDetail(babell);
        MachineInfoDTO machineInfoDTO_shoulderPress = machineInfoService.resultDetail(shoulder_press);

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("foamRoller", machineInfoDTO_foamRoller);
        model.addAttribute("dumbBell", machineInfoDTO_dumbBell);
        model.addAttribute("kettleBell", machineInfoDTO_kettleBell);
        model.addAttribute("babel", machineInfoDTO_babel);
        model.addAttribute("shoulderPress", machineInfoDTO_shoulderPress);
        return "machine/alllist";
    }

    //(관리자) 운동기구 정보 등록 form
    @GetMapping("/machine_info_register")
    public String machineInfoRegisterForm() throws Exception {

        return "machine/info_register";
    }

    //(관리자) 운동기구 정보 등록 proc
    @PostMapping("/machine_info_register")
    public String machineInfoRegisterProc(MachineInfoDTO machineInfoDTO,
                                          MultipartFile imgFile,
                                          Model model) throws Exception {

        machineInfoService.register(machineInfoDTO, imgFile);
        model.addAttribute("machineInfoDTO", machineInfoDTO);

        return "redirect:/admin_machine_list";
    }
}