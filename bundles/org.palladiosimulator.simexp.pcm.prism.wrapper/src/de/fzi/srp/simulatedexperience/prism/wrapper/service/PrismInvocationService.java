package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;

import de.fzi.srp.simulatedexperience.prism.wrapper.service.impl.PrismLoader;

public class PrismInvocationService implements PrismService {

    private static final Logger LOGGER = Logger.getLogger(PrismInvocationService.class);

    private final Path javaBinary;

    private int counter = 0;
    private Path prismBinary;
    private Path prismFolder;

    public PrismInvocationService() {
        Path javaHome = Paths.get(System.getProperty("java.home"));
        this.javaBinary = javaHome.resolve("bin")
            .resolve("java");
    }

    @Override
    public void initialise(Path prismFolder, String strategyId) {
        PrismLoader pl = PrismLoader.INSTANCE;
        this.prismBinary = pl.load();
        this.prismFolder = prismFolder;
    }

    @Override
    public PrismResult modelCheck(PrismContext context) {
        String contentKind = context.getKind();
        LOGGER.info("Start prism invocation: " + contentKind);
        long start = System.currentTimeMillis();
        PrismResult result = executeModelCheck(context);
        long end = System.currentTimeMillis();
        LOGGER.info("Stop prism invocation: " + contentKind + ", Elapsed time in seconds: " + ((end - start) / 1000));
        return result;
    }

    private PrismResult executeModelCheck(PrismContext context) {
        int currentCounter = getCounter();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Path modelFile = createModelFile(context, currentCounter);
            Path propertiesFile = createPropertiesFile(context, currentCounter);
            Path resultFile = prismFolder.resolve(buildPrismFileName(currentCounter, "result"));

            List<String> args = new ArrayList<>();
            args.add(prismBinary.toString());
            args.add(modelFile.toString());
            args.add(propertiesFile.toString());
            args.add("-exportresults");
            args.add(resultFile.toString());
            LOGGER.debug(String.format("execute: %s", StringUtils.join(args, " ")));
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(prismBinary.getParent()
                .toFile());
            pb.environment()
                .put("PRISM_JAVA", javaBinary.toString());
            Process p = pb.start();

            Path prismLogPath = prismFolder.resolve("prism.log");
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(prismLogPath, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                bufferedWriter.write(String.format("PRISM invocation #%04d%s", counter, System.lineSeparator()));
                StreamGobbler stdoutReader = new StreamGobbler(p.getInputStream(),
                        lambdaWrapper(s -> bufferedWriter.write(s + System.lineSeparator())));
                Future<?> stdoutFuture = executor.submit(stdoutReader);
                try {
                    List<String> errorLines = new ArrayList<>();
                    StreamGobbler stderrReader = new StreamGobbler(p.getErrorStream(), s -> errorLines.add(s));
                    Future<?> stderrFuture = executor.submit(stderrReader);

                    int rc = p.waitFor();
                    if (rc != 0) {
                        stderrFuture.get();
                        String message = String.format("prism failure (rc=%d):\n%s", rc,
                                StringUtils.join(errorLines, "\n"));
                        throw new RuntimeException(message);
                    }
                } finally {
                    stdoutFuture.get();
                }
            }

            String propertyName = extractPropertyName(context);
            PrismResult prismResult = readPrismResult(resultFile, propertyName);
            return prismResult;
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failure during prism model checking", e);
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private Consumer<String> lambdaWrapper(IOFunction<String> consumer) {
        return s -> {
            try {
                consumer.accept(s);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        };
    }

    private int getCounter() {
        return counter++;
    }

    private String extractPropertyName(PrismContext context) {
        String propertyFileContent = context.getPropertyFileContent();
        String[] result = propertyFileContent.split("\\R", 2);
        String propertyName = result[0];
        return propertyName;
    }

    private PrismResult readPrismResult(Path resultFile, String propertyToCheck) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(resultFile)) {
            PrismResult prismResult = new PrismResult();
            r.readLine();
            String valueString = r.readLine();
            Double value = Double.valueOf(valueString);
            prismResult.addResult(propertyToCheck, value);
            return prismResult;
        }
    }

    private Path createModelFile(PrismContext context, int counter) throws IOException {
        Path modulePath = prismFolder.resolve(buildPrismFileName(counter, "model"));
        try (Writer w = Files.newBufferedWriter(modulePath)) {
            w.write(context.getModuleFileContent());
        }
        return modulePath;
    }

    private Path createPropertiesFile(PrismContext context, int counter) throws IOException {
        Path propertiesPath = prismFolder.resolve(buildPrismFileName(counter, "properties"));
        try (Writer w = Files.newBufferedWriter(propertiesPath)) {
            w.write(context.getPropertyFileContent());
        }
        return propertiesPath;
    }

    private String buildPrismFileName(int counter, String type) {
        return String.format("prism_%04d.%s", counter, type);
    }
}
