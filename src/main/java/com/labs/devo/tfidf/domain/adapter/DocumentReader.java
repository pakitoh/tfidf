package com.labs.devo.tfidf.domain.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocumentReader {
    private static final Logger logger = LoggerFactory.getLogger(DocumentReader.class);
    private static final String DEFAULT_DOC = "";

    public String read(String fileName) {
        return readContentFromPath(Paths.get(fileName));
    }

    private String readContentFromPath(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            logger.error(String.format("Error reading %s", path.toString()), e);
            return DEFAULT_DOC;
        }
    }
}

