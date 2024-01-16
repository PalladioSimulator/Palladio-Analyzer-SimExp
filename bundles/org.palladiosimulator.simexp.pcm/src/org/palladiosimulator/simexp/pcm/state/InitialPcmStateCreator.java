package org.palladiosimulator.simexp.pcm.state;

import java.util.Objects;
import java.util.Set;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.solver.models.PCMInstance;

public class InitialPcmStateCreator<A>
        implements SelfAdaptiveSystemStateSpaceNavigator.InitialSelfAdaptiveSystemStateCreator<PCMInstance, A> {

    // TODO: singleton
    private static Set<SimulatedMeasurementSpecification> pcmMeasurementSpecs = null;
    private final IExperimentProvider experimentProvider;
    private final IQVToReconfigurationManager qvtoReconfigurationManager;

    public InitialPcmStateCreator(Set<SimulatedMeasurementSpecification> specs, IExperimentProvider experimentProvider,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        pcmMeasurementSpecs = specs;
        this.experimentProvider = experimentProvider;
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
    }

    public static <A> InitialPcmStateCreator<A> with(Set<SimulatedMeasurementSpecification> specs,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
        return new InitialPcmStateCreator<>(specs, experimentProvider, qvtoReconfigurationManager);
    }

    public static Set<SimulatedMeasurementSpecification> getMeasurementSpecs() {
        // TODO exception handling
        return Objects.requireNonNull(pcmMeasurementSpecs, "");
    }

    @Override
    public SelfAdaptiveSystemState<PCMInstance, A> create(ArchitecturalConfiguration<PCMInstance, A> initialArch,
            PerceivableEnvironmentalState initialEnv) {
        return PcmSelfAdaptiveSystemState.<A> newBuilder()
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
