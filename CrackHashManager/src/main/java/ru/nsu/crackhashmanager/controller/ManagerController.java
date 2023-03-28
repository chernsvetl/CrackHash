package ru.nsu.crackhashmanager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.crackhashmanager.dto.CrackStatusDTO;
import ru.nsu.crackhashmanager.dto.HashDTO;
import ru.nsu.crackhashmanager.dto.RequestIdDTO;
import ru.nsu.crackhashmanager.service.ManagerService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @PostMapping(value = "/hash/crack")
    public RequestIdDTO crackHash(@RequestBody HashDTO hashDTO) {
        log.info("crackHash(), payload={}", hashDTO);

        return managerService.crackHash(hashDTO);
    }

    @GetMapping(value = "/hash/status")
    public CrackStatusDTO getCrackStatus(@RequestParam UUID requestId) {
        log.info("getCrackStatus(), requestId={}", requestId);

        return managerService.getCrackStatus(new RequestIdDTO(requestId));
    }

}
