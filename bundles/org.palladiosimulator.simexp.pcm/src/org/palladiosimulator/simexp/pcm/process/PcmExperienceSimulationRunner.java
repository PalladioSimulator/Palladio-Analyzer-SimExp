package org.palladiosimulator.simexp.pcm.process;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.simexp.core.process.AbstractExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.pcm.datasource.DataSource;
import org.palladiosimulator.simexp.pcm.datasource.EDP2DataSource;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult.MeasurementSeries;
import org.palladiosimulator.simexp.pcm.state.InitialPcmStateCreator;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.solver.models.PCMInstance;

public class PcmExperienceSimulationRunner<A> extends AbstractExperienceSimulationRunner<PCMInstance, A> {

    private final DataSource dataSource;
    private final IExperimentProvider experimentProvider;

    public PcmExperienceSimulationRunner(IExperimentProvider experimentProvider,
            InitialPcmStateCreator<A> initialStateCreator) {
        this(new EDP2DataSource<>(initialStateCreator), experimentProvider);
    }

    PcmExperienceSimulationRunner(DataSource dataSource, IExperimentProvider experimentProvider) {
        this.dataSource = dataSource;
        this.experimentProvider = experimentProvider;
    }

    @Override
    protected void doSimulate(SelfAdaptiveSystemState<PCMInstance, A> sasState) {
        runSimulation();
        retrieveStateQuantities(asPcmState(sasState));
    }

    private PcmSelfAdaptiveSystemState<A> asPcmState(SelfAdaptiveSystemState<PCMInstance, A> sasState) {
        if (sasState instanceof PcmSelfAdaptiveSystemState) {
            return (PcmSelfAdaptiveSystemState<A>) sasState;
        }

        // TODO exception handling
        throw new RuntimeException("");
    }

    private void runSimulation() {
        experimentProvider.getExperimentRunner()
            .runExperiment();
    }

    private void retrieveStateQuantities(PcmSelfAdaptiveSystemState<A> sasState) {
        ExperimentRunner expRunner = experimentProvider.getExperimentRunner();
        List<ExperimentRun> currentExperimentRuns = expRunner.getCurrentExperimentRuns();
        MeasurementSeriesResult result = dataSource.getSimulatedMeasurements(currentExperimentRuns);
        StateQuantity quantifiedState = sasState.getQuantifiedState();
        for (PcmMeasurementSpecification each : getMeasurementSpecs(quantifiedState)) {
            Optional<MeasurementSeries> measurementsSeries = result.getMeasurementsSeries(each);
            measurementsSeries.ifPresent(series -> {
                double computeQuantity = each.computeQuantity(series);
                quantifiedState.setMeasurement(computeQuantity, each);
            });
        }
    }

    private Set<PcmMeasurementSpecification> getMeasurementSpecs(StateQuantity stateQuantity) {
        return stateQuantity.getMeasurementSpecs()
            .stream()
            .filter(PcmMeasurementSpecification.class::isInstance)
            .map(PcmMeasurementSpecification.class::cast)
            .collect(Collectors.toSet());
    }

}
