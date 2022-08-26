package com.labs.devo.tfidf.domain;

import com.labs.devo.tfidf.domain.model.Frequencies;
import com.labs.devo.tfidf.domain.model.TfIdf;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TfIdfCalculator {

    public Double termFrequency(String term, Frequencies frequencies) {
        if(frequencies.containsWord(term)) {
            return frequencies.frequency(term) /
                    frequencies.getNumTerms().doubleValue();
        }
        return 0D;
    }

    public Double inverseDocumentFrequency(String term, List<Frequencies> frequenciesForAllDocs) {
        if(frequenciesForAllDocs.isEmpty()) {
            return 0D;
        }
        Long numDocsContainingTerm = frequenciesForAllDocs.stream()
                .filter(freqs -> freqs.containsWord(term))
                .count();
        try {
            return Math.log10(frequenciesForAllDocs.size() / numDocsContainingTerm);
        } catch (ArithmeticException e) {
            return 0D;
        }
    }

    public Map<String, TfIdf> tfidf(String term,
                                    List<Frequencies> frequenciesForAllDocs,
                                    Double inverseDocumentFrequency) {
        if(frequenciesForAllDocs.isEmpty()) {
            return Map.of();
        }
        final List<String> terms = List.of(term);
        return frequenciesForAllDocs
                .stream()
                .map(frequencies -> new TfIdf(
                        frequencies.getDocName(),
                        terms,
                        termFrequency(term, frequencies) * inverseDocumentFrequency))
                .collect(Collectors.toMap(TfIdf::docName, Function.identity()));
    }

    public List<TfIdf> tfidfForMultipleTerms(List<String> terms, List<Frequencies> frequenciesForAllDocs) {
        final Map<String, TfIdf> total = frequenciesForAllDocs.stream()
                .map(Frequencies::getDocName)
                .collect(Collectors.toMap(
                        Function.identity(),
                        (docName) -> new TfIdf(docName, List.of(), 0D)));
        for (String term : terms) {
            final Double inverseDocumentFrequency = inverseDocumentFrequency(term, frequenciesForAllDocs);
            for (String docName : total.keySet()) {
                final Map<String, TfIdf> tfIdfForTerm = tfidf(term, frequenciesForAllDocs, inverseDocumentFrequency);
                final TfIdf currentTfIdf = total.get(docName);
                final List<String> newTerms = Stream.concat(
                            currentTfIdf.terms().stream(),
                            Stream.of(term))
                        .collect(Collectors.toList());
                final Double newScore = tfIdfForTerm.get(docName).score() + currentTfIdf.score();
                total.put(docName, new TfIdf(docName, newTerms, newScore ));
            }
        }
        return total.values().stream().toList();
    }
}
