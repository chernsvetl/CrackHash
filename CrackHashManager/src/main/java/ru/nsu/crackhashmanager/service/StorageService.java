package ru.nsu.crackhashmanager.service;

import com.roytuts.jaxb.CrackHashManagerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.crackhashmanager.client.ManagerApiClient;
import ru.nsu.crackhashmanager.dto.CrackStatus;
import ru.nsu.crackhashmanager.dto.CrackStatusDTO;
import ru.nsu.crackhashmanager.dto.JobData;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StorageService {

    private Duration crackJobTimeout;

    private Map<UUID, CopyOnWriteArrayList<UUID>> requestToJob;

    private Map<Map.Entry<UUID, Integer>, UUID> partNumberToJob;

    private Map<UUID, JobData> jobToData;

    private Map<UUID, CrackStatus> requestToStatus;

    @Value("${crack-hash.manager.job-timeout}")
    private String crackJobTimeoutString;

    @Value("${crack-hash.manager.workers}")
    private Integer workersNumber;

    @Value("${crack-hash.manager.alphabet}")
    private String alphabetForCrack;

    @Autowired
    private ManagerApiClient managerApiClient;

    @PostConstruct
    public void init() {
        crackJobTimeout = Duration.parse(crackJobTimeoutString);

        requestToJob = new ConcurrentHashMap<>();
        partNumberToJob = new ConcurrentHashMap<>();
        jobToData = new ConcurrentHashMap<>();
        requestToStatus = new ConcurrentHashMap<>();
    }

    public UUID fillCrackData(String hash, Integer maxLength) {
        UUID requestId = UUID.randomUUID();

        while (requestToJob.containsKey(requestId)) {
            requestId = UUID.randomUUID();
        }

        CopyOnWriteArrayList<UUID> jobIds = new CopyOnWriteArrayList<>();
        for (int i = 1; i <= workersNumber; ++i) {
            UUID jobId = UUID.randomUUID();

            while (jobIds.contains(jobId)) {
                jobId = UUID.randomUUID();
            }

            jobIds.add(jobId);
            partNumberToJob.put(Map.entry(requestId, i), jobId);
            jobToData.put(jobId, new JobData(Instant.now(), CrackStatus.IN_PROGRESS, null));

            managerApiClient.sendInternalWorkerResponse(
                generateRequest(
                    hash,
                    maxLength,
                    requestId
                )
            );
        }

        log.info("requestId={} is now associated with jobIds={}", requestId, jobIds);

        requestToJob.put(requestId, jobIds);
        requestToStatus.put(requestId, CrackStatus.IN_PROGRESS);

        return requestId;
    }

    public CrackStatusDTO getCrackStatus(UUID requestId) {
        refreshCrackStatuses();

        CrackStatus status = requestToStatus.get(requestId);
        if (status == null) {
            return new CrackStatusDTO(CrackStatus.ERROR, null);
        }

        List<String> resultData = requestToJob.get(requestId).stream()
            .map(jobToData::get).map(JobData::getData)
            .filter(Objects::nonNull)
            .flatMap(List::stream).collect(Collectors.toList());

        return new CrackStatusDTO(status, status.equals(CrackStatus.READY) ? resultData : null);
    }

    public void refreshCrackStatuses() {
        for (Map.Entry<UUID, JobData> entry : jobToData.entrySet()) {
            boolean isInProgress = entry.getValue().getStatus().equals(CrackStatus.IN_PROGRESS);
            boolean isTimedOut = entry.getValue().getStartedAt().plus(crackJobTimeout).isBefore(Instant.now());

            if (isInProgress && isTimedOut) {
                entry.getValue().setStatus(CrackStatus.ERROR);
            }
        }

        for (Map.Entry<UUID, CrackStatus> entry : requestToStatus.entrySet()) {
            List<UUID> jobsOfRequest = requestToJob.get(entry.getKey());

            entry.setValue(
                jobsOfRequest.stream()
                    .map(jobToData::get)
                    .map(JobData::getStatus)
                    .reduce(CrackStatus.READY, CrackStatus.statusConjunction)
            );
        }
    }

    public void updateCrackStatus(UUID requestId, Integer partNumber, List<String> result) {
        UUID jobId = partNumberToJob.get(Map.entry(requestId, partNumber));

        jobToData.computeIfPresent(
            jobId,
            (k, v) -> new JobData(v.getStartedAt(), CrackStatus.READY, result)
        );
    }

    private CrackHashManagerRequest generateRequest(String hash, Integer maxLength, UUID requestId) {
        CrackHashManagerRequest request = new CrackHashManagerRequest();

        request.setRequestId(requestId.toString());
        request.setHash(hash);
        request.setMaxLength(maxLength);
        request.setPartNumber(1);
        request.setPartCount(1);

        CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();
        alphabet.getSymbols().addAll(
            Arrays.stream(alphabetForCrack.split("")).collect(Collectors.toList())
        );

        request.setAlphabet(alphabet);

        return request;
    }

}
