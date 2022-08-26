package com.labs.devo.tfidf.domain.model;

import com.labs.devo.tfidf.domain.model.Frequencies;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FrequenciesTest {

    private static final String DOCUMENT_1_NAME = "doc1";

    @Test
    public void frequenciesShouldReturnEmptyMapWhenNoWords() {
        List<String> words = List.of();

        Frequencies freqs = new Frequencies(DOCUMENT_1_NAME, words);

        assertThat(freqs.isEmpty(), equalTo(true));
    }

    @Test
    public void frequenciesShouldReturnMapWithOneWordAndFreq1WhenJustOneWord() {
        String word = "word";
        List<String> words = List.of(word);

        Frequencies freqs = new Frequencies(DOCUMENT_1_NAME, words);

        assertThat(freqs.size(), equalTo(1));
        assertThat(freqs.frequency(word), equalTo(1));
    }

    @Test
    public void frequenciesShouldReturnMapWithOneWordAndFreq2WhenJustOneDuplicatedWord() {
        String word = "word";
        List<String> words = List.of(word, word);

        Frequencies freqs = new Frequencies(DOCUMENT_1_NAME, words);

        assertThat(freqs.size(), equalTo(1));
        assertThat(freqs.frequency(word), equalTo(2));
    }

    @Test
    public void frequenciesShouldReturnMapWithSeveralWordsAndFreqWhenMultipleWords() {
        String word1 = "word1";
        String word2 = "word2";
        List<String> words = List.of(word1, word2, word2, word2);

        Frequencies freqs = new Frequencies(DOCUMENT_1_NAME, words);

        assertThat(freqs.size(), equalTo(2));
        assertThat(freqs.frequency(word1), equalTo(1));
        assertThat(freqs.frequency(word2), equalTo(3));
    }
}
