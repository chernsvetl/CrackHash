package ru.nsu.crackhashworker.service;

import com.roytuts.jaxb.CrackHashManagerRequest;
import com.roytuts.jaxb.CrackHashWorkerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.crackhashworker.client.WorkerApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkerService {

    private final BiFunction<String, String, Boolean> hashValidating = (word, hash) -> DigestUtils.md5Hex(word).equals(hash);

    @Autowired
    private WorkerApiClient workerApiClient;

    public void decryptHash(CrackHashManagerRequest request) {
        ICombinatoricsVector<String> alphabetVector = CombinatoricsFactory.createVector(request.getAlphabet().getSymbols());
        List<String> matchingWords = new ArrayList<>();

        for (int i = 1; i <= request.getMaxLength(); ++i) {
            Generator<String> generator = CombinatoricsFactory.createPermutationWithRepetitionGenerator(alphabetVector, i);
            long currentCardinality = getCardinality(request.getAlphabet(), i);

            matchingWords.addAll(
                processCurrentLengthByBatches(
                    generator,
                    currentCardinality,
                    request.getHash()
                )
            );
        }

        log.info("Processed worker task, found words={}", matchingWords);

        CrackHashWorkerResponse response = generateResponse(matchingWords, request);

        workerApiClient.sendInternalManagerResponse(response);
    }

    private long getCardinality(CrackHashManagerRequest.Alphabet alphabet, int length) {
        long currentCardinality = 1;

        for (int j = 1; j <= length; ++j) {
            currentCardinality *= alphabet.getSymbols().size();
        }

        return currentCardinality;
    }

    private List<String> processCurrentLengthByBatches(Generator<String> generator, long cardinality, String hash) {
        List<String> matchingWords = new ArrayList<>();

        for (int j = 0; j < cardinality; j += 10) {
            List<String> wordsBatch = generator.generateObjectsRange(j, Math.min(j + 9, cardinality))
                .stream()
                .map(ICombinatoricsVector::getVector)
                .map(it -> String.join("", it))
                .collect(Collectors.toList());

            matchingWords.addAll(
                wordsBatch.stream()
                    .filter(it -> hashValidating.apply(it, hash))
                    .collect(Collectors.toList())
            );
        }

        log.info("Processed cardinality={}, got result list={}", cardinality, matchingWords);

        return matchingWords;
    }

    private CrackHashWorkerResponse generateResponse(List<String> words, CrackHashManagerRequest request) {
        CrackHashWorkerResponse response = new CrackHashWorkerResponse();

        CrackHashWorkerResponse.Answers answers = new CrackHashWorkerResponse.Answers();
        answers.getWords().addAll(words);

        response.setRequestId(request.getRequestId());
        response.setPartNumber(request.getPartNumber());

        response.setAnswers(answers);

        return response;
    }

}
