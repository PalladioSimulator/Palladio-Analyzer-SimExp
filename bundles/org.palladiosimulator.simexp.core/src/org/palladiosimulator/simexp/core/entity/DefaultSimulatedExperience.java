package org.palladiosimulator.simexp.core.entity;

import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class DefaultSimulatedExperience<S, A, R> implements SimulatedExperience {

    private final static String NONE = "-";

    private final Sample<S, A, R> sample;

    private DefaultSimulatedExperience(Sample<S, A, R> sample) {
        this.sample = sample;
    }

    public static <S, A, R> DefaultSimulatedExperience<S, A, R> of(Sample<S, A, R> sample) {
        return new DefaultSimulatedExperience<>(sample);
    }

    public static <S, A, R> String deriveIdFrom(Sample<S, A, R> sample) {
        DefaultSimulatedExperience<S, A, R> helper = DefaultSimulatedExperience.of(sample);
        return String.format("%1s_%2s_%3s", helper.getCurrent()
            .toString(), helper.getReconfiguration(),
                helper.getNext()
                    .toString());
    }

    public static String getCurrentStateFrom(SimulatedExperience simExperience) {
        String reconf = simExperience.getReconfiguration();
        String simId = simExperience.getId();
        String currentState = simId.split(reconf)[0];
        int lastIndexOf = currentState.lastIndexOf("_");
        return currentState.substring(0, lastIndexOf);
    }

    @Override
    public String getConfigurationDifferenceBefore() {
        ArchitecturalConfiguration<S> current = ((SelfAdaptiveSystemState<S>) sample.getCurrent())
            .getArchitecturalConfiguration();
        ArchitecturalConfiguration<S> next = ((SelfAdaptiveSystemState<S>) sample.getNext())
            .getArchitecturalConfiguration();
        return current.difference(next);
    }

    @Override
    public String getConfigurationDifferenceAfter() {
        ArchitecturalConfiguration<S> current = ((SelfAdaptiveSystemState<S>) sample.getCurrent())
            .getArchitecturalConfiguration();
        ArchitecturalConfiguration<S> next = ((SelfAdaptiveSystemState<S>) sample.getNext())
            .getArchitecturalConfiguration();
        return next.difference(current);
    }

    @Override
    public String getReconfiguration() {
        return sample.getAction()
            .toString();
    }

    @Override
    public String getQuantifiedStateOfCurrent() {
        return getCurrent().getQuantifiedState()
            .toString();
    }

    @Override
    public String getQuantifiedStateOfNext() {
        return getNext().getQuantifiedState()
            .toString();
    }

    @Override
    public String getEnvironmentalStateBefore() {
        return getCurrent().getPerceivedEnvironmentalState()
            .getStringRepresentation();
    }

    @Override
    public String getEnvironmentalStateAfter() {
        return getNext().getPerceivedEnvironmentalState()
            .getStringRepresentation();
    }

    @Override
    public String getEnvironmentalStateObservation() {
        PerceivableEnvironmentalState perception = getNext().getPerceivedEnvironmentalState();
        if (perception.isHidden()) {
            return perception.getStringRepresentation();
        }
        return NONE;
    }

    @Override
    public String getId() {
        return deriveIdFrom(sample);
    }

    private SelfAdaptiveSystemState<S> getCurrent() {
        return ((SelfAdaptiveSystemState<S>) sample.getCurrent());
    }

    private SelfAdaptiveSystemState<S> getNext() {
        return ((SelfAdaptiveSystemState<S>) sample.getNext());
    }

    @Override
    public String getReward() {
        return sample.getReward()
            .toString();
    }
}
