package org.palladiosimulator.simexp.pcm.prism.service;

import java.nio.file.Path;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;

public interface PrismService {

    public static class PrismResult {

        private final String property;
        private final double value;

        public PrismResult(String property, Double result) {
            this.property = property;
            this.value = result;
        }

        public String getProperty() {
            return property;
        }

        public double getValue() {
            return value;
        }
    }

    public void initialise(Path logFilePath, String strategyId);

    public PrismResult modelCheck(PrismContext context);

}