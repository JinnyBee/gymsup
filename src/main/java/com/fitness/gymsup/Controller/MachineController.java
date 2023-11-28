package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.MachineUsageDTO;
import com.fitness.gymsup.Service.MachineUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MachineController {

    private final MachineUsageService machineUsageService;

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
        return "machine/about";
    }
    @GetMapping("/machine_detail")
    public String detailForm(Model model) throws Exception {
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
    @GetMapping("/machine_modify")
    public String modifyForm(Model model) throws Exception {
        return "machine/modify";
    }
    @PostMapping("/machine_modify")
    public String modifyProc(Model model) throws Exception {
        return "redirect:/machine_list";
    }
    @GetMapping("/machine_remove")
    public String removeProc(Model model) throws Exception {
        return "redirect:/machine_list";
    }
}