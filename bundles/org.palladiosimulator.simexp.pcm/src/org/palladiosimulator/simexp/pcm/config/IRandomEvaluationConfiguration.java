package org.palladiosimulator.simexp.pcm.config;

import org.palladiosimulator.simexp.commons.constants.model.RandomSampleType;

public interface IRandomEvaluationConfiguration extends IKubernetesConfiguration {
    RandomSampleType getRandomSampleType();

    int getRandomSampleCount();
}
