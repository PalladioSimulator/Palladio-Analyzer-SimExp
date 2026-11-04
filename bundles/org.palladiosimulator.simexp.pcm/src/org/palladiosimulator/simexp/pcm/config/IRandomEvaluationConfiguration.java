package org.palladiosimulator.simexp.pcm.config;

import java.util.Optional;

import org.palladiosimulator.simexp.commons.constants.model.RandomSampleType;

public interface IRandomEvaluationConfiguration extends IKubernetesConfiguration {
    RandomSampleType getRandomSampleType();

    Optional<Integer> getRandomSampleCount();
}
