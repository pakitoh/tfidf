package com.labs.devo.tfidf.domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class WordExtractorTest {

    @Test
    public void wordsShouldReturnEmptyWhenEmptyDoc() {
        String doc = "";

        List<String> words = new WordExtractor().words(doc);

        assertThat(words, empty());
    }

    @Test
    public void wordsShouldReturn1WordWhenDocWithOneWord() {
        int expectedSize = 1;
        String expectedWord = "word";
        String doc = expectedWord;

        List<String> words = new WordExtractor().words(doc);

        assertThat(words.size(), equalTo(expectedSize));
        assertThat(words.get(0), equalTo(expectedWord));
    }

    @Test
    public void wordsShouldReturnLowercaseWordWhenDocWithUppercaseWord() {
        int expectedSize = 1;
        String expectedWord = "word";
        String doc = expectedWord.toUpperCase();

        List<String> words = new WordExtractor().words(doc);

        assertThat(words.size(), equalTo(expectedSize));
        assertThat(words.get(0), equalTo(expectedWord));
    }

    @Test
    public void wordsShouldReturnSeveralWordsWhenDocWithMoreThanOneWord() {
        int expectedSize = 5;
        String[] expectedWords = {"this", "file", "contains", "several", "words"};
        String doc = Arrays.stream(expectedWords).collect(Collectors.joining(" "));

        List<String> words = new WordExtractor().words(doc);

        assertThat(words.size(), equalTo(expectedSize));
        assertThat(words, contains(expectedWords));
    }

    @Test
    public void wordsShouldReturnSeveralWordsWhenDocContainsWordsAndPunctuation() {
        int expectedSize = 8;
        String[] expectedWords = {"this", "file", "contains", "symbols", "spaces", "and", "several", "words"};
        String doc = "\tThis file\ncontains: symbols, spaces and several words.";

        List<String> words = new WordExtractor().words(doc);

        assertThat(words.size(), equalTo(expectedSize));
        assertThat(words, contains(expectedWords));
    }

}
