package ru.nsu.crackhashworker.client;

import com.roytuts.jaxb.CrackHashWorkerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class WorkerApiClient {

    private static final String internalHashCrackRequestPath = "/internal/api/manager/hash/crack/request";

    @Value("${crack-hash.manager.host}")
    private String managerHost;

    @Autowired
    private RestTemplate restTemplate;

    public void sendInternalManagerResponse(CrackHashWorkerResponse response) {
        log.info("Sending info to manager endpoint={}", managerHost + internalHashCrackRequestPath);

        try {
            restTemplate.patchForObject(managerHost + internalHashCrackRequestPath, response, String.class);
        } catch (RestClientResponseException e) {
            log.error("An HTTP error has occurred when trying to connect {}", managerHost + internalHashCrackRequestPath);

            e.printStackTrace();
        }
    }

}
