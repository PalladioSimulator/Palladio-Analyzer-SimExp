package de.fzi.srp.simulatedexperience.prism.wrapper.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class TempDirectory implements AutoCloseable {
    private final Path path;

    public TempDirectory(String prefix) throws IOException {
        this.path = Files.createTempDirectory(prefix);
    }

    public Path path() {
        return path;
    }

    public Path resolve(String other) {
        return path.resolve(other);
    }

    @Override
    public void close() throws IOException {
        deleteRecursively(path);
    }

    private void deleteRecursively(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }
}
