package ru.nsu.crackhashmanager.service;

import com.roytuts.jaxb.CrackHashWorkerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.nsu.crackhashmanager.dto.CrackStatusDTO;
import ru.nsu.crackhashmanager.dto.HashDTO;
import ru.nsu.crackhashmanager.dto.RequestIdDTO;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@EnableScheduling
public class ManagerService {

    @Autowired
    private StorageService storageService;

    public RequestIdDTO crackHash(HashDTO hashDTO) {
        return new RequestIdDTO(
            storageService.fillCrackData(
                hashDTO.getHash(),
                hashDTO.getMaxLength()
            )
        );
    }

    public CrackStatusDTO getCrackStatus(RequestIdDTO requestIdDTO) {
        return storageService.getCrackStatus(requestIdDTO.getRequestId());
    }

    public void updateCrackStatus(CrackHashWorkerResponse response) {
        storageService.updateCrackStatus(
            UUID.fromString(response.getRequestId()),
            response.getPartNumber(),
            response.getAnswers().getWords()
        );
    }

    @Scheduled(cron = "0 * * * * *")
    public void scheduledUpdateCache() {
        log.info("scheduledUpdateCache(), now={}", Instant.now());

        storageService.refreshCrackStatuses();
    }

}
