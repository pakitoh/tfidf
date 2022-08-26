package com.labs.devo.tfidf.app;

import com.labs.devo.tfidf.app.TfIdfService;
import com.labs.devo.tfidf.domain.adapter.DocumentReader;
import com.labs.devo.tfidf.domain.adapter.FilesWrapper;
import com.labs.devo.tfidf.domain.model.TfIdf;
import com.labs.devo.tfidf.domain.TfIdfCalculator;
import com.labs.devo.tfidf.domain.WordExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TfIdfServiceTest {

    private static final int NUM_RESULTS = 2;
    private static final String DIR = "path/to/dir/";
    private static final String TERM_1 = "term1";
    private static final String TERM_2 = "term2";
    private static final String DOCUMENT_1_NAME = "doc1";
    private static final String DOCUMENT_1_PATH = DIR + DOCUMENT_1_NAME;
    private static final String DOC_1 = "doc1Content";
    private static final String WORD_1 = "word1";
    private static final String WORD_2 = "word2";
    private static final String DOCUMENT_2_NAME = "doc2";
    private static final String DOCUMENT_2_PATH = DIR + DOCUMENT_2_NAME;
    private static final String DOC_2 = "doc2Content";
    private static final Double SCORE_D1 = 0D;
    private static final Double SCORE_D2 = 0.1D;

    @Mock
    DocumentReader documentReader;

    @Mock
    WordExtractor wordExtractor;

    @Mock
    TfIdfCalculator calculator;

    @Mock
    FilesWrapper files;

    @InjectMocks
    TfIdfService tfIdfService;

    @Test
    public void computeIdfTfShouldCallCollaboratorsWhenHappyPath() throws Exception {
        List<String> terms = List.of(TERM_1, TERM_2);
        givenADirWith2Docs();
        TfIdf tfIdfDoc1 = new TfIdf(DOCUMENT_1_NAME, terms, SCORE_D1);
        TfIdf tfIdfDoc2 = new TfIdf(DOCUMENT_2_NAME, terms, SCORE_D2);
        when(calculator.tfidfForMultipleTerms(eq(terms), any()))
                .thenReturn(List.of(tfIdfDoc1, tfIdfDoc2));

        List<TfIdf> result = tfIdfService.computeTfidf(DIR, terms, NUM_RESULTS);

        assertThat(result.size(), equalTo(NUM_RESULTS));
        assertThat(result, containsInAnyOrder(tfIdfDoc1, tfIdfDoc2));
    }

    private Path givenADoc() throws Exception {
        Path doc1Path = mock(Path.class);
        when(doc1Path.toString()).thenReturn(DOCUMENT_1_PATH);
        Path doc1File = mock(Path.class);
        when(doc1Path.getFileName()).thenReturn(doc1File);
        when(doc1File.toString()).thenReturn(DOCUMENT_1_NAME);
        when(documentReader.read(DOCUMENT_1_PATH)).thenReturn(DOC_1);
        when(wordExtractor.words(DOC_1)).thenReturn(List.of(WORD_1, WORD_2));
        return doc1Path;
    }

    private Path givenAnotherDoc() {
        Path doc2Path = mock(Path.class);
        when(doc2Path.toString()).thenReturn(DOCUMENT_2_PATH);
        Path doc2File = mock(Path.class);
        when(doc2Path.getFileName()).thenReturn(doc2File);
        when(doc2File.toString()).thenReturn(DOCUMENT_2_NAME);
        when(documentReader.read(DOCUMENT_2_PATH)).thenReturn(DOC_2);
        when(wordExtractor.words(DOC_2)).thenReturn(List.of(WORD_1));
        return doc2Path;
    }

    private void givenADirWith2Docs() throws Exception {
        Path dirPath = mock(Path.class);
        when(files.path(DIR)).thenReturn(dirPath);
        when(files.isDirectory(dirPath)).thenReturn(true);
        List<Path> docList = List.of(givenADoc(), givenAnotherDoc());
        when(files.list(dirPath)).thenReturn(docList.stream());
    }
}
