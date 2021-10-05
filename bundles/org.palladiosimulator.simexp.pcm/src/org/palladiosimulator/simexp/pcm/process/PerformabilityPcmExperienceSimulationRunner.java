package org.palladiosimulator.simexp.pcm.process;

import java.io.IOException;

import org.palladiosimulator.failuremodel.failurescenario.FailureScenarioRepository;
import org.palladiosimulator.failuremodel.failurescenario.FailurescenarioPackage;
import org.palladiosimulator.failuremodel.failuretype.FailureTypeRepository;
import org.palladiosimulator.failuremodel.failuretype.FailuretypePackage;
import org.palladiosimulator.simexp.pcm.datasource.DataSource;
import org.palladiosimulator.simexp.pcm.datasource.EDP2DataSource;
import org.palladiosimulator.simexp.pcm.state.failure.NodeFailureStateCreator;
import org.palladiosimulator.simexp.pcm.state.failure.NodeFailureTypeCreator;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;

public class PerformabilityPcmExperienceSimulationRunner extends PcmExperienceSimulationRunner { //implements ExperienceSimulationRunner, Initializable {
    
//    private static final Logger LOGGER = Logger.getLogger(PerformabilityPcmExperienceSimulationRunner.class.getName());

//    private final DataSource dataSource;
//    private final ExperimentProvider experimentProvider;
    
    private NodeFailureTypeCreator failureTypeCeator;
    private NodeFailureStateCreator failureStateCreator;

    public PerformabilityPcmExperienceSimulationRunner() {
        this(new EDP2DataSource(), ExperimentProvider.get());
    }

    public PerformabilityPcmExperienceSimulationRunner(DataSource dataSource, ExperimentProvider experimentProvider) {
        super(dataSource, experimentProvider);
//        this.dataSource = dataSource; 
//        this.experimentProvider = experimentProvider;
//        
        failureTypeCeator = new NodeFailureTypeCreator();
        failureStateCreator = new NodeFailureStateCreator();
    }

    
//    @Override
//    public void simulate(SelfAdaptiveSystemState<?> sasState) {
//        LOGGER.info(String.format("==== Run simulation for state '%s'", sasState.toString()));
//
//        experimentProvider.getExperimentRunner().runExperiment();
//        LOGGER.info("Finished experiment run.");
//        
//        retrieveStateQuantities(asPcmState(sasState));
//        LOGGER.info("Retrieved state quantities and aggregated measurements.");
//
//        LOGGER.info(String.format("==== Done. Simulation for state '%s'", sasState.toString()));
//    }
//    
//
//    private PcmSelfAdaptiveSystemState asPcmState(SelfAdaptiveSystemState<?> sasState) {
//        if (sasState instanceof PcmSelfAdaptiveSystemState) {
//            return (PcmSelfAdaptiveSystemState) sasState;
//        }
//
//        // TODO exception handling
//        throw new RuntimeException("");
//    }
//
//
//    private void retrieveStateQuantities(PcmSelfAdaptiveSystemState sasState) {
//        ExperimentRunner expRunner = experimentProvider.getExperimentRunner();
//        MeasurementSeriesResult result = dataSource.getSimulatedMeasurements(expRunner.getCurrentExperimentRuns());
//        for (PcmMeasurementSpecification each : getMeasurementSpecs(sasState.getQuantifiedState())) {
//            result.getMeasurementsSeries(each).ifPresent(series -> {
//                sasState.getQuantifiedState().setMeasurement(each.computeQuantity(series), each);
//            });
//        }
//    }
//
//    private Set<PcmMeasurementSpecification> getMeasurementSpecs(StateQuantity stateQuantity) {
//        return stateQuantity.getMeasurementSpecs().stream().filter(PcmMeasurementSpecification.class::isInstance)
//                .map(PcmMeasurementSpecification.class::cast).collect(Collectors.toSet());
//    }

    @Override
    protected void doInitialize() {
        // FIXME: check if failurescenario models are available in working partition
        // lookup failure model from blackboard partition
        
        try {
            // create and inject failure models into blackboard partition
            FailureTypeRepository failureTypeRepo = failureTypeCeator.create();
            FailureScenarioRepository failureScenarioRepo = failureStateCreator.createRepo();
            ExperimentProvider.get().getExperimentRunner().injectFailureScenario(failureScenarioRepo, failureTypeRepo);
            LOGGER.info("Initialized failure scenario models.");
        } catch (IOException e) {
            LOGGER.error("Failed to inject failurescenario models into blackboard partition", e);
        }
        
        ResourceSetPartition plainPartition = ExperimentProvider.get().getExperimentRunner().getPlainWorkingPartition();
        
        FailureScenarioRepository failureScenarioRepo = (FailureScenarioRepository) plainPartition.getElement(FailurescenarioPackage.eINSTANCE.getFailureScenarioRepository()).get(0);;
        FailureTypeRepository failureTypeRepo = (FailureTypeRepository) plainPartition.getElement(FailuretypePackage.eINSTANCE.getFailureTypeRepository()).get(0);
        
        assert failureScenarioRepo != null;
        assert failureTypeRepo != null;
    }
    
}
