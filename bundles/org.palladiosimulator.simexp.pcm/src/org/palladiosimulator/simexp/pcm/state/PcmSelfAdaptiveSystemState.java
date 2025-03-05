package org.palladiosimulator.simexp.pcm.state;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.solver.core.models.PCMInstance;

public class PcmSelfAdaptiveSystemState<A, V> extends SelfAdaptiveSystemState<PCMInstance, A, V> {

    public static class PcmSassBuilder<A, V> {

        private final InitialPcmStateCreator<A, V> initialPcmStateCreator;
        private final SimulationRunnerHolder simulationRunnerHolder;

        private Set<SimulatedMeasurementSpecification> specs = new LinkedHashSet<>();
        private PcmArchitecturalConfiguration<A> initialArch = null;
        private PerceivableEnvironmentalState<V> initialEnv = null;
        private boolean isInitial = false;

        public PcmSassBuilder(InitialPcmStateCreator<A, V> initialPcmStateCreator,
                SimulationRunnerHolder simulationRunnerHolder) {
            this.initialPcmStateCreator = initialPcmStateCreator;
            this.simulationRunnerHolder = simulationRunnerHolder;
        }

        public PcmSassBuilder<A, V> withStructuralState(PcmArchitecturalConfiguration<A> initialArch,
                PerceivableEnvironmentalState<V> initialEnv) {
            this.initialArch = initialArch;
            this.initialEnv = initialEnv;
            return this;
        }

        public PcmSassBuilder<A, V> andMetricDescriptions(Set<SimulatedMeasurementSpecification> specs) {
            this.specs.addAll(specs);
            return this;
        }

        public PcmSassBuilder<A, V> asInitial() {
            isInitial = true;
            return this;
        }

        public PcmSelfAdaptiveSystemState<A, V> build() {
            // TODO Exception handling
            Objects.requireNonNull(initialArch, "");
            Objects.requireNonNull(initialEnv, "");
            if (specs.isEmpty()) {
                throw new RuntimeException("");
            }

            return new PcmSelfAdaptiveSystemState<>(simulationRunnerHolder, initialArch, initialEnv, specs, isInitial,
                    initialPcmStateCreator);
        }

    }

    private final InitialPcmStateCreator<A, V> initialPcmStateCreator;

    private PcmSelfAdaptiveSystemState(SimulationRunnerHolder simulationRunnerHolder,
            PcmArchitecturalConfiguration<A> archConf, PerceivableEnvironmentalState<V> perceivedState,
            Set<SimulatedMeasurementSpecification> specs, boolean isInitial,
            InitialPcmStateCreator<A, V> initialPcmStateCreator) {
        super(simulationRunnerHolder);
        this.quantifiedState = StateQuantity.of(toMeasuredQuantities(specs));
        this.archConfiguration = archConf;
        this.perceivedState = perceivedState;
        this.initialPcmStateCreator = initialPcmStateCreator;

        applyChanges(perceivedState);
        if (isInitial) {
            determineQuantifiedState();
        }
    }

    public static <A, V> PcmSassBuilder<A, V> newBuilder(InitialPcmStateCreator<A, V> initialPcmStateCreator,
            SimulationRunnerHolder simulationRunnerHolder) {
        return new PcmSassBuilder<>(initialPcmStateCreator, simulationRunnerHolder);
    }

    private List<SimulatedMeasurement> toMeasuredQuantities(Set<SimulatedMeasurementSpecification> specs) {
        return specs.stream()
            .map(each -> SimulatedMeasurement.with(each))
            .collect(Collectors.toList());
    }

    public boolean isSteadyState() {
        for (SimulatedMeasurement each : quantifiedState.getMeasurements()) {
            Optional<Threshold> evaluator = ((PcmMeasurementSpecification) each.getSpecification())
                .getSteadyStateEvaluator();
            if (evaluator.isPresent()) {
                if (evaluator.get()
                    .isNotSatisfied(each.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public SelfAdaptiveSystemState<PCMInstance, A, V> transitToNext(PerceivableEnvironmentalState<V> perceivedState,
            ArchitecturalConfiguration<PCMInstance, A> archConf) {
        PcmSassBuilder<A, V> builder = newBuilder(initialPcmStateCreator, simulationRunnerHolder);
        return builder.withStructuralState((PcmArchitecturalConfiguration<A>) archConf, perceivedState)
            .andMetricDescriptions(initialPcmStateCreator.getMeasurementSpecs())
            .build();
    }

    private void applyChanges(PerceivableEnvironmentalState<V> perceivedState) {
        if (perceivedState instanceof PcmModelChange) {
            ((PcmModelChange<V>) perceivedState).apply(perceivedState.getValue());
        }
        // TODO logging or exception handling
    }

}
