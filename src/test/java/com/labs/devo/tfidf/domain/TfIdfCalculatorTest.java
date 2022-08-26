package com.labs.devo.tfidf.domain;

import com.labs.devo.tfidf.domain.model.Frequencies;
import com.labs.devo.tfidf.domain.model.TfIdf;
import com.labs.devo.tfidf.domain.TfIdfCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class TfIdfCalculatorTest {

    private static final String TERM_1 = "term1";
    private static final String TERM_2 = "term2";
    private static final String A_WORD = "a_word";
    private static final String ANOTHER_WORD = "another_word";
    private static final String DOCUMENT_1_NAME = "doc1";
    private static final String DOCUMENT_2_NAME = "doc2";

    @Test
    public void termFrequencyShouldReturn0WhenNoWords() {
        Frequencies frequencies = new Frequencies(DOCUMENT_1_NAME, List.of());
        Double expectedTermFrequency = 0D;

        Double termFrequency = new TfIdfCalculator().termFrequency(TERM_1, frequencies);

        assertThat(termFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void termFrequencyShouldReturn1WhenTermIsTheOnlyWord() {
        Frequencies frequencies = new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1));
        Double expectedTermFrequency = 1D;

        Double termFrequency = new TfIdfCalculator().termFrequency(TERM_1, frequencies);

        assertThat(termFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void termFrequencyShouldReturn05When2DifferentWordsAndTermIsOneOfThem() {
        Frequencies frequencies = new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1, ANOTHER_WORD));
        Double expectedTermFrequency = 0.5D;

        Double termFrequency = new TfIdfCalculator().termFrequency(TERM_1, frequencies);

        assertThat(termFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void termFrequencyShouldReturn05When4DifferentWordsAndTermIsTwice() {
        Frequencies frequencies = new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1, A_WORD, TERM_1, ANOTHER_WORD));
        Double expectedTermFrequency = 0.5D;

        Double termFrequency = new TfIdfCalculator().termFrequency(TERM_1, frequencies);

        assertThat(termFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void inverseDocumentFrequencyShouldReturn0WhenNoDocs() {
        List<Frequencies> frequenciesForAllDocs = List.of();
        Double expectedTermFrequency = 0D;

        Double inverseDocumentFrequency = new TfIdfCalculator().inverseDocumentFrequency(TERM_1, frequenciesForAllDocs);

        assertThat(inverseDocumentFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void inverseDocumentFrequencyShouldReturn0WhenTermNotFoundInDocs() {
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(A_WORD))
        );
        Double expectedTermFrequency = 0D;

        Double inverseDocumentFrequency = new TfIdfCalculator().inverseDocumentFrequency(TERM_1, frequenciesForAllDocs);

        assertThat(inverseDocumentFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void inverseDocumentFrequencyShouldReturn0WhenOneDocContainingTheTerm() {
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1))
        );
        Double expectedTermFrequency = 0D;

        Double inverseDocumentFrequency = new TfIdfCalculator().inverseDocumentFrequency(TERM_1, frequenciesForAllDocs);

        assertThat(inverseDocumentFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void inverseDocumentFrequencyShouldReturn03WhenOneDocOutOf2ContainingTheTerm() {
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1)),
                new Frequencies(DOCUMENT_2_NAME, List.of(A_WORD))
        );
        Double expectedTermFrequency = 0.3010299956639812D;

        Double inverseDocumentFrequency = new TfIdfCalculator().inverseDocumentFrequency(TERM_1, frequenciesForAllDocs);

        assertThat(inverseDocumentFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void inverseDocumentFrequencyShouldReturn0WhenAllDocsContainingTheTerm() {
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1)),
                new Frequencies(DOCUMENT_2_NAME, List.of(TERM_1))
        );
        Double expectedTermFrequency = 0D;

        Double inverseDocumentFrequency = new TfIdfCalculator().inverseDocumentFrequency(TERM_1, frequenciesForAllDocs);

        assertThat(inverseDocumentFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void inverseDocumentFrequencyShouldReturn0WhenOneDocOutOf10ContainingTheTerm() {
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1)),
                new Frequencies(DOCUMENT_2_NAME, List.of(A_WORD)),
                new Frequencies("doc3", List.of(A_WORD)),
                new Frequencies("doc4", List.of(A_WORD)),
                new Frequencies("doc5", List.of(A_WORD)),
                new Frequencies("doc6", List.of(A_WORD)),
                new Frequencies("doc7", List.of(A_WORD)),
                new Frequencies("doc8", List.of(A_WORD)),
                new Frequencies("doc9", List.of(A_WORD)),
                new Frequencies("doc10", List.of(A_WORD))
        );
        Double expectedTermFrequency = 1D;

        Double inverseDocumentFrequency = new TfIdfCalculator().inverseDocumentFrequency(TERM_1, frequenciesForAllDocs);

        assertThat(inverseDocumentFrequency, equalTo(expectedTermFrequency));
    }

    @Test
    public void tfidfShouldReturn0WhenNoDocs() {
        List<Frequencies> frequenciesForAllDocs = List.of();
        Double inverseDocumentFrequency = 0D;
        TfIdfCalculator calculator = new TfIdfCalculator();

        Map<String, TfIdf> scores = calculator.tfidf(TERM_1, frequenciesForAllDocs, inverseDocumentFrequency);

        assertThat(scores.size(), equalTo(0));
    }

    @Test
    public void tfidfShouldReturn0WhenNoDocContainingTheTerm() {
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(A_WORD))
        );
        Double inverseDocumentFrequency = 0D;
        Double expectedScoreForDoc1 = 0D;
        TfIdfCalculator calculator = new TfIdfCalculator();

        Map<String, TfIdf> scores = calculator.tfidf(TERM_1, frequenciesForAllDocs, inverseDocumentFrequency);

        assertThat(scores.size(), equalTo(frequenciesForAllDocs.size()));
        assertThat(scores.get(DOCUMENT_1_NAME).docName(), equalTo(DOCUMENT_1_NAME));
        assertThat(scores.get(DOCUMENT_1_NAME).terms().get(0), equalTo(TERM_1));
        assertThat(scores.get(DOCUMENT_1_NAME).score(), equalTo(expectedScoreForDoc1));
    }

    @Test
    public void tfidfShouldReturn0WhenAllDocsContainingTheTerm() {
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1)),
                new Frequencies(DOCUMENT_2_NAME, List.of(TERM_1))
        );
        Double inverseDocumentFrequency = 0D;
        Double expectedScore = 0D;
        TfIdfCalculator calculator = new TfIdfCalculator();

        Map<String, TfIdf> scores = calculator.tfidf(TERM_1, frequenciesForAllDocs, inverseDocumentFrequency);

        assertThat(scores.size(), equalTo(frequenciesForAllDocs.size()));
        assertThat(scores.get(DOCUMENT_1_NAME).docName(), equalTo(DOCUMENT_1_NAME));
        assertThat(scores.get(DOCUMENT_1_NAME).terms().get(0), equalTo(TERM_1));
        assertThat(scores.get(DOCUMENT_1_NAME).score(), equalTo(expectedScore));
        assertThat(scores.get(DOCUMENT_2_NAME).docName(), equalTo(DOCUMENT_2_NAME));
        assertThat(scores.get(DOCUMENT_2_NAME).terms().get(0), equalTo(TERM_1));
        assertThat(scores.get(DOCUMENT_2_NAME).score(), equalTo(expectedScore));

    }

    @Test
    public void tfidfShouldReturn0129WhenOneDocOutOf2Containing3TimesTheTerm() {
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1, A_WORD, ANOTHER_WORD, ANOTHER_WORD, TERM_1, TERM_1, A_WORD)),
                new Frequencies(DOCUMENT_2_NAME, List.of(A_WORD, A_WORD, ANOTHER_WORD))
        );
        Double inverseDocumentFrequency = 0.3010299956639812D;
        Double expectedScoreForDoc1 = 0.12901285528456335D;
        Double expectedScoreForDoc2 = 0D;
        TfIdfCalculator calculator = new TfIdfCalculator();

        Map<String, TfIdf> scores = calculator.tfidf(TERM_1, frequenciesForAllDocs, inverseDocumentFrequency);

        assertThat(scores.size(), equalTo(frequenciesForAllDocs.size()));
        assertThat(scores.get(DOCUMENT_1_NAME).docName(), equalTo(DOCUMENT_1_NAME));
        assertThat(scores.get(DOCUMENT_1_NAME).terms().get(0), equalTo(TERM_1));
        assertThat(scores.get(DOCUMENT_1_NAME).score(), equalTo(expectedScoreForDoc1));
        assertThat(scores.get(DOCUMENT_2_NAME).docName(), equalTo(DOCUMENT_2_NAME));
        assertThat(scores.get(DOCUMENT_2_NAME).terms().get(0), equalTo(TERM_1));
        assertThat(scores.get(DOCUMENT_2_NAME).score(), equalTo(expectedScoreForDoc2));
    }

    @Test
    public void tfidfShouldReturnTheSumOfScoresForAllTermsWhenCalledWithSeveralTerms() {
        List<String> terms = List.of(TERM_1, TERM_2);
        List<Frequencies> frequenciesForAllDocs = List.of(
                new Frequencies(DOCUMENT_1_NAME, List.of(TERM_1, A_WORD, ANOTHER_WORD, ANOTHER_WORD, TERM_1, TERM_1, A_WORD)),
                new Frequencies(DOCUMENT_2_NAME, List.of(TERM_2, A_WORD, ANOTHER_WORD))
        );
        Double expectedScoreForDoc1 = 0.12901285528456335D;
        Double expectedScoreForDoc2 = 0.10034333188799373D;
        TfIdfCalculator calculator = new TfIdfCalculator();

        List<TfIdf> scores = calculator.tfidfForMultipleTerms(terms, frequenciesForAllDocs);

        assertThat(scores.size(), equalTo(frequenciesForAllDocs.size()));

        assertThat(scores, containsInAnyOrder(
                new TfIdf(DOCUMENT_1_NAME, terms, expectedScoreForDoc1),
                new TfIdf(DOCUMENT_2_NAME, terms, expectedScoreForDoc2)));
    }
}
