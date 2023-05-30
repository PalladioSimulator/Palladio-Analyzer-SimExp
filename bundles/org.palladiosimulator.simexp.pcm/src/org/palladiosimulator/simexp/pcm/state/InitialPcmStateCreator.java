package org.palladiosimulator.simexp.pcm.state;

import java.util.Objects;
import java.util.Set;

import org.palladiosimulator.simexp.core.entity.SimulatedMeasurementSpecification;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class InitialPcmStateCreator implements SelfAdaptiveSystemStateSpaceNavigator.InitialSelfAdaptiveSystemStateCreator {

	private static Set<SimulatedMeasurementSpecification> pcmMeasurementSpecs = null;
	private final IExperimentProvider experimentProvider;
	private final IQVToReconfigurationManager qvtoReconfigurationManager;
	
	public InitialPcmStateCreator(Set<SimulatedMeasurementSpecification> specs, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) { 
		pcmMeasurementSpecs = specs;
		this.experimentProvider = experimentProvider;
		this.qvtoReconfigurationManager = qvtoReconfigurationManager;
	}
	
	public static InitialPcmStateCreator with(Set<SimulatedMeasurementSpecification> specs, IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
		return new InitialPcmStateCreator(specs, experimentProvider, qvtoReconfigurationManager);
	}

	public static Set<SimulatedMeasurementSpecification> getMeasurementSpecs() {
		//TODO exception handling
		return Objects.requireNonNull(pcmMeasurementSpecs, "");
	}

	@Override
	public SelfAdaptiveSystemState<?> create(ArchitecturalConfiguration<?> initialArch,
											 PerceivableEnvironmentalState initialEnv) {
		return PcmSelfAdaptiveSystemState.newBuilder()
				.withStructuralState((PcmArchitecturalConfiguration) initialArch, initialEnv)
				.andMetricDescriptions(getMeasurementSpecs())
				.asInitial()
				.build();
	}

	@Override
	public ArchitecturalConfiguration<?> getInitialArchitecturalConfiguration() {
		return PcmArchitecturalConfiguration.of(experimentProvider.getExperimentRunner().makeSnapshotOfPCM(), experimentProvider, qvtoReconfigurationManager);
	}
	
}
