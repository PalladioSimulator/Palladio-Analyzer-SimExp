package org.palladiosimulator.simexp.pcm.prism.replay;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismRecorderService;

public class PrismServiceRecorder extends BasePrismService implements PrismRecorderService {
    private static final Logger LOGGER = Logger.getLogger(PrismServiceRecorder.class);

    private final Map<String, Integer> counterMap = new HashMap<>();

    private Path dbPath;

    @Override
    public void initialise(Path prismFolder, String strategyId) {
        delegate.initialise(prismFolder, strategyId);
        dbPath = getReplayDBPath(prismFolder, strategyId);
        LOGGER.info(String.format("replay DB path: %s", dbPath));

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
}
