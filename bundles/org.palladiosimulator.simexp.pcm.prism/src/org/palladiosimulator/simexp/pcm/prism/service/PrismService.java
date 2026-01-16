package org.palladiosimulator.simexp.pcm.prism.service;

import java.nio.file.Path;
import java.time.Duration;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;

public interface PrismService {

    public static class PrismResult {

        private final String property;
        private final double value;
        private final Duration duration;

        public PrismResult(String property, Double result, Duration duration) {
            this.property = property;
            this.value = result;
            this.duration = duration;
        }

        public String getProperty() {
            return property;
        }

        public double getValue() {
            return value;
        }

        public Duration getDuration() {
            return duration;
        }
    }

    public void initialise(Path logFilePath, String strategyId);

    public PrismResult modelCheck(PrismContext context);

}