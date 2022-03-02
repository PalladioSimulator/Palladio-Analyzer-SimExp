package org.palladiosimulator.simexp.pcm.datasource;

import java.util.List;

import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;

public abstract class DataSource {

	public abstract MeasurementSeriesResult getSimulatedMeasurements(List<ExperimentRun> experimentRuns);
}
