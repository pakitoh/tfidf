package com.labs.devo.tfidf.domain.adapter;

import com.labs.devo.tfidf.domain.adapter.DocumentReader;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

public class DocumentReaderIT {

    @Test
    public void readShouldReturnEmptyWhenNonExistingFile() {
        String fileName = "src/test/resources/nonExistingFile.txt";

        String doc = new DocumentReader().read(fileName);

        assertThat(doc, equalTo(""));
    }

    @Test
    public void readShouldReturnEmptyWhenErrorReadingFile() {
        String fileName = "src/test/resources/";

        String doc = new DocumentReader().read(fileName);

        assertThat(doc, equalTo(""));
    }

    @Test
    public void readShouldReturnDocContentWhenSimpleFile() {
        String fileName = "src/test/resources/simpleFile.txt";
        int expectedSize = 445;
        String expectedContentBeginning = "Lorem ipsum";

        String doc = new DocumentReader().read(fileName);

        assertThat(doc.length(), equalTo(expectedSize));
        assertThat(doc, startsWith(expectedContentBeginning));
    }

    @Test
    public void readShouldReturnDocContentWhenLargeFile() {
        String fileName = "src/test/resources/Moby Dick by Herman Melville.txt";
        String expectedContentBeginning = "\uFEFFThe Project Gutenberg eBook of Moby-Dick";

        String doc = new DocumentReader().read(fileName);

        assertThat(doc, startsWith(expectedContentBeginning));
    }
}
