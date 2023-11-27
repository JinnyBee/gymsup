package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.ContactDTO;
import com.fitness.gymsup.Service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j

public class ContactController {
    private final ContactService contactService;

    @GetMapping("/contact_register")
    public String contactRegisterForm(Model model) throws Exception{
        ContactDTO contactDTO = new ContactDTO();
        model.addAttribute("contactDTO",contactDTO);

        return "contact/register";
    }

    @PostMapping("/contact_register")
    public String contactRegisterProc(@Valid ContactDTO contactDTO, BindingResult bindingResult,
                                      HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes)throws Exception{
        if (bindingResult.hasErrors()){
            return "contact/register";
        }

        redirectAttributes.addAttribute("errorMessage","문의가 등록되었습니다.");
        contactService.contactRegister(contactDTO,request,principal);
        return "redirect:/";
    }

    @GetMapping("/user_contact")
    public String userContact(Principal principal, HttpServletRequest request, Model model)throws Exception{

        List<ContactDTO> contactDTOS = contactService.userContact(request, principal);

        model.addAttribute("contactDTOS",contactDTOS);
        return "contact/userlist";
    }
    
    @GetMapping("/admin_contact")
    public String adminContact(@PageableDefault(page = 1)Pageable pageable, Model model)throws Exception{
        Page<ContactDTO> contactDTOS = contactService.contactList(pageable);

        int blockLimit = 5;
        int startPage, endPage, prevPage, currentPage, nextPage, lastPage;

        if(contactDTOS.isEmpty()) {
            startPage = 0;
            endPage = 0;
            prevPage = 0;
            currentPage = 0;
            nextPage = 0;
            lastPage = 0;
        } else {
            startPage = (((int)(Math.ceil((double) pageable.getPageNumber()/blockLimit)))-1) * blockLimit + 1;
            //endPage = Math.min(startPage+blockLimit-1, contactDTOS.getTotalPages());
            endPage = ((startPage+blockLimit-1)<contactDTOS.getTotalPages()) ? startPage+blockLimit-1 : contactDTOS.getTotalPages();

            prevPage = contactDTOS.getNumber();
            currentPage = contactDTOS.getNumber() + 1;
            nextPage = contactDTOS.getNumber() + 2;
            lastPage = contactDTOS.getTotalPages();
        }

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("contactDTOS",contactDTOS);

        return "contact/adminlist";
    }
}
