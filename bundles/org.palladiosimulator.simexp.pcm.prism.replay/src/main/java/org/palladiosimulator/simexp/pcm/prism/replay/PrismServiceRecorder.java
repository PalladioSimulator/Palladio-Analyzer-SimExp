package org.palladiosimulator.simexp.pcm.prism.replay;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;

public class PrismServiceRecorder implements PrismService {
    private static final Logger LOGGER = Logger.getLogger(PrismServiceRecorder.class);

    private static final CSVFormat CSV_FORMAT = CSVFormat.EXCEL;

    private final PrismService delegate;
    private final Map<String, Integer> counterMap = new HashMap<>();

    private Path dbPath;

    public PrismServiceRecorder(PrismService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void initialise(Path prismFolder, String strategyId) {
        delegate.initialise(prismFolder, strategyId);
        dbPath = prismFolder.resolve("replay.csv");
        LOGGER.info(String.format("replay DB path: %s", dbPath));

        /*
         * try (Reader in = Files.newBufferedReader(dbPath)) { Iterable<CSVRecord> records =
         * CSVFormat.EXCEL.parse(in); for (CSVRecord record : records) { String lastName =
         * record.get("Last Name"); String firstName = record.get("First Name"); } } catch
         * (IOException e) { LOGGER.error(e.getMessage(), e); }
         */
        CSVFormat format = CSV_FORMAT.withHeader("type", "count", "result", "module hash");
        try (BufferedWriter writer = Files.newBufferedWriter(dbPath, StandardCharsets.UTF_8)) {
            try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public PrismResult modelCheck(PrismContext context) {
        PrismResult modelCheckResult = delegate.modelCheck(context);

        Integer counter = counterMap.get(context.getKind());
        if (counter == null) {
            counter = 0;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(dbPath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            try (CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)) {
                List<String> entries = getEntries(counter, context, modelCheckResult);
                printer.printRecord(entries);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        counterMap.put(context.getKind(), counter + 1);

        return modelCheckResult;
    }

    private List<String> getEntries(int counter, PrismContext context, PrismResult result) {
        List<String> entries = new ArrayList<>();
        entries.add(context.getKind());
        entries.add(String.format("%d", counter));

        double resultValue = extractResult(context, result);
        entries.add(String.format("%f", resultValue));

        String sha256hex = DigestUtils.sha256Hex(context.getModuleFileContent());
        entries.add(sha256hex);

        return entries;
    }

    private double extractResult(PrismContext context, PrismResult result) {
        String trimmedPropertyFileContent = context.getPropertyFileContent()
            .trim();
        Optional<Double> resultValue = result.getResultOf(trimmedPropertyFileContent);
        return resultValue.get();
    }
}
