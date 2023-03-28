package ru.nsu.crackhashmanager.client;

import com.roytuts.jaxb.CrackHashManagerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ManagerApiClient {

    private static final String internalHashCrackTaskPath = "/internal/api/worker/hash/crack/task";

    @Value("${crack-hash.worker.host}")
    private String workerHost;

    @Autowired
    private RestTemplate restTemplate;

    public void sendInternalWorkerResponse(CrackHashManagerRequest request) {
        log.info("Sending info to manager endpoint={}", workerHost + internalHashCrackTaskPath);

        try {
            restTemplate.postForObject(workerHost + internalHashCrackTaskPath, request, String.class);
        } catch (RestClientResponseException e) {
            log.error("An HTTP error has occurred when trying to connect {}", workerHost + internalHashCrackTaskPath);

            e.printStackTrace();
        }
    }

}
