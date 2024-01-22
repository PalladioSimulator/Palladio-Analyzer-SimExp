package org.palladiosimulator.simexp.core.state;

import java.util.Optional;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.core.entity.SimulatedExperience;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;

public class RestoredSelfAdaptiveSystemState<S, A> extends SelfAdaptiveSystemState<S, A> {

    // TODO this is not a good solution: The class should be refactored in further iterations.
    private static class SpecialCaseStateQuantity extends StateQuantity {

        private final String quantifiedState;

        public SpecialCaseStateQuantity(String quantifiedState) {
            super(null);
            this.quantifiedState = quantifiedState;
        }

        @Override
        public Optional<SimulatedMeasurement> findMeasurementWith(SimulatedMeasurementSpecification spec) {
            Stream<String> quantities = Stream.of(quantifiedState.split(withDelimiter()));
            return quantities.filter(quantity -> quantity.contains(spec.getId()))
                .map(quantity -> restoreSimulatedMeasurement(quantity, spec))
                .findFirst();
        }

        private String withDelimiter() {
            return "\\" + DELIMITER;
        }

        private SimulatedMeasurement restoreSimulatedMeasurement(String quantity,
                SimulatedMeasurementSpecification spec) {
            SimulatedMeasurement measurement = SimulatedMeasurement.with(spec);
            measurement.setValue(restoreValue(quantity));
            return measurement;
        }

        private Double restoreValue(String quantity) {
            String value = quantity.split(" ")[1];
            value = value.replace(",", "");
            return Double.parseDouble(value);
        }

        @Override
        public String toString() {
            return quantifiedState;
        }

    }

    private final SpecialCaseStateQuantity quantifiedState;
    private final SelfAdaptiveSystemState<S, A> restoredState;

    private RestoredSelfAdaptiveSystemState(SimulationRunnerHolder<S, A> simulationRunnerHolder,
            SelfAdaptiveSystemState<S, A> restoredState, SimulatedExperience experience) {
        super(simulationRunnerHolder);
        this.restoredState = restoredState;
        this.quantifiedState = new SpecialCaseStateQuantity(experience.getQuantifiedStateOfCurrent());
    }

    public static <S, A> RestoredSelfAdaptiveSystemState<S, A> restoreFrom(
            SimulationRunnerHolder<S, A> simulationRunnerHolder, SimulatedExperience experience,
            SelfAdaptiveSystemState<S, A> restoredState) {
        return new RestoredSelfAdaptiveSystemState<>(simulationRunnerHolder, restoredState, experience);
    }

    @Override
    public StateQuantity getQuantifiedState() {
        return quantifiedState;
    }

    @Override
    public ArchitecturalConfiguration<S, A> getArchitecturalConfiguration() {
        return restoredState.getArchitecturalConfiguration();
    }

    @Override
    public PerceivableEnvironmentalState getPerceivedEnvironmentalState() {
        return restoredState.getPerceivedEnvironmentalState();
    }

    @Override
    public SelfAdaptiveSystemState<S, A> transitToNext(PerceivableEnvironmentalState perceivedState,
            ArchitecturalConfiguration<S, A> archConf) {
        return restoredState.transitToNext(perceivedState, archConf);
    }

    @Override
    public String toString() {
        return restoredState.toString();
    }

}
