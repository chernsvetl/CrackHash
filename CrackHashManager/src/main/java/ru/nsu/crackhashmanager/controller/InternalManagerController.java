package ru.nsu.crackhashmanager.controller;

import com.roytuts.jaxb.CrackHashWorkerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.crackhashmanager.service.ManagerService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping(value = "/internal/api/manager")
public class InternalManagerController {

    @Autowired
    private ManagerService managerService;

    @PatchMapping(
        value = "/hash/crack/request",
        consumes = MediaType.APPLICATION_XML_VALUE
    )
    public String updateCacheStatus(@RequestBody CrackHashWorkerResponse response) {
        log.info("updateCacheStatus(), requestId = {}, words={}", response.getRequestId(), response.getAnswers().getWords());

        CompletableFuture.runAsync(
            () -> managerService.updateCrackStatus(response)
        );

        return "Successfully sent";
    }

}
