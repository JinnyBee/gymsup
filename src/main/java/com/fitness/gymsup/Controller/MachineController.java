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

import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
@Log4j2
public class MachineController {

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
    @PostMapping("/machine_detect")
    public String detectProc(@RequestParam("detectImg") MultipartFile detectImg,
                             Model model) throws Exception {

        String errorMessage = "";
        if(detectImg.getOriginalFilename().isEmpty()) {
            errorMessage="파일을 첨부해주세요.";
            model.addAttribute("errorMessage",errorMessage);

            return "redirect:/machine_detect";
        }
        log.info(detectImg.getOriginalFilename());

        //플라스크 서버에 분석할 이미지를 전달하여 처리
        FlaskResponseDTO flaskResponseDTO = flask.requestToFlask(detectImg);
        List<MachineInfoDTO> machineInfoDTOS = new ArrayList<>();

        log.info("Flask Response DTO (resultFilename) : " + flaskResponseDTO.getResultFilename());
        if(flaskResponseDTO.getName().isEmpty()) {
            machineInfoDTOS = machineInfoService.list();
            model.addAttribute("machineInfoDTOS", machineInfoDTOS);

            return "machine/detect_error";
        }

        for(String name : flaskResponseDTO.getName()) {
            log.info("Flask Response DTO (name) : " + name);
            machineInfoDTOS.add(machineInfoService.find(name));
        }
        log.info(machineInfoDTOS);

        model.addAttribute("flaskResponseDTO", flaskResponseDTO);
        model.addAttribute("machineInfoDTOS", machineInfoDTOS);

        return "machine/howto";
    }
    @GetMapping("/machine_howto")
    public String howtoProc(Model model) throws Exception {

        return "machine/howto";
    }
    @GetMapping("/machine_about") //운동기구 전체 페이지
    public String aboutForm(Model model) throws Exception {
        int id1 = 1;
        int id2 = 2;
        int id3 = 3;

        MachineInfoDTO machineInfoDTO = machineInfoService.detail(id1);
        MachineInfoDTO machineInfoDTOid2 = machineInfoService.detail(id2);
        MachineInfoDTO machineInfoDTOid3 = machineInfoService.detail(id3);

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("machineInfoDTO", machineInfoDTO);
        model.addAttribute("machineInfoDTOid2", machineInfoDTOid2);
        model.addAttribute("machineInfoDTOid3", machineInfoDTOid3);
        return "machine/about";
    }
    @GetMapping("/machine_detail")
    public String detailForm(int id,Model model) throws Exception {
        MachineUsageDTO machineUsageDTO = machineUsageService.detail(id);

        model.addAttribute("machineUsageDTO",machineUsageDTO);
        return "machine/detail";
    }
    @GetMapping("/admin_machine_list")
    public String listForm(@PageableDefault(page = 1) Pageable pageable,
                           Model model) throws Exception {
        Page<MachineUsageDTO> machineUsageDTOS = machineUsageService.listAll(pageable);
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

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("machineUsageDTOS",machineUsageDTOS);

        return "machine/alllist";
    }
    @GetMapping("/admin_machine_register")
    public String registerForm(Model model) throws Exception {
        return "machine/usageregister";
    }
    @PostMapping("/admin_machine_register")
    public String registerProc(MachineUsageDTO machineUsageDTO, MultipartFile imgFile) throws Exception {
        machineUsageService.register(machineUsageDTO, imgFile);
        return "redirect:/";
    }
    @GetMapping("/machine_info_modify")
    public String modifyForm(Integer id, Model model) throws Exception {
        MachineInfoDTO machineInfoDTO = machineInfoService.detail(id);

        model.addAttribute("machineInfoDTO", machineInfoDTO);
        return "machine/modify";
    }
    @PostMapping("/machine_info_modify")
    public String modifyProc(MachineInfoDTO machineInfoDTO,
                             RedirectAttributes redirectAttributes,
                             MultipartFile imgFile) throws Exception {

        int id = machineInfoDTO.getId();

        redirectAttributes.addAttribute("id",id);
        machineInfoService.modify(machineInfoDTO, imgFile);
        return "redirect:/machine_select_list";
    }
    @GetMapping("/machine_remove")
    public String removeProc(Model model) throws Exception {
        return "redirect:/machine_list";
    }

    @GetMapping("/machine_select_list")
    public String selectList(@PageableDefault(page =1) Pageable pageable, Model model, int id)throws Exception{
        Page<MachineUsageDTO> machineUsageDTOS =  machineUsageService.partList(id,pageable);
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

        MachineInfoDTO machineInfoDTO = machineInfoService.detail(id);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("machineUsageDTOS",machineUsageDTOS);
        model.addAttribute("machineInfoDTO", machineInfoDTO);

        return "machine/list";
    }
}