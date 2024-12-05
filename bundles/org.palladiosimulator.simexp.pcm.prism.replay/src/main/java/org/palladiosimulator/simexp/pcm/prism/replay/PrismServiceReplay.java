package org.palladiosimulator.simexp.pcm.prism.replay;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;

public class PrismServiceReplay extends BasePrismService {
    private static final Logger LOGGER = Logger.getLogger(PrismServiceReplay.class);

    private static class Entry {
        private final double value;
        private final String hash;

        Entry(double value, String hash) {
            this.value = value;
            this.hash = hash;
        }
    }

    private final Map<Pair<String, Integer>, Entry> entryMap = new HashMap<>();
    private final Map<String, Integer> counterMap = new HashMap<>();
    private final PrismService delegate;

    private Path dbPath;

    public PrismServiceReplay(PrismService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void initialise(Path prismFolder, String strategyId) {
        // delegate.initialise(prismFolder, strategyId);
        dbPath = getReplayDBPath(prismFolder, strategyId);
        LOGGER.info(String.format("replay DB path: %s", dbPath));
        entryMap.clear();
        try (Reader in = Files.newBufferedReader(dbPath, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSV_FORMAT.withFirstRecordAsHeader()
                .parse(in);
            for (CSVRecord record : records) {
                String type = record.get("type");
                int count = Integer.parseInt(record.get("count"));
                double result = Double.parseDouble(record.get("result"));
                String moduleHash = record.get("module hash");

                Pair<String, Integer> key = new ImmutablePair<>(type, count);
                entryMap.put(key, new Entry(result, moduleHash));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public PrismResult modelCheck(PrismContext context) {
        // PrismResult modelCheckResult = delegate.modelCheck(context);

        Integer counter = counterMap.get(context.getKind());
        if (counter == null) {
            counter = 0;
        }

        // List<String> currentEntries = getEntries(counter, context, modelCheckResult);
        Pair<String, Integer> key = new ImmutablePair<>(context.getKind(), counter);
        Entry entry = entryMap.get(key);
        if (entry == null) {
            throw new RuntimeException("cannot find entry: " + key);
        }
        /*
         * if (!currentEntries.get(0) .equals(key.getKey())) { throw new
         * RuntimeException("invalid kind: " + key); } if (!currentEntries.get(1)
         * .equals(key.getValue() .toString())) { throw new RuntimeException("invalid count: " +
         * key); } if (!currentEntries.get(3) .equals(entry.hash)) { throw new
         * RuntimeException("invalid hash: " + key); } if (!currentEntries.get(2)
         * .equals(String.format("%f", entry.value))) { throw new RuntimeException("invalid hash: "
         * + key); }
         */

        counterMap.put(context.getKind(), counter + 1);

        PrismResult cachedCheckResult = new PrismResult();
        cachedCheckResult.addResult(extractPrismKey(context), entry.value);

        return cachedCheckResult;
    }
}
