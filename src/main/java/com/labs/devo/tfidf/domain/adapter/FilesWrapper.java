package com.labs.devo.tfidf.domain.adapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FilesWrapper {

    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    public Stream<Path> list(Path dir) throws IOException {
        return Files.list(dir);
    }

    public Path path(String dir) {
        return Paths.get(dir);
    }
}
