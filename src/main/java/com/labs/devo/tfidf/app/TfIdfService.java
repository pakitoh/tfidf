package com.labs.devo.tfidf.app;

import com.labs.devo.tfidf.domain.adapter.DocumentReader;
import com.labs.devo.tfidf.domain.adapter.FilesWrapper;
import com.labs.devo.tfidf.domain.*;
import com.labs.devo.tfidf.domain.model.Frequencies;
import com.labs.devo.tfidf.domain.model.TfIdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TfIdfService {
    private static final Logger logger = LoggerFactory.getLogger(TfIdfService.class);

    private final DocumentReader documentReader;
    private final WordExtractor wordExtractor;
    private final TfIdfCalculator calculator;
    private final FilesWrapper files;

    private Map<String, Frequencies> frequenciesCache;

    public TfIdfService() {
        this.documentReader = new DocumentReader();
        this.wordExtractor = new WordExtractor();
        this.calculator = new TfIdfCalculator();
        this.files = new FilesWrapper();
        this.frequenciesCache = new HashMap<>();
    }

    public TfIdfService(DocumentReader documentReader,
                        WordExtractor wordExtractor,
                        TfIdfCalculator calculator,
                        FilesWrapper files) {
        this.documentReader = documentReader;
        this.wordExtractor = wordExtractor;
        this.calculator = calculator;
        this.files = files;
        this.frequenciesCache = new HashMap<>();
    }

    public List<TfIdf> computeTfidf(String dir, List<String> terms, int numResults) {
        logger.info(String.format("Computing the %d better scores of Tf-Idf in %s for the terms: %s",
                numResults, dir, terms.stream().collect(Collectors.joining(" "))));
        long initTime = System.currentTimeMillis();
        List<Path> docPaths = getDocs(dir);
        wordFrequenciesForDocs(docPaths);
        List<TfIdf> totalResults = calculateAllTfidf(
                terms.stream().map(String::toLowerCase).collect(Collectors.toList()),
                frequenciesCache.values().stream().toList());
        List<TfIdf> result = getKBetter(numResults, totalResults);
        result.forEach(System.out::println);
        logger.debug(String.format("TOTAL time consumed: %d", System.currentTimeMillis() - initTime));
        return result;
    }

    private List<Path> getDocs(String dir) {
        Path path = files.path(dir);
        logger.debug(String.format("Is %s a valid folder? %s", dir, files.isDirectory(path)));
        List<Path> docPaths = null;
        try {
            docPaths = files
                    .list(path)
                    .filter(docPath -> !files.isDirectory(docPath))
                    .collect(Collectors.toList());
            logger.debug(String.format("Docs found:\n\t%s",
                    docPaths.stream()
                    .map(docPath -> docPath.getFileName().toString())
                    .collect(Collectors.joining("\n\t"))));
        } catch (IOException e) {
            logger.error("Error listing docs", e);
        }
        return docPaths;
    }

    private void wordFrequenciesForDocs(List<Path> docPaths) {
        logger.debug("Starting to process word frequencies:");
        long initAllTime = System.currentTimeMillis();
        docPaths
                .parallelStream()
                .filter(docPath -> !frequenciesCache.containsKey(docPath.getFileName().toString()))
                .map(docPath -> {
                    long initTime = System.currentTimeMillis();
                    Frequencies freqs = wordFrequenciesForDoc(docPath);
                    logger.debug(String.format("Time consumed computing word frequencies in %s: %d",
                            docPath.getFileName(), System.currentTimeMillis() - initTime));
                    return freqs;
                })
                .forEach(freqs -> frequenciesCache.put(freqs.getDocName(), freqs));
        logger.debug(String.format("Time consumed computing word frequencies for ALL docs: %d",
                System.currentTimeMillis() - initAllTime));
    }

    private Frequencies wordFrequenciesForDoc(Path docPath) {
        long initReadTime = System.currentTimeMillis();
        final String doc = documentReader.read(docPath.toString());
        logger.debug(String.format("Time consumed reading %s: %d",
                docPath.toString(),
                System.currentTimeMillis() - initReadTime));
        final String fileName = docPath.getFileName().toString();
        long initExtractingTime = System.currentTimeMillis();
        final List<String> words = wordExtractor.words(doc);
        logger.debug(String.format("Time consumed extracting words from %s: %d",
                fileName,
                System.currentTimeMillis() - initExtractingTime));
        Frequencies freqs = new Frequencies(fileName, words);
        logger.debug(String.format("%s contains %s words", fileName, freqs.getNumTerms()));
        return freqs;
    }

    private List<TfIdf> calculateAllTfidf(List<String> terms, List<Frequencies> frequenciesForAllDocs) {
        logger.debug("Starting to calculate tfidf");
        long initTime = System.currentTimeMillis();
        List<TfIdf> result = calculator.tfidfForMultipleTerms(terms, frequenciesForAllDocs);
        logger.debug(String.format("Time consumed calculating TfIdf for all terms and docs: %d",
                System.currentTimeMillis() - initTime));
        return result;
    }

    private List<TfIdf> getKBetter(int k, List<TfIdf> result) {
        logger.debug("Choosing K better");
        long initKTime = System.currentTimeMillis();
        List<TfIdf> bestResults = new KBetterSelector(result).topK(k);
        bestResults.forEach(tfIdf -> logger.debug(String.format("RESULT %s", tfIdf)));
        logger.debug(String.format("Time consumed choosing K better: %d",
                System.currentTimeMillis() - initKTime));
        return bestResults;
    }
}
