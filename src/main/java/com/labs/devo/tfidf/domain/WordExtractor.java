package com.labs.devo.tfidf.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WordExtractor {
    public List<String> words(String doc) {
        return Arrays
                .stream(doc.split("\\s|\\p{Punct}"))
                .filter(word -> !word.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}
