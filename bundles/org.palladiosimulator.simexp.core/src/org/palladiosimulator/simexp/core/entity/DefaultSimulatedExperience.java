package org.palladiosimulator.simexp.core.entity;

import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.samplemodel.Sample;

public class DefaultSimulatedExperience<C, A, R, V> implements SimulatedExperience {

    private final static String NONE = "-";

    private final Sample<A, R> sample;

    private DefaultSimulatedExperience(Sample<A, R> sample) {
        this.sample = sample;
    }

    public static <C, A, R, V> DefaultSimulatedExperience<C, A, R, V> of(Sample<A, R> sample) {
        return new DefaultSimulatedExperience<>(sample);
    }

    public static <S, A, R, V> String deriveIdFrom(Sample<A, R> sample) {
        DefaultSimulatedExperience<S, A, R, V> helper = DefaultSimulatedExperience.of(sample);
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
        ArchitecturalConfiguration<C, A> current = ((SelfAdaptiveSystemState<C, A, V>) sample.getCurrent())
            .getArchitecturalConfiguration();
        ArchitecturalConfiguration<C, A> next = ((SelfAdaptiveSystemState<C, A, V>) sample.getNext())
            .getArchitecturalConfiguration();
        return current.difference(next);
    }

    @Override
    public String getConfigurationDifferenceAfter() {
        ArchitecturalConfiguration<C, A> current = ((SelfAdaptiveSystemState<C, A, V>) sample.getCurrent())
            .getArchitecturalConfiguration();
        ArchitecturalConfiguration<C, A> next = ((SelfAdaptiveSystemState<C, A, V>) sample.getNext())
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
        PerceivableEnvironmentalState<V> perception = getNext().getPerceivedEnvironmentalState();
        if (perception.isHidden()) {
            return perception.getStringRepresentation();
        }
        return NONE;
    }

    @Override
    public String getId() {
        return deriveIdFrom(sample);
    }

    private SelfAdaptiveSystemState<C, A, V> getCurrent() {
        return ((SelfAdaptiveSystemState<C, A, V>) sample.getCurrent());
    }

    private SelfAdaptiveSystemState<C, A, V> getNext() {
        return ((SelfAdaptiveSystemState<C, A, V>) sample.getNext());
    }

    @Override
    public String getReward() {
        return sample.getReward()
            .toString();
    }
}
