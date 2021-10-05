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
    
    private NodeFailureTypeCreator failureTypeCeator;
    private NodeFailureStateCreator failureStateCreator;

    public PerformabilityPcmExperienceSimulationRunner() {
        this(new EDP2DataSource(), ExperimentProvider.get());
    }

    public PerformabilityPcmExperienceSimulationRunner(DataSource dataSource, ExperimentProvider experimentProvider) {
        super(dataSource, experimentProvider);
        failureTypeCeator = new NodeFailureTypeCreator();
        failureStateCreator = new NodeFailureStateCreator();
    }

    
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
