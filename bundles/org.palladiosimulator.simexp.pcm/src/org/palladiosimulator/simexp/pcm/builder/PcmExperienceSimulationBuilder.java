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
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator.InitialSelfAdaptiveSystemStateCreator;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PcmExperienceSimulationBulder<S, A, Aa extends Reconfiguration<A>, R>
        extends ExperienceSimulationBuilder<S, A, Aa, R> {

    private List<ExperienceSimulationRunner<S, A>> simRunner = Lists.newArrayList();
    private Set<SimulatedMeasurementSpecification> specs = Sets.newHashSet();
    private Experiment initial = null;
    private final IExperimentProvider experimentProvider;
    private final IQVToReconfigurationManager qvtoReconfigurationManager;

    public class GlobalPcmSettingsBuilder {

        public GlobalPcmSettingsBuilder withInitialExperiment(Experiment initial) {
            PcmExperienceSimulationBuilder.this.initial = initial;
            return this;
        }

        public GlobalPcmSettingsBuilder andSimulatedMeasurementSpecs(Set<SimulatedMeasurementSpecification> specs) {
            PcmExperienceSimulationBuilder.this.specs = specs;
            return this;
        }

        public GlobalPcmSettingsBuilder addExperienceSimulationRunner(ExperienceSimulationRunner<S, A> runner) {
            PcmExperienceSimulationBuilder.this.simRunner.add(runner);
            return this;
        }

        public GlobalPcmSettingsBuilder addExperienceSimulationRunners(Set<ExperienceSimulationRunner<S, A>> runners) {
            PcmExperienceSimulationBuilder.this.simRunner.addAll(runners);
            return this;
        }

        public PcmExperienceSimulationBuilder<S, A, Aa, R> done() {
            return PcmExperienceSimulationBuilder.this;
        }

    }

    public PcmExperienceSimulationBuilder(IExperimentProvider experimentProvider,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        this.experimentProvider = experimentProvider;
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
    }

    @Override
    public ExperienceSimulator<S, A, R> build() {
        // TODO Exception handling
        Objects.requireNonNull(initial, "");
        if (Boolean.logicalOr(specs.isEmpty(), simRunner.isEmpty())) {
            throw new RuntimeException("");
        }
        return super.build();
    }

    public static PcmExperienceSimulationBuilder newBuilder(IExperimentProvider experimentProvider,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        return new PcmExperienceSimulationBuilder(experimentProvider, qvtoReconfigurationManager);
    }

    public GlobalPcmSettingsBuilder makeGlobalPcmSettings() {
        return new GlobalPcmSettingsBuilder();
    }

    @Override
    protected List<ExperienceSimulationRunner<S, A>> getSimulationRunner() {
        return simRunner;
    }

    @Override
    protected InitialSelfAdaptiveSystemStateCreator<S, A> createInitialSassCreator() {
        return new InitialPcmStateCreator(specs, experimentProvider, qvtoReconfigurationManager);
    }

}
