package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.process.IPrismObserver;

public class FileDumperPrismObserver implements IPrismObserver {
    private static final Logger LOGGER = Logger.getLogger(FileDumperPrismObserver.class);

    private final Path prismLogFolder;

    private int counter = 0;

    public FileDumperPrismObserver(Path prismLogFolder) {
        this.prismLogFolder = prismLogFolder;
    }

    @Override
    public void onContext(PrismContext context, String sasName) {
        Path dumpPath = createDumpPath(counter);
        counter++;

        try (BufferedWriter writer = Files.newBufferedWriter(dumpPath)) {
            writeDump(writer, context);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    private void writeDump(BufferedWriter writer, PrismContext context) {
        try (PrintWriter pw = new PrintWriter(writer)) {
            String timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(LocalDateTime.now());
            pw.println(String.format("time: %s", timeStamp));
            pw.println("Prism module:");
            pw.println("Prism property: " + context.propertyFileContent);
            pw.println(context.moduleFileContent);
        }
    }

    private Path createDumpPath(int count) {
        return prismLogFolder.resolve(String.format("prism_%04d.dump", count));
    }
}
