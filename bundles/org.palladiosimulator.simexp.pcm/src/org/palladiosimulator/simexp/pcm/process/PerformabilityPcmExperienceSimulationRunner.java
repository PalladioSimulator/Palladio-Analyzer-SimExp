package org.palladiosimulator.simexp.pcm.process;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenarioRepository;
import org.palladiosimulator.failuremodel.failuretype.FailureTypeRepository;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.StateQuantity;
import org.palladiosimulator.simexp.pcm.datasource.DataSource;
import org.palladiosimulator.simexp.pcm.datasource.EDP2DataSource;
import org.palladiosimulator.simexp.pcm.datasource.MeasurementSeriesResult;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;
import org.palladiosimulator.simexp.pcm.state.failure.NodeFailureStateCreator;
import org.palladiosimulator.simexp.pcm.state.failure.NodeFailureTypeCreator;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;

public class PerformabilityPcmExperienceSimulationRunner implements ExperienceSimulationRunner {
    
    private static final Logger LOGGER = Logger.getLogger(PerformabilityPcmExperienceSimulationRunner.class.getName());

    private final DataSource dataSource;
    private final ExperimentProvider experimentProvider;
    
    private NodeFailureTypeCreator failureTypeCeator;
    private NodeFailureStateCreator failureStateCreator;

    public PerformabilityPcmExperienceSimulationRunner() {
        this(new EDP2DataSource(), ExperimentProvider.get());
    }

    public PerformabilityPcmExperienceSimulationRunner(DataSource dataSource, ExperimentProvider experimentProvider) {
        this.dataSource = dataSource; 
        this.experimentProvider = experimentProvider;
        
        failureTypeCeator = new NodeFailureTypeCreator();
        failureStateCreator = new NodeFailureStateCreator();
    }

    
    @Override
    public void simulate(SelfAdaptiveSystemState<?> sasState) {
        LOGGER.info(String.format("Simulate performability analysis for state %s", sasState.toString()));

        try {
            // create and inject failure models into blackboard partition
            FailureTypeRepository failureTypeRepo = failureTypeCeator.create();
            FailureScenarioRepository failureScenarioRepo = failureStateCreator.createRepo();
            ExperimentProvider.get().getExperimentRunner().injectFailureScenario(failureScenarioRepo, failureTypeRepo);
            LOGGER.info("Initialized failure scenario model.");
        } catch (IOException e) {
            LOGGER.error("Failed to inject failurescenario models into blackboard partition", e);
        }
        
        experimentProvider.getExperimentRunner().runExperiment();
        LOGGER.info("Finished experiment run.");
        
        retrieveStateQuantities(asPcmState(sasState));
        LOGGER.info("Retrieved state quantities and aggregated measurements.");
    }
    

    private PcmSelfAdaptiveSystemState asPcmState(SelfAdaptiveSystemState<?> sasState) {
        if (sasState instanceof PcmSelfAdaptiveSystemState) {
            return (PcmSelfAdaptiveSystemState) sasState;
        }

        // TODO exception handling
        throw new RuntimeException("");
    }


    private void retrieveStateQuantities(PcmSelfAdaptiveSystemState sasState) {
        ExperimentRunner expRunner = experimentProvider.getExperimentRunner();
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
