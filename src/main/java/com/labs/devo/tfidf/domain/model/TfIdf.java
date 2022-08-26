package com.labs.devo.tfidf.domain.model;

import java.util.List;
import java.util.stream.Collectors;

public record TfIdf(String docName, List<String> terms, Double score) implements Comparable<TfIdf> {

    @Override
    public String toString() {
        return String.format("TfIdf { docName='%s', terms='%s', score='%1.4f' }",
                docName,
                terms.stream().collect(Collectors.joining(" ")),
                score);
    }

    @Override
    public int compareTo(TfIdf o) {
        return score().compareTo(o.score());
    }
}
