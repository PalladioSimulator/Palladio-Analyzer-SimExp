package org.palladiosimulator.simexp.workflow.config;

import org.palladiosimulator.simexp.pcm.config.IRandomEvaluationConfiguration;

public class RandomEvaluationConfiguration implements IRandomEvaluationConfiguration {
    private final double errorDefault;
    private final int memoryUsage;
    private final int randomSampleCount;

    public RandomEvaluationConfiguration(double errorDefault, int memoryUsage, int randomSampleCount) {
        this.errorDefault = errorDefault;
        this.memoryUsage = memoryUsage;
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
    public int getRandomSampleCount() {
        return randomSampleCount;
    }

}
