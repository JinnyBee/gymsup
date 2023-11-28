package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.MachineInfoDTO;
import com.fitness.gymsup.Service.MachineInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MachineController {
    private final MachineInfoService machineInfoService;

    @GetMapping("/machine_detect")
    public String detectForm(Model model) throws Exception {
        return "machine/detect";
    }
    @PostMapping("/machine_detect")
    public String detectProc(Model model) throws Exception {
        return "redirect:/machine_howto";
    }
    @GetMapping("/machine_howto")
    public String howtoProc(Model model) throws Exception {
        return "machine/howto";
    }
    @GetMapping("/machine_about") //운동기구 전체 페이지
    public String aboutForm(Model model) throws Exception {
        List<MachineInfoDTO> machineInfoDTOS = machineInfoService.list();

        model.addAttribute("machineInfoDTOS", machineInfoDTOS);

        return "machine/about";
    }
    @GetMapping("/machine_detail")
    public String detailForm(Model model) throws Exception {
        return "machine/detail";
    }
    @GetMapping("/machine_list")
    public String listForm(Model model) throws Exception {
        return "machine/list";
    }
    @GetMapping("/machine_register")
    public String registerForm(Model model) throws Exception {
        return "machine/register";
    }
    @PostMapping("/machine_register")
    public String registerProc(Model model) throws Exception {
        return "redirect:/machine_list";
    }
    @GetMapping("/machine_modify")
    public String modifyForm(Integer id, Model model) throws Exception {
        MachineInfoDTO machineInfoDTO = machineInfoService.detail(id);

        model.addAttribute("machineInfoDTO", machineInfoDTO);

        return "machine/modify";
    }
    @PostMapping("/machine_modify")
    public String modifyProc(MachineInfoDTO machineInfoDTO,
                             MultipartFile imgFile) throws Exception {

        machineInfoService.modify(machineInfoDTO, imgFile);
        return "redirect:/machine_list";
    }
    @GetMapping("/machine_remove")
    public String removeProc(Model model) throws Exception {
        return "redirect:/machine_list";
    }
}