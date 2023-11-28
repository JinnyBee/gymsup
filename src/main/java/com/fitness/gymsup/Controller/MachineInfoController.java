package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.MachineInfoDTO;
import com.fitness.gymsup.Service.MachineInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MachineInfoController {
    private final MachineInfoService machineInfoService;

    @GetMapping("/machine_info_register")
    public String machineInfoRegisterForm()throws Exception{
        return "machine/register";
    }

    @PostMapping("/machine_info_register")
    public String machineInfoRegisterProc(MachineInfoDTO machineInfoDTO)throws Exception{
        machineInfoService.register(machineInfoDTO);
        return "machine/register";
    }
}
