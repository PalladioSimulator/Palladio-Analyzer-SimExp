package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fzi.srp.simulatedexperience.prism.wrapper.service.impl.PrismLoader;
import de.fzi.srp.simulatedexperience.prism.wrapper.service.impl.TempDirectory;

public class PrismInvocationService implements PrismService {

    private static final Logger LOGGER = Logger.getLogger(PrismInvocationService.class);

    private final Path javaBinary;
    private final Gson gson;

    private int counter = 0;
    private Path prismBinary;
    private Path prismFolder;

    public PrismInvocationService() {
        Path javaHome = Paths.get(System.getProperty("java.home"));
        this.javaBinary = javaHome.resolve("bin")
            .resolve("java");
        this.gson = new GsonBuilder() //
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .setPrettyPrinting()
            .create();
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
        LOGGER.info(String.format("Start prism invocation: %s", contentKind));
        PrismResult result = executeModelCheck(context);
        Duration duration = result.getDuration();
        LOGGER.info(String.format("Stop prism invocation: %s, duration: %ss", contentKind, duration.toSeconds()));
        return result;
    }

    private PrismResult executeModelCheck(PrismContext context) {
        int currentCounter = getCounter();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try (TempDirectory tempDirectory = new TempDirectory("prism-")) {
            Path modelFile = createModelFile(context, currentCounter);
            Path propertiesFile = createPropertiesFile(context, currentCounter);
            Path resultFile = tempDirectory.resolve(buildPrismFileName(currentCounter, "result"));

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
            long start = System.currentTimeMillis();
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
            long end = System.currentTimeMillis();
            Duration duration = Duration.of(end - start, ChronoUnit.MILLIS);

            String propertyName = extractPropertyName(context);
            PrismResult prismResult = readPrismResult(resultFile, propertyName, duration);
            storeResult(prismResult, context, currentCounter);
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

    private PrismResult readPrismResult(Path resultFile, String propertyToCheck, Duration duration) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(resultFile)) {
            r.readLine();
            String valueString = r.readLine();
            Double value = Double.valueOf(valueString);
            PrismResult prismResult = new PrismResult(propertyToCheck, value, duration);
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

    static class PrismResultEntry {
        public final int id;
        public final String kind;
        public final double result;
        public final long duration;
        public final String durationUnit;

        public PrismResultEntry(int id, String kind, double result, long duration, String durationUnit) {
            this.id = id;
            this.kind = kind;
            this.result = result;
            this.duration = duration;
            this.durationUnit = durationUnit;
        }
    }

    private void storeResult(PrismResult prismResult, PrismContext context, int counter) throws IOException {
        long seconds = prismResult.getDuration()
            .getSeconds();
        PrismResultEntry resultEntry = new PrismResultEntry(counter, context.getKind(), prismResult.getValue(), seconds,
                "seconds");
        Path jsonResultFile = prismFolder.resolve(buildPrismFileName(counter, "json"));
        try (Writer writer = Files.newBufferedWriter(jsonResultFile)) {
            gson.toJson(resultEntry, writer);
        }
    }

    private String buildPrismFileName(int counter, String type) {
        return String.format("prism_%04d.%s", counter, type);
    }
}
