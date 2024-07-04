package org.palladiosimulator.simexp.pcm.prism.service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;

public interface PrismService {

    public static class PrismResult {

        private final HashMap<String, Double> prismResults = new HashMap<>();

        public void addResult(String property, Double result) {
            prismResults.put(property, result);
        }

        public Optional<Double> getResultOf(String property) {
            return Optional.ofNullable(prismResults.get(property));
        }

        public void mergeWith(PrismResult resultToMerge) {
            if (resultToMerge == null) {
                return;
            }

            prismResults.putAll(resultToMerge.prismResults);
        }

    }

    public void initialise(Path logFilePath);

    public PrismResult modelCheck(PrismContext context);

}