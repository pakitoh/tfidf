package com.labs.devo.tfidf.domain;

import com.labs.devo.tfidf.domain.model.TfIdf;

import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KBetterSelector {

    private final PriorityQueue<TfIdf> data;

    public KBetterSelector(List<TfIdf> input) {
        data = new PriorityQueue<>(Collections.reverseOrder());
        data.addAll(input);
    }

    public List<TfIdf> topK(int k) {
        return IntStream.range(0, k)
                .mapToObj(i -> data.poll())
                .collect(Collectors.toList());
    }
}
