package org.palladiosimulator.simexp.workflow.config;

import java.util.Optional;

import org.palladiosimulator.simexp.commons.constants.model.RandomSampleType;
import org.palladiosimulator.simexp.pcm.config.IRandomEvaluationConfiguration;

public class RandomEvaluationConfiguration implements IRandomEvaluationConfiguration {
    private final double errorDefault;
    private final int memoryUsage;
    private final RandomSampleType randomSampleType;
    private final Optional<Integer> randomSampleCount;

    public RandomEvaluationConfiguration(double errorDefault, int memoryUsage, RandomSampleType randomSampleType,
            Optional<Integer> randomSampleCount) {
        this.errorDefault = errorDefault;
        this.memoryUsage = memoryUsage;
        this.randomSampleType = randomSampleType;
        this.randomSampleCount = randomSampleCount;
    }

    @Override
    public double getErrorReward() {
        return errorDefault;
    }

    @Override
    public int getMemoryUsage() {
        return memoryUsage;
    }

    @Override
    public RandomSampleType getRandomSampleType() {
        return randomSampleType;
    }

    @Override
    public Optional<Integer> getRandomSampleCount() {
        return randomSampleCount;
    }
}
