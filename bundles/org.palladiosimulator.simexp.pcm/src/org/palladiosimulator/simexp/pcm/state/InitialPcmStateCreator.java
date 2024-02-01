package org.palladiosimulator.simexp.pcm.state;

import java.util.Set;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.solver.models.PCMInstance;

public class InitialPcmStateCreator<A, V>
        implements SelfAdaptiveSystemStateSpaceNavigator.InitialSelfAdaptiveSystemStateCreator<PCMInstance, A, V> {

    private final Set<SimulatedMeasurementSpecification> pcmMeasurementSpecs;
    private final IExperimentProvider experimentProvider;
    private final IQVToReconfigurationManager qvtoReconfigurationManager;
    private final SimulationRunnerHolder<PCMInstance> simulationRunnerHolder;

    public InitialPcmStateCreator(Set<SimulatedMeasurementSpecification> specs, IExperimentProvider experimentProvider,
            IQVToReconfigurationManager qvtoReconfigurationManager,
            SimulationRunnerHolder<PCMInstance> simulationRunnerHolder) {
        this.pcmMeasurementSpecs = specs;
        this.experimentProvider = experimentProvider;
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
        this.simulationRunnerHolder = simulationRunnerHolder;
    }

    public static <A, V> InitialPcmStateCreator<A, V> with(Set<SimulatedMeasurementSpecification> specs,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager,
            SimulationRunnerHolder<PCMInstance> simulationRunnerHolder) {
        return new InitialPcmStateCreator<>(specs, experimentProvider, qvtoReconfigurationManager,
                simulationRunnerHolder);
    }

    public Set<SimulatedMeasurementSpecification> getMeasurementSpecs() {
        return pcmMeasurementSpecs;
    }

    @Override
    public SelfAdaptiveSystemState<PCMInstance, A, V> create(ArchitecturalConfiguration<PCMInstance, A> initialArch,
            PerceivableEnvironmentalState<V> initialEnv) {
        return PcmSelfAdaptiveSystemState.<A, V> newBuilder(this, simulationRunnerHolder)
            .withStructuralState((PcmArchitecturalConfiguration<A>) initialArch, initialEnv)
            .andMetricDescriptions(getMeasurementSpecs())
            .asInitial()
            .build();
    }

    @Override
    public ArchitecturalConfiguration<PCMInstance, A> getInitialArchitecturalConfiguration() {
        ExperimentRunner experimentRunner = experimentProvider.getExperimentRunner();
        PCMInstance snapshotOfPCM = experimentRunner.makeSnapshotOfPCM();
        return PcmArchitecturalConfiguration.<A> of(snapshotOfPCM, experimentProvider, qvtoReconfigurationManager);
    }

}
