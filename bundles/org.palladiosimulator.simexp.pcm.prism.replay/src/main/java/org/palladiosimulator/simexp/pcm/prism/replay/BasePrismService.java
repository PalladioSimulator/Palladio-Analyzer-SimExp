package org.palladiosimulator.simexp.pcm.prism.replay;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.csv.CSVFormat;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;

abstract class BasePrismService implements PrismService {
    protected static final CSVFormat CSV_FORMAT = CSVFormat.EXCEL;

    protected List<String> getEntries(int counter, PrismContext context, PrismResult result) {
        List<String> entries = new ArrayList<>();
        entries.add(context.getKind());
        entries.add(String.format("%d", counter));

        double resultValue = extractResult(context, result);
        entries.add(String.format("%.25f", resultValue));

        String sha256hex = DigestUtils.sha256Hex(context.getModuleFileContent());
        entries.add(sha256hex);

        return entries;
    }

    private double extractResult(PrismContext context, PrismResult result) {
        String trimmedPropertyFileContent = extractPrismKey(context);
        Optional<Double> resultValue = result.getResultOf(trimmedPropertyFileContent);
        return resultValue.get();
    }

    protected String extractPrismKey(PrismContext context) {
        String trimmedPropertyFileContent = context.getPropertyFileContent()
            .trim();
        return trimmedPropertyFileContent;
    }

    protected Path getReplayDBPath(Path prismFolder, String strategyId) {
        return prismFolder.getParent()
            .getParent()
            .resolve(String.format("replay_%s.csv", strategyId));
    }
}
