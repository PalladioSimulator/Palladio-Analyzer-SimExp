package org.palladiosimulator.simexp.pcm.state;

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

    private final Set<SimulatedMeasurementSpecification> pcmMeasurementSpecs;
    private final IExperimentProvider experimentProvider;
    private final IQVToReconfigurationManager qvtoReconfigurationManager;

    public InitialPcmStateCreator(Set<SimulatedMeasurementSpecification> specs, IExperimentProvider experimentProvider,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        this.pcmMeasurementSpecs = specs;
        this.experimentProvider = experimentProvider;
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
    }

    public static <A> InitialPcmStateCreator<A> with(Set<SimulatedMeasurementSpecification> specs,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
        return new InitialPcmStateCreator<>(specs, experimentProvider, qvtoReconfigurationManager);
    }

    public Set<SimulatedMeasurementSpecification> getMeasurementSpecs() {
        return pcmMeasurementSpecs;
    }

    @Override
    public SelfAdaptiveSystemState<PCMInstance, A> create(ArchitecturalConfiguration<PCMInstance, A> initialArch,
            PerceivableEnvironmentalState initialEnv) {
        return PcmSelfAdaptiveSystemState.<A> newBuilder(this)
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
