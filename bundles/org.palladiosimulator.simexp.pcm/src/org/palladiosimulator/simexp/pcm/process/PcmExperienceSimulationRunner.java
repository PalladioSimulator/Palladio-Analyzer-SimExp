package org.palladiosimulator.simexp.pcm.process;

import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.datasource.DataSource;
import org.palladiosimulator.simexp.pcm.datasource.EDP2DataSource;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;

public class PcmExperienceSimulationRunner implements ExperienceSimulationRunner {

	private final DataSource dataSource;

	public PcmExperienceSimulationRunner() {
		this.dataSource = new EDP2DataSource();
	}

	@Override
	public void initSimulationRun() {
		ExperimentProvider.get().initializeExperimentRunner();
		QVToReconfigurationManager.get().resetReconfigurator();
	}

	@Override
	public void simulate(SelfAdaptiveSystemState<?> sasState) {
		runSimulation();
		retrieveStateQuantities(asPcmState(sasState));
	}

	private PcmSelfAdaptiveSystemState asPcmState(SelfAdaptiveSystemState<?> sasState) {
		if (sasState instanceof PcmSelfAdaptiveSystemState) {
			return (PcmSelfAdaptiveSystemState) sasState;
		}

		// TODO exception handling
		throw new RuntimeException("");
	}

	private void runSimulation() {
		ExperimentProvider.get().getExperimentRunner().runExperiment();
	}

	private void retrieveStateQuantities(PcmSelfAdaptiveSystemState sasState) {
		ExperimentRunner expRunner = ExperimentProvider.get().getExperimentRunner();
		MeasurementSeriesResult result = dataSource.getSimulatedMeasurements(expRunner.getCurrentExperimentRuns());
		for (PcmMeasurementSpecification each : getMeasurementSpecs(sasState.getQuantifiedState())) {
			result.getMeasurementsSeries(each).ifPresent(series -> {
				sasState.getQuantifiedState().setMeasurement(each.computeQuantity(series), each);
			});
		}
	}

	private Set<PcmMeasurementSpecification> getMeasurementSpecs(StateQuantity stateQuantity) {
		return stateQuantity.getMeasurementSpecs().stream().filter(PcmMeasurementSpecification.class::isInstance)
				.map(PcmMeasurementSpecification.class::cast).collect(Collectors.toSet());
	}

}
