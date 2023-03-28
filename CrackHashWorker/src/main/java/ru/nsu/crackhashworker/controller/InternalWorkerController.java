package ru.nsu.crackhashworker.controller;

import com.roytuts.jaxb.CrackHashManagerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.crackhashworker.service.WorkerService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping(value = "/internal/api/worker")
public class InternalWorkerController {

    @Autowired
    private WorkerService workerService;

    @PostMapping(
        value = "/hash/crack/task",
        consumes = MediaType.APPLICATION_XML_VALUE
    )
    public String crackHashRequest(@RequestBody CrackHashManagerRequest request) {
        log.info("crackHashRequest(), hash={}, partNumber={}, partCount={}",
            request.getHash(), request.getPartNumber(), request.getPartCount());

        CompletableFuture.runAsync(() -> workerService.decryptHash(request));

        return "Successfully sent";
    }

}
