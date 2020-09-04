package org.palladiosimulator.simexp.pcm.datasource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

public abstract class DataSource {

	protected Set<PcmMeasurementSpecification> getMeasurementSpecs() {
		return InitialPcmStateCreator.getMeasurementSpecs().stream()
				.filter(PcmMeasurementSpecification.class::isInstance).map(PcmMeasurementSpecification.class::cast)
				.collect(Collectors.toSet());
	}

	public abstract MeasurementSeriesResult getSimulatedMeasurements(List<ExperimentRun> experimentRuns);
}
