package org.palladiosimulator.simexp.pcm.builder;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.builder.ExperienceSimulationBuilder;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator.InitialSelfAdaptiveSystemStateCreator;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.solver.core.models.PCMInstance;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PcmExperienceSimulationBuilder<A, Aa extends Reconfiguration<A>, R, V>
        extends ExperienceSimulationBuilder<PCMInstance, A, Aa, R, V> {

    private final IExperimentProvider experimentProvider;
    private final SimulationRunnerHolder simulationRunnerHolder;
    private List<ExperienceSimulationRunner> simRunner = Lists.newArrayList();
    private Set<SimulatedMeasurementSpecification> specs = Sets.newLinkedHashSet();
    private Experiment initial = null;

    public class GlobalPcmSettingsBuilder {

        public GlobalPcmSettingsBuilder withInitialExperiment(Experiment initial) {
            PcmExperienceSimulationBuilder.this.initial = initial;
            return this;
        }

        public GlobalPcmSettingsBuilder andSimulatedMeasurementSpecs(Set<SimulatedMeasurementSpecification> specs) {
            PcmExperienceSimulationBuilder.this.specs = specs;
            return this;
        }

        public GlobalPcmSettingsBuilder addExperienceSimulationRunner(ExperienceSimulationRunner runner) {
            PcmExperienceSimulationBuilder.this.simRunner.add(runner);
            return this;
        }

        public GlobalPcmSettingsBuilder addExperienceSimulationRunners(Set<ExperienceSimulationRunner> runners) {
            PcmExperienceSimulationBuilder.this.simRunner.addAll(runners);
            return this;
        }

        public PcmExperienceSimulationBuilder<A, Aa, R, V> done() {
            return PcmExperienceSimulationBuilder.this;
        }

    }

    public PcmExperienceSimulationBuilder(IExperimentProvider experimentProvider,
            SimulationRunnerHolder simulationRunnerHolder) {
        this.experimentProvider = experimentProvider;
        this.simulationRunnerHolder = simulationRunnerHolder;
    }

    @Override
    public ExperienceSimulator<PCMInstance, A, R> build() {
        // TODO Exception handling
        Objects.requireNonNull(initial, "");
        if (Boolean.logicalOr(specs.isEmpty(), simRunner.isEmpty())) {
            throw new RuntimeException("");
        }
        return super.build();
    }

    public static <A, Aa extends Reconfiguration<A>, R, V> PcmExperienceSimulationBuilder<A, Aa, R, V> newBuilder(
            IExperimentProvider experimentProvider, SimulationRunnerHolder simulationRunnerHolder) {
        return new PcmExperienceSimulationBuilder<>(experimentProvider, simulationRunnerHolder);
    }

    public GlobalPcmSettingsBuilder makeGlobalPcmSettings() {
        return new GlobalPcmSettingsBuilder();
    }

    @Override
    protected List<ExperienceSimulationRunner> getSimulationRunner() {
        return simRunner;
    }

    @Override
    protected InitialSelfAdaptiveSystemStateCreator<PCMInstance, A, V> createInitialSassCreator() {
        return new InitialPcmStateCreator<>(specs, experimentProvider, simulationRunnerHolder);
    }

}
