package com.labs.devo.tfidf.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frequencies {
    private final Map<String, Integer> data;
    private final Integer numTerms;
    private final String docName;

    public Frequencies(String docName, List<String> words) {
        this.docName = docName;
        this.data =  new HashMap<>();
        words.forEach(this::increaseFrequency);
        this.numTerms = words.size();
    }

    private void increaseFrequency(String word) {
        if (data.containsKey(word)) {
            data.put(word, data.get(word) + 1);
        } else {
            data.put(word, 1);
        }
    }

    public String getDocName() {
        return docName;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    public Integer frequency(String word) {
        return data.get(word);
    }

    public boolean containsWord(String word) {
        return data.containsKey(word);
    }

    public Integer getNumTerms() {
        return numTerms;
    }
}
